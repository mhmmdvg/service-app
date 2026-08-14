package com.cashierserviceapp.screens.home

import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.utils.formatRupiah
import com.cashierserviceapp.utils.parseTimestamp
import com.cashierserviceapp.utils.toLocalDateTime
import kotlinx.datetime.LocalDate

/** How many of the waiting orders the home screen shows before deferring to the Order tab. */
const val ATTENTION_PREVIEW_COUNT = 3

/**
 * One in-progress order as a list row, with the waiting time already worked out.
 *
 * Shared with the search screen on purpose: both are looking at the same in-progress list, and a
 * customer should look identical whether they were scrolled to or searched for.
 *
 * @param daysWaiting whole days since the order was taken in, or null if the timestamp didn't
 *   parse. Drives both the sort order and the badge.
 */
data class AttentionRow(
    val id: String,
    val customerName: String,
    val orderCode: String,
    val itemsCount: Int,
    val totalLabel: String,
    val daysWaiting: Int?,
) {
    /** Orders sitting for a few days are the ones a cashier needs nudging about. */
    val isOverdue: Boolean get() = (daysWaiting ?: 0) >= OVERDUE_DAYS

    companion object {
        const val OVERDUE_DAYS = 3
    }
}

/** Everything the home screen renders, derived once per load rather than per recomposition. */
data class HomeSnapshot(
    /** Longest wait first — the queue as a cashier would work it. */
    val attention: List<AttentionRow>,
    /**
     * Total taken across all completed work, formatted. Null until the server can report it —
     * there's no endpoint for it yet, and summing `/orders/history` client-side would mean pulling
     * the whole archive down to add up one number.
     */
    val incomeLabel: String? = null,
) {
    val orderCount: Int get() = attention.size
}

fun buildHomeSnapshot(orders: List<Order>, today: LocalDate): HomeSnapshot = HomeSnapshot(
    attention = orders.toRows(today)
)

/** Case-insensitive match on customer name or order code. */
fun List<AttentionRow>.search(query: String): List<AttentionRow> {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return emptyList()

    return filter { row ->
        row.customerName.contains(trimmed, ignoreCase = true) ||
                row.orderCode.contains(trimmed, ignoreCase = true)
    }
}

fun List<Order>.toRows(today: LocalDate): List<AttentionRow> = this
    .map { it.toAttentionRow(today) }
    // Longest wait first; anything undated sorts to the end rather than jumping the queue.
    .sortedByDescending { it.daysWaiting ?: -1 }

private fun Order.toAttentionRow(today: LocalDate): AttentionRow {
    val createdDate = parseTimestamp(createdAt)?.toLocalDateTime()?.date

    return AttentionRow(
        id = id,
        customerName = customerName,
        orderCode = orderCode,
        itemsCount = itemsCount,
        totalLabel = formatRupiah(totalCost),
        daysWaiting = createdDate?.let { (today.toEpochDays() - it.toEpochDays()).toInt() },
    )
}
