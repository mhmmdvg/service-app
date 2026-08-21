package com.cashierserviceapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cashierserviceapp.domain.models.OrderStatus

/**
 * The cached form of [com.cashierserviceapp.domain.models.Order], field-for-field.
 *
 * Nothing here is formatted for display: [createdAt] keeps the server's ISO-8601 instant rather than
 * a `timeLabel`, so the cache doesn't go stale overnight or freeze into whichever language happened
 * to be active when the row was written. The screens derive their labels per read — see
 * [com.cashierserviceapp.screens.order.toOrderRows].
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val status: OrderStatus,
    val customerName: String,
    val orderCode: String,
    /** ISO-8601 UTC (`2026-08-13T17:16:47Z`) — fixed-width, so TEXT ordering is chronological. */
    val createdAt: String,
    val totalCost: Long,
    val itemsCount: Int,
)
