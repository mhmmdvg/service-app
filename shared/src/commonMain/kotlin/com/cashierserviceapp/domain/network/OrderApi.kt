package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.Order

interface OrderApi {
    suspend fun getOrders(): HttpResponse<List<Order>>?

    suspend fun createOrder(request: CreateOrderRequest): HttpResponse<CreateOrderResponse>?
}
