package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.UpdatedOrderItem
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.domain.models.QueryParams
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(params: QueryParams = QueryParams()): Flow<Result<List<Order>>>

    /**
     * Every order, finished or not, matching [QueryParams.search] — newest first.
     *
     * Never cached: it's a filtered slice of the whole archive, not a list any screen owns, and it
     * is answered by the server rather than by matching rows locally, so it also finds customers by
     * phone number.
     */
    suspend fun searchOrders(params: QueryParams): Result<List<Order>>

    /** Completed orders, newest first — cache first, on the same terms as [getOrders]. */
    fun getOrderHistory(): Flow<Result<List<Order>>>

    /**
     * One network round-trip for the history that writes its result to the cache, for
     * pull-to-refresh.
     *
     * A caller already collecting [getOrderHistory] sees the new rows arrive through that flow, so
     * the returned [Result] is only worth reading for its failure.
     */
    suspend fun refreshOrderHistory(): Result<List<Order>>

    suspend fun createOrder(request: CreateOrderRequest): Result<CreateOrderResponse>

    /** One order in full, by id. */
    suspend fun getOrderDetail(orderId: String): Result<OrderDetail>

    /** Moves one device along, and/or prices it. */
    suspend fun updateOrderItem(
        orderItemId: String,
        request: UpdateOrderItemRequest,
    ): Result<UpdatedOrderItem>

    /** Resolves a receipt QR token to that order's progress. */
    suspend fun trackOrder(qrToken: String): Result<OrderTracking>
}