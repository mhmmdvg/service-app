package com.cashierserviceapp.data.remote.api

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.domain.network.OrderApi
import com.cashierserviceapp.utils.Logger
import com.cashierserviceapp.utils.apiUrl
import com.cashierserviceapp.utils.safeApiCall
import com.cashierserviceapp.utils.tagged
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class OrderApiImpl(
    private val client: HttpClient,
    private val logger: Logger
) : OrderApi {
    private val taggedLogger = logger.tagged("OrderApi")

    override suspend fun getOrders(): HttpResponse<List<Order>>? = safeApiCall(taggedLogger) {
        client.get { apiUrl("orders/in-progress") }.body()
    }

    override suspend fun getOrderHistory(): HttpResponse<List<Order>>? = safeApiCall(taggedLogger) {
        client.get { apiUrl("orders/history") }.body()
    }

    override suspend fun getOrderDetail(orderId: String): HttpResponse<OrderDetail>? =
        safeApiCall(taggedLogger) {
            client.get { apiUrl("orders/${orderId.encodeURLPathPart()}") }.body()
        }

    override suspend fun trackOrder(qrToken: String): HttpResponse<OrderTracking>? =
        safeApiCall(taggedLogger) {
            client.get { apiUrl("track/${qrToken.encodeURLPathPart()}") }.body()
        }

    override suspend fun createOrder(
        request: CreateOrderRequest
    ): HttpResponse<CreateOrderResponse>? = safeApiCall(taggedLogger) {
        client.post {
            apiUrl("orders")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}