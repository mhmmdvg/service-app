package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.domain.models.PageInfo
import com.cashierserviceapp.domain.models.UpdatedOrderItem
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.domain.models.QueryParams
import com.cashierserviceapp.data.local.dao.OrderDao
import com.cashierserviceapp.data.local.mappers.toDomain
import com.cashierserviceapp.data.local.mappers.toEntities
import com.cashierserviceapp.domain.network.OrderApi
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.utils.apiCatching
import com.cashierserviceapp.utils.unreachable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class OrderRepositoryImpl(
    private val api: OrderApi,
    private val orderDao: OrderDao,
) : OrderRepository {
    /**
     * The queue as cached. Ordering and contents are the DAO's business; this only maps.
     *
     * Emitted even when empty, unlike the old cache-first flow: with pagination the screen needs to
     * be told "there is nothing here" as readily as it is told what there is, and the paginator now
     * owns the loading state that used to depend on silence.
     */
    override fun observeOrders(): Flow<List<Order>> =
        orderDao.getOrders(OrderStatus.IN_PROGRESS).map { it.toDomain() }

    override fun observeOrderHistory(): Flow<List<Order>> =
        orderDao.getOrders(OrderStatus.COMPLETED).map { it.toDomain() }

    override suspend fun fetchOrders(
        params: QueryParams,
        replaceCache: Boolean,
    ): Result<PageInfo?> = fetchPage(
        status = OrderStatus.IN_PROGRESS,
        replaceCache = replaceCache,
    ) { api.getOrders(params) }

    override suspend fun fetchOrderHistory(
        params: QueryParams,
        replaceCache: Boolean,
    ): Result<PageInfo?> = fetchPage(
        status = OrderStatus.COMPLETED,
        replaceCache = replaceCache,
    ) { api.getOrderHistory(params) }

    /**
     * One page into one half of the shared table.
     *
     * Replacing and appending are the same call because the difference is only ever *when* the old
     * rows go: a first page is a new truth and supersedes what was there, a later page adds to it.
     * Both write through [OrderStatus], so neither list can disturb the other.
     */
    private suspend fun fetchPage(
        status: OrderStatus,
        replaceCache: Boolean,
        request: suspend () -> HttpResponse<List<Order>>?,
    ): Result<PageInfo?> = apiCatching {
        val response = request() ?: unreachable()

        // An empty page is a perfectly good success, so this only trips when the server actually
        // refused — in which case its own message is the useful one.
        val orders = response.data ?: throw Exception(response.message)

        if (replaceCache) {
            orderDao.replaceAll(status, orders.toEntities())
        } else {
            orderDao.upsertOrders(orders.toEntities())
        }

        response.pageInfo
    }

    override suspend fun searchOrders(params: QueryParams): Result<List<Order>> = apiCatching {
        val response = api.searchOrders(params) ?: unreachable()

        // No cache write: these rows span both halves of the table and are a filtered slice of
        // neither, so they'd corrupt whichever one they were written to.
        response.data ?: throw Exception(response.message)
    }

    /**
     * One order in full — and the moment the cached summary of it can be corrected.
     *
     * This is the only authoritative read of a single order the app makes, so it is where a status
     * change becomes visible to the lists. Finishing the last device moves the cached row from the
     * queue half of the table to the history half, and both screens redraw off their own live
     * query — no list refresh, no network call of their own.
     *
     * Write-only-if-present, so a detail opened from search can never inject an order into a list
     * that doesn't own it.
     */
    override suspend fun getOrderDetail(orderId: String): Result<OrderDetail> = apiCatching {
        val response = api.getOrderDetail(orderId) ?: unreachable()
        val detail = response.data ?: throw Exception(response.message)

        orderDao.updateSummary(
            // The id asked for, not the one echoed back: the response may leave it off.
            id = orderId,
            status = detail.summaryStatus,
            totalCost = detail.items.sumOf { it.finalCost ?: 0L },
            itemsCount = detail.items.size,
        )

        detail
    }

    override suspend fun updateOrderItem(
        orderItemId: String,
        request: UpdateOrderItemRequest,
    ): Result<UpdatedOrderItem> = apiCatching {
        val response = api.updateOrderItem(orderItemId, request)
            ?: unreachable()

        response.data ?: throw Exception(response.message)
    }

    override suspend fun trackOrder(qrToken: String): Result<OrderTracking> = apiCatching {
        val response = api.trackOrder(qrToken.trim()) ?: unreachable()

        response.data ?: throw Exception(response.message)
    }

    override suspend fun createOrder(request: CreateOrderRequest): Result<CreateOrderResponse> =
        apiCatching {
            val response = api.createOrder(request) ?: unreachable()

            // A failed status carries the server's reason — a validation message worth showing on
            // the form, not a generic error.
            response.data ?: throw Exception(response.message)
        }
}

/**
 * The status the list endpoints would report for this order, derived the same way the server does
 * it: finished only once it has devices and every one of them is done.
 *
 * An order with no devices counts as in progress — there is nothing to have finished, and
 * `orderIDs(for:)` on the server leaves it out of the history for the same reason.
 */
private val OrderDetail.summaryStatus: OrderStatus
    get() = if (items.isNotEmpty() && items.all { it.status == OrderStatus.COMPLETED }) {
        OrderStatus.COMPLETED
    } else {
        OrderStatus.IN_PROGRESS
    }
