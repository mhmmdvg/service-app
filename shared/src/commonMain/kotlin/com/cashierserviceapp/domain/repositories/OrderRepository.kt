package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.OrderDetailItem
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
import com.cashierserviceapp.domain.models.OrderTracking

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>

    /** Completed orders, newest first. */
    suspend fun getOrderHistory(): Result<List<Order>>

    suspend fun createOrder(request: CreateOrderRequest): Result<CreateOrderResponse>

    /** One order in full, by id. */
    suspend fun getOrderDetail(orderId: String): Result<OrderDetail>

    /** Moves one device along, and/or prices it. */
    suspend fun updateOrderItem(
        orderItemId: String,
        request: UpdateOrderItemRequest,
    ): Result<OrderDetailItem>

    /** Resolves a receipt QR token to that order's progress. */
    suspend fun trackOrder(qrToken: String): Result<OrderTracking>
}