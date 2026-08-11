package com.cashierserviceapp.data.remote.api

import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.network.OrderApi
import com.cashierserviceapp.utils.apiUrl
import com.cashierserviceapp.utils.safeApiCall
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class OrderApiImpl(
    private val client: HttpClient
) : OrderApi {
    override suspend fun getOrders(): HttpResponse<List<Order>>? = safeApiCall {
        client.get { apiUrl("orders") }.body()
    }
}