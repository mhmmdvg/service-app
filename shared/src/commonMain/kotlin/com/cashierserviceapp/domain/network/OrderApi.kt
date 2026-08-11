package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.Order

interface OrderApi {
    suspend fun getOrders(): HttpResponse<List<Order>>?
}