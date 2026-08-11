package com.cashierserviceapp.data.remote.repositories

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
           print(response)
           Result.success(response)
       }.onFailure { exception ->
           print(exception)
           Result.failure<Order>(exception)
       }
    }
}