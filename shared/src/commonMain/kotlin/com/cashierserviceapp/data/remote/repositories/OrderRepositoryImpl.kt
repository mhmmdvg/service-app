package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
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
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class OrderRepositoryImpl(
    private val api: OrderApi,
    private val orderDao: OrderDao,
) : OrderRepository {
    /**
     * Cache first, network second — for the unfiltered queue.
     *
     * An empty cache emits nothing at all, so the screen stays on its initial loading state rather
     * than flashing an empty queue the refresh is about to fill. Once rows exist they go out
     * immediately and again on every write, so the list never blanks mid-request.
     *
     * The refresh outcome is always emitted, success included: on a genuinely empty queue it is
     * the only thing that tells the screen to stop waiting.
     */
    override fun getOrders(params: QueryParams): Flow<Result<List<Order>>> = channelFlow {
        // A search or a later page is a subset of the queue, not the queue — emitting the cache
        // there would answer the search screen with every order it has ever seen.
        if (params.isWholeQueue) {
            launch {
                orderDao.getOrders().collect { cached ->
                    if (cached.isNotEmpty()) send(Result.success(cached.toDomain()))
                }
            }
        }

        send(refreshOrders(params))
    }

    private suspend fun refreshOrders(params: QueryParams): Result<List<Order>> = apiCatching {
        val response = api.getOrders(params) ?: unreachable()
        val orders = response.data ?: throw Exception(response.message)

        // Same reason as above: only a complete set may replace the cache.
        if (params.isWholeQueue) orderDao.replaceAll(orders.toEntities())

        orders
    }

    override suspend fun getOrderHistory(): Result<List<Order>> = apiCatching {
        val response = api.getOrderHistory() ?: unreachable()

        // An empty history is a perfectly good success, so this only trips when the server
        // actually refused — in which case its own message is the useful one.
        response.data ?: throw Exception(response.message)
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
