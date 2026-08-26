package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.OrderStatus
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class OrderRepositoryImpl(
    private val api: OrderApi,
    private val orderDao: OrderDao,
) : OrderRepository {
    /**
     * Cache first, network second — for the unfiltered queue.
     *
     * Two producers, merged: the cache re-emits on every write, the refresh emits once. Cached rows
     * therefore reach the screen without waiting for the network, and again once it has answered.
     */
    override fun getOrders(params: QueryParams): Flow<Result<List<Order>>> {
        // Always emitted, success included: on a genuinely empty queue this is the only thing that
        // tells the screen to stop waiting.
        val refresh = flow { emit(refreshOrders(params)) }

        // A later page is a subset of the queue, not the queue, so it can neither be answered
        // from the cache nor replace it. Searching has its own endpoint — see [searchOrders].
        if (!params.isWholeQueue) return refresh

        return merge(cached(OrderStatus.IN_PROGRESS), refresh)
    }

    override suspend fun searchOrders(params: QueryParams): Result<List<Order>> = apiCatching {
        val response = api.searchOrders(params) ?: unreachable()

        // No cache write: these rows span both halves of the table and are a filtered slice of
        // neither, so they'd corrupt whichever one they were written to.
        response.data ?: throw Exception(response.message)
    }

    /** The same arrangement as [getOrders], reading the completed half of the same table. */
    override fun getOrderHistory(): Flow<Result<List<Order>>> =
        merge(cached(OrderStatus.COMPLETED), flow { emit(refreshOrderHistory()) })

    /**
     * One half of the shared `orders` table, as a live query.
     *
     * An empty cache emits nothing, so the screen stays on its initial loading state instead of
     * flashing an empty list the refresh is about to fill.
     */
    private fun cached(status: OrderStatus): Flow<Result<List<Order>>> =
        orderDao.getOrders(status)
            .filter { it.isNotEmpty() }
            .map { Result.success(it.toDomain()) }

    private suspend fun refreshOrders(params: QueryParams): Result<List<Order>> = apiCatching {
        val response = api.getOrders(params) ?: unreachable()
        val orders = response.data ?: throw Exception(response.message)

        // Same reason as above: only a complete set may replace its half of the cache.
        if (params.isWholeQueue) orderDao.replaceAll(OrderStatus.IN_PROGRESS, orders.toEntities())

        orders
    }

    override suspend fun refreshOrderHistory(): Result<List<Order>> = apiCatching {
        val response = api.getOrderHistory() ?: unreachable()

        // An empty history is a perfectly good success, so this only trips when the server
        // actually refused — in which case its own message is the useful one.
        val orders = response.data ?: throw Exception(response.message)

        // The screen only ever asks for the first page, so that page *is* the complete set from
        // its point of view, and may replace the completed rows wholesale.
        orderDao.replaceAll(OrderStatus.COMPLETED, orders.toEntities())

        orders
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

/** True for the plain in-progress queue — the only response that is a complete set. */
private val QueryParams.isWholeQueue: Boolean
    get() = search.isNullOrBlank() && page == null
