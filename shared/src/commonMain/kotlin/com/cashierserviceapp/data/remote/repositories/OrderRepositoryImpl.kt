package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order
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
    override suspend fun getOrders(): Result<List<Order>> {
       return runCatching {
           api.getOrders().let { result ->
               result?.data ?: throw Exception(result?.message)
           }
       }.onSuccess { response ->
           print("checking $response")
           Result.success(response)
       }.onFailure { exception ->
           print("exception $exception")
           Result.failure<Order>(exception)
       }
    }

    override suspend fun getOrderHistory(): Result<List<Order>> = runCatching {
        val response = api.getOrderHistory() ?: throw Exception(UNREACHABLE_MESSAGE)

        // An empty history is a perfectly good success, so this only trips when the server
        // actually refused — in which case its own message is the useful one.
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