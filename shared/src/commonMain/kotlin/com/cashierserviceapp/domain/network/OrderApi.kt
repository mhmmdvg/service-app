package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.UpdatedOrderItem
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.domain.models.QueryParams

interface OrderApi {
    suspend fun getOrders(params: QueryParams): HttpResponse<List<Order>>?

    /**
     * Every order, finished or not, newest first — `GET /orders`.
     *
     * The one endpoint that spans both lists, which is what search needs: a cashier looking up a
     * customer doesn't know or care whether that repair is still on the bench. Matches on order
     * code, customer name, or customer phone.
     */
    suspend fun searchOrders(params: QueryParams): HttpResponse<List<Order>>?

    /** Orders whose every item is completed, newest first. */
    suspend fun getOrderHistory(params: QueryParams): HttpResponse<List<Order>>?

    suspend fun createOrder(request: CreateOrderRequest): HttpResponse<CreateOrderResponse>?

    /** One order in full, by id. */
    suspend fun getOrderDetail(orderId: String): HttpResponse<OrderDetail>?

    /** Moves one device along, and/or prices it. */
    suspend fun updateOrderItem(
        orderItemId: String,
        request: UpdateOrderItemRequest,
    ): HttpResponse<UpdatedOrderItem>?

    /** Resolves the token embedded in a receipt's QR code to that order's progress. */
    suspend fun trackOrder(qrToken: String): HttpResponse<OrderTracking>?
}
