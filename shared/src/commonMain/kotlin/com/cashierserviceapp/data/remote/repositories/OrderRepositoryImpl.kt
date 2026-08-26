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

        // A search or a later page is a subset of the queue, not the queue — answering one from
        // the cache would hand the search screen every order it has ever seen.
        if (!params.isWholeQueue) return refresh

        return merge(cached(OrderStatus.IN_PROGRESS), refresh)
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

    override suspend fun getOrderDetail(orderId: String): Result<OrderDetail> = apiCatching {
        val response = api.getOrderDetail(orderId) ?: unreachable()

        response.data ?: throw Exception(response.message)
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

/** True for the plain in-progress queue — the only response that is a complete set. */
private val QueryParams.isWholeQueue: Boolean
    get() = search.isNullOrBlank() && page == null
