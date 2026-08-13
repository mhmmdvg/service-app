package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>

    /** Completed orders, newest first. */
    suspend fun getOrderHistory(): Result<List<Order>>

    suspend fun createOrder(request: CreateOrderRequest): Result<CreateOrderResponse>
}