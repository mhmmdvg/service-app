package com.cashierserviceapp.screens.home

import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.utils.formatRupiah
import com.cashierserviceapp.utils.parseTimestamp
import com.cashierserviceapp.utils.toLocalDateTime
import kotlinx.datetime.LocalDate

/** How many of the waiting orders the home screen shows before deferring to the Order tab. */
const val ATTENTION_PREVIEW_COUNT = 8

/**
 * One order as a list row, with the waiting time already worked out.
 *
 * Shared with the search screen so a customer looks identical whether scrolled to or searched for.
 * Search runs against `/orders`, which mixes in finished work — hence [status].
 *
 * @param daysWaiting whole days since the order was taken in, or null if the timestamp didn't
 *   parse. Drives both the sort order and the badge.
 * @param status `IN_PROGRESS` or `COMPLETED` — all the summary endpoints report. A finished order
 *   has stopped waiting, so it is neither overdue nor labelled with a wait.
 */
data class AttentionRow(
    val id: String,
    val customerName: String,
    val orderCode: String,
    val itemsCount: Int,
    val totalLabel: String,
    val daysWaiting: Int?,
    val status: OrderStatus = OrderStatus.IN_PROGRESS,
) {
    val isCompleted: Boolean get() = status == OrderStatus.COMPLETED

    /** Orders sitting for a few days are the ones a cashier needs nudging about. */
    val isOverdue: Boolean get() = !isCompleted && (daysWaiting ?: 0) >= OVERDUE_DAYS

    companion object {
        const val OVERDUE_DAYS = 3
    }
}

/** Everything the home screen renders, derived once per load rather than per recomposition. */
data class HomeSnapshot(
    /** Longest wait first — the queue as a cashier would work it. */
    val attention: List<AttentionRow>,
    /**
     * How many orders are waiting in total, from the server's `page_info` — not `attention.size`,
     * which only counts the pages loaded. Null until the first page answers.
     */
    val totalCount: Int? = null,
    /**
     * Total taken across all completed work, formatted. Null until the server can report it —
     * there's no endpoint for it yet, and summing `/orders/history` client-side would mean pulling
     * the whole archive down to add up one number.
     */
    val incomeLabel: String? = null,
) {
    val orderCount: Int get() = totalCount ?: attention.size
}

fun buildHomeSnapshot(
    orders: List<Order>,
    today: LocalDate,
    totalCount: Int? = null,
): HomeSnapshot = HomeSnapshot(
    attention = orders.toRows(today),
    totalCount = totalCount,
)

fun List<Order>.toRows(today: LocalDate): List<AttentionRow> = map { it.toAttentionRow(today) }
    // Longest wait first; anything undated sorts to the end rather than jumping the queue.
    .sortedByDescending { it.daysWaiting ?: -1 }

/** Search maps with this directly, to keep the server's ordering instead of [toRows]' sort. */
fun Order.toAttentionRow(today: LocalDate): AttentionRow {
    val createdDate = parseTimestamp(createdAt)?.toLocalDateTime()?.date

    return AttentionRow(
        id = id,
        customerName = customerName,
        orderCode = orderCode,
        itemsCount = itemsCount,
        totalLabel = formatRupiah(totalCost),
        daysWaiting = createdDate?.let { (today.toEpochDays() - it.toEpochDays()).toInt() },
        status = status,
    )
}
