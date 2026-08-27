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
    /** Longest wait first, capped at [ATTENTION_PREVIEW_COUNT] — all the screen ever draws. */
    val attention: List<AttentionRow> = emptyList(),
    /**
     * How many orders are waiting in total, from the server's `page_info` — not `attention.size`,
     * which is capped. Null until the first page answers, so [cachedCount] stands in.
     */
    val totalCount: Int? = null,
    /** How many the cache holds. Keeps the count honest before the server's total arrives. */
    val cachedCount: Int = attention.size,
    /**
     * Total taken across all completed work, formatted. Null until the server can report it —
     * there's no endpoint for it yet, and summing `/orders/history` client-side would mean pulling
     * the whole archive down to add up one number.
     */
    val incomeLabel: String? = null,
) {
    val orderCount: Int get() = totalCount ?: cachedCount
}

/**
 * Longest wait first, trimmed to what the screen shows.
 *
 * Ordered on the raw `createdAt` text rather than on parsed dates: it is fixed-width ISO-8601 UTC,
 * so it sorts chronologically as-is — the same property [com.cashierserviceapp.data.local.dao.OrderDao]
 * relies on. That keeps the trim ahead of the mapping, so timestamps and money are formatted for the
 * handful of rows drawn instead of for the whole cache, which grows every time the Order tab pages.
 */
fun List<Order>.toHomeSnapshot(today: LocalDate, totalCount: Int? = null) = HomeSnapshot(
    attention = sortedBy { it.createdAt }
        .take(ATTENTION_PREVIEW_COUNT)
        .map { it.toAttentionRow(today) },
    totalCount = totalCount,
    cachedCount = size,
)

/** Search maps with this directly, to keep the server's ordering instead of the queue's. */
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
