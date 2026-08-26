package com.cashierserviceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.cashierserviceapp.data.local.entities.OrderEntity
import com.cashierserviceapp.domain.models.OrderStatus
import kotlinx.coroutines.flow.Flow

/**
 * One table behind both order lists.
 *
 * `/orders/in-progress` and `/orders/history` return the same `OrderSummaryDTO`, differing only in
 * its `status` — `inProgress` for the queue, `completed` for the history. That column partitions
 * the cache, so every read and write here is scoped by it.
 *
 * Sharing the table isn't only a saving. `id` is the primary key, so an order that finishes is the
 * *same row* crossing the partition: it leaves the queue and joins the history in a single upsert,
 * with no chance of two caches disagreeing about where it belongs.
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

    @Query("DELETE FROM orders WHERE status = :status")
    suspend fun clear(status: OrderStatus)

    /** Both halves — for logout, where the whole cache has to go. */
    @Query("DELETE FROM orders")
    suspend fun clearAll()

    /**
     * Replaces one side of the partition, leaving the other untouched.
     *
     * Each list is a complete set as far as its screen is concerned, so a refresh replaces it
     * rather than merging into it — an order that has moved on no longer comes back from its
     * endpoint, and an upsert alone would leave it in the list forever.
     *
     * Scoping the delete to [status] is what makes the shared table safe: refreshing the queue
     * must not take the history down with it.
     */
    @Transaction
    suspend fun replaceAll(status: OrderStatus, orders: List<OrderEntity>) {
        clear(status)
        upsertOrders(orders)
    }
}
