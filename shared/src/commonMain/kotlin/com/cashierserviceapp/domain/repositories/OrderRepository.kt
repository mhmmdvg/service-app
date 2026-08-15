package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.OrderTracking

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>

    /** Completed orders, newest first. */
    suspend fun getOrderHistory(): Result<List<Order>>

    suspend fun createOrder(request: CreateOrderRequest): Result<CreateOrderResponse>

    /** One order in full, by id. */
    suspend fun getOrderDetail(orderId: String): Result<OrderDetail>

    /** Resolves a receipt QR token to that order's progress. */
    suspend fun trackOrder(qrToken: String): Result<OrderTracking>
}