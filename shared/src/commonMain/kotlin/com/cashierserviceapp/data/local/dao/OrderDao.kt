package com.cashierserviceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.cashierserviceapp.data.local.entities.OrderEntity
import com.cashierserviceapp.domain.models.OrderStatus
import kotlinx.coroutines.flow.Flow

/**
 * One table behind both order lists. `/orders/in-progress` and `/orders/history` return the same
 * `OrderSummaryDTO` and differ only in its `status`, so that column partitions the cache and every
 * read and write here is scoped by it.
 *
 * `id` is the primary key, so an order that finishes is the *same row* crossing the partition —
 * one upsert moves it, and the two lists can't disagree about where it belongs.
 */
@Dao
interface OrderDao {
    @Upsert
    suspend fun upsertOrder(order: OrderEntity)

    @Upsert
    suspend fun upsertOrders(orders: List<OrderEntity>)

    /** Newest first. Safe as a TEXT sort because `createdAt` is fixed-width ISO-8601 UTC. */
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrders(status: OrderStatus): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrder(id: String): OrderEntity?

    /**
     * Re-states one cached row from an authoritative read of that order.
     *
     * An `UPDATE` rather than an upsert, so it can't add an order the list endpoints never
     * returned — one reached through search belongs to neither list, and an invented row would sit
     * in the queue forever. Not cached means no-op, which is the right answer.
     */
    @Query(
        "UPDATE orders SET status = :status, totalCost = :totalCost, itemsCount = :itemsCount " +
                "WHERE id = :id"
    )
    suspend fun updateSummary(id: String, status: OrderStatus, totalCost: Long, itemsCount: Int)

    @Query("DELETE FROM orders WHERE status = :status")
    suspend fun clear(status: OrderStatus)

    /** Both halves — for logout, where the whole cache has to go. */
    @Query("DELETE FROM orders")
    suspend fun clearAll()

    /**
     * Replaces one side of the partition, leaving the other untouched.
     *
     * Each list is a complete set to its own screen, so a refresh replaces rather than merges — an
     * order that has moved on stops coming back from its endpoint, and an upsert would strand it.
     * Scoping the delete to [status] is what stops a queue refresh taking the history with it.
     */
    @Transaction
    suspend fun replaceAll(status: OrderStatus, orders: List<OrderEntity>) {
        clear(status)
        upsertOrders(orders)
    }
}
