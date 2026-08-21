package com.cashierserviceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.cashierserviceapp.data.local.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Upsert
    suspend fun upsertOrder(order: OrderEntity)

    @Upsert
    suspend fun upsertOrders(orders: List<OrderEntity>)

    /** Newest first. Safe as a TEXT sort because `createdAt` is fixed-width ISO-8601 UTC. */
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrder(id: String): OrderEntity?

    @Query("DELETE FROM orders")
    suspend fun clear()

    /**
     * The in-progress queue is a complete set, so a refresh replaces it rather than merging into
     * it — an order that has moved on no longer comes back from `/orders/in-progress`, and an
     * upsert would leave it in the list forever.
     */
    @Transaction
    suspend fun replaceAll(orders: List<OrderEntity>) {
        clear()
        upsertOrders(orders)
    }
}
