package com.cashierserviceapp.domain.models

import kotlinx.serialization.Serializable

/**
 * `OrderSummaryDTO` — the row shape shared by the in-progress and history lists.
 *
 * [createdAt] arrives as an ISO-8601 instant in UTC (`2026-08-13T17:16:47Z`) and is kept as text
 * here; the screens that show it parse and localise it themselves.
 */
@Serializable
data class Order(
    val id: String,
    val status: OrderStatus,
    val customerName: String,
    val orderCode: String,
    val createdAt: String,
    // Int64 server-side, so Long here: rupiah totals outgrow Int sooner than you'd think.
    val totalCost: Long,
    val itemsCount: Int,
)

