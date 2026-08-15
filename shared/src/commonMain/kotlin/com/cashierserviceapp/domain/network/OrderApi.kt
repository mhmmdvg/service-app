package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.OrderTracking

interface OrderApi {
    suspend fun getOrders(): HttpResponse<List<Order>>?

    /** Orders whose every item is completed, newest first. */
    suspend fun getOrderHistory(): HttpResponse<List<Order>>?

    suspend fun createOrder(request: CreateOrderRequest): HttpResponse<CreateOrderResponse>?

    /** One order in full, by id. */
    suspend fun getOrderDetail(orderId: String): HttpResponse<OrderDetail>?

    /** Resolves the token embedded in a receipt's QR code to that order's progress. */
    suspend fun trackOrder(qrToken: String): HttpResponse<OrderTracking>?
}
