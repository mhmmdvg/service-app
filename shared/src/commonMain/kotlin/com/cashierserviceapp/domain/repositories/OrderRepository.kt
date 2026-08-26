package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.PageInfo
import com.cashierserviceapp.domain.models.UpdatedOrderItem
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.domain.models.QueryParams
import kotlinx.coroutines.flow.Flow

/**
 * Reading a list and filling it are separate here, because pagination makes them separate concerns.
 *
 * `observe*` is the list: a live query over the cache that emits every page written so far, and
 * again on any later edit. `fetch*` is one page, written into that cache — what it returns is the
 * [PageInfo] a paginator needs to know whether to ask for more, not the rows themselves. Screens
 * therefore render from one source no matter how many pages have arrived.
 */
interface OrderRepository {
    /** The in-progress queue, as cached — every page loaded so far, newest first. */
    fun observeOrders(): Flow<List<Order>>

    /** Completed orders, as cached — the history's equivalent of [observeOrders]. */
    fun observeOrderHistory(): Flow<List<Order>>

    /**
     * Fetches one page of the queue into the cache.
     *
     * @param replaceCache true drops the cached queue before writing, so the first page of a fresh
     *   load or a pull-to-refresh cannot leave rows behind that the server has since dropped. Pass
     *   false to add to what's cached — see [observeOrders]' note about who owns the queue.
     */
    suspend fun fetchOrders(
        params: QueryParams = QueryParams(),
        replaceCache: Boolean = false,
    ): Result<PageInfo?>

    /** [fetchOrders] for the history list. */
    suspend fun fetchOrderHistory(
        params: QueryParams = QueryParams(),
        replaceCache: Boolean = false,
    ): Result<PageInfo?>

    /**
     * Every order, finished or not, matching [QueryParams.search] — newest first.
     *
     * Never cached: it's a filtered slice of the whole archive, not a list any screen owns, and it
     * is answered by the server rather than by matching rows locally, so it also finds customers by
     * phone number.
     */
    suspend fun searchOrders(params: QueryParams): Result<List<Order>>

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
