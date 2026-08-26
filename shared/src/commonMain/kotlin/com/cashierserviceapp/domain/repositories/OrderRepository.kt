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
 * Reading a list and filling it are separate concerns once it pages.
 *
 * `observe*` is the list — a live query over the cache. `fetch*` is one page written into it,
 * returning the [PageInfo] a paginator needs rather than the rows, so a screen renders from one
 * source however many pages have arrived.
 */
interface OrderRepository {
    /** The in-progress queue, as cached — every page loaded so far, newest first. */
    fun observeOrders(): Flow<List<Order>>

    /** Completed orders, as cached — the history's equivalent of [observeOrders]. */
    fun observeOrderHistory(): Flow<List<Order>>

    /**
     * Fetches one page of the queue into the cache.
     *
     * @param replaceCache drops the cached queue first, so a first page can't strand rows the
     *   server has since dropped. False adds to what's already there.
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
     * Never cached: a filtered slice of the archive, owned by no screen. The server does the
     * matching, so it finds customers by phone as well as by name and code.
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
