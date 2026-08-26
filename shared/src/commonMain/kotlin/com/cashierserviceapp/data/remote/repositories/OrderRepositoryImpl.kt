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
     * The queue as cached — ordering and contents are the DAO's business, this only maps.
     *
     * Emits when empty too: the screen has to be told there is nothing here, and the paginator now
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
     * One page into one half of the shared table. A first page supersedes what was there, a later
     * page adds to it; both are scoped by [status], so neither list disturbs the other.
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
     * One order in full — and the only authoritative read of a single order, so it is where a
     * status change reaches the lists. Finishing the last device moves the cached row from the
     * queue half to the history half, and both screens redraw off their own live query.
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
 * The status the list endpoints would report, derived as the server does it: finished only once it
 * has devices and all of them are done. No devices counts as in progress — nothing to have
 * finished, which is why the server leaves those out of the history too.
 */
private val OrderDetail.summaryStatus: OrderStatus
    get() = if (items.isNotEmpty() && items.all { it.status == OrderStatus.COMPLETED }) {
        OrderStatus.COMPLETED
    } else {
        OrderStatus.IN_PROGRESS
    }
