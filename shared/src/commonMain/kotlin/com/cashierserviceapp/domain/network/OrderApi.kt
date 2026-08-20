package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.OrderDetailItem
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.domain.models.QueryParams

interface OrderApi {
    suspend fun getOrders(params: QueryParams): HttpResponse<List<Order>>?

    /** Orders whose every item is completed, newest first. */
    suspend fun getOrderHistory(): HttpResponse<List<Order>>?

    suspend fun createOrder(request: CreateOrderRequest): HttpResponse<CreateOrderResponse>?

    /** One order in full, by id. */
    suspend fun getOrderDetail(orderId: String): HttpResponse<OrderDetail>?

    /** Moves one device along, and/or prices it. */
    suspend fun updateOrderItem(
        orderItemId: String,
        request: UpdateOrderItemRequest,
    ): HttpResponse<OrderDetailItem>?

    /** Resolves the token embedded in a receipt's QR code to that order's progress. */
    suspend fun trackOrder(qrToken: String): HttpResponse<OrderTracking>?
}
