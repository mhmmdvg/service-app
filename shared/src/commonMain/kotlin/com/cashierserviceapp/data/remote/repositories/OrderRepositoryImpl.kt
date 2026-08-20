package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.OrderDetailItem
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.domain.models.QueryParams
import com.cashierserviceapp.domain.network.OrderApi
import com.cashierserviceapp.domain.repositories.OrderRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class OrderRepositoryImpl(
    private val api: OrderApi
) : OrderRepository {
    override suspend fun getOrders(params: QueryParams): Result<List<Order>> {
       return runCatching {
           api.getOrders(params).let { result ->
               result?.data ?: throw Exception(result?.message)
           }
       }.onSuccess { response ->
           Result.success(response)
       }.onFailure { exception ->
           Result.failure<Order>(exception)
       }
    }

    override suspend fun getOrderHistory(): Result<List<Order>> = runCatching {
        val response = api.getOrderHistory() ?: throw Exception(UNREACHABLE_MESSAGE)

        // An empty history is a perfectly good success, so this only trips when the server
        // actually refused — in which case its own message is the useful one.
        response.data ?: throw Exception(response.message)
    }

    override suspend fun getOrderDetail(orderId: String): Result<OrderDetail> = runCatching {
        val response = api.getOrderDetail(orderId) ?: throw Exception(UNREACHABLE_MESSAGE)

        response.data ?: throw Exception(response.message)
    }

    override suspend fun updateOrderItem(
        orderItemId: String,
        request: UpdateOrderItemRequest,
    ): Result<OrderDetailItem> = runCatching {
        val response = api.updateOrderItem(orderItemId, request)
            ?: throw Exception(UNREACHABLE_MESSAGE)

        response.data ?: throw Exception(response.message)
    }

    override suspend fun trackOrder(qrToken: String): Result<OrderTracking> = runCatching {
        val response = api.trackOrder(qrToken.trim()) ?: throw Exception(UNREACHABLE_MESSAGE)

        response.data ?: throw Exception(response.message)
    }

    override suspend fun createOrder(request: CreateOrderRequest): Result<CreateOrderResponse> =
        runCatching {
            val response = api.createOrder(request) ?: throw Exception(UNREACHABLE_MESSAGE)

            // A failed status carries the server's reason — a validation message worth showing on
            // the form, not a generic error.
            response.data ?: throw Exception(response.message)
        }
}

/** Nothing came back at all: no envelope to read a reason out of, so the network is the suspect. */
private const val UNREACHABLE_MESSAGE =
    "Couldn't reach the server. Check your connection and try again."