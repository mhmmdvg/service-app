package com.cashierserviceapp.screens.history

import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.utils.formatLongDate
import com.cashierserviceapp.utils.formatRupiah
import com.cashierserviceapp.utils.formatTime
import com.cashierserviceapp.utils.parseTimestamp
import com.cashierserviceapp.utils.toLocalDateTime
import kotlinx.datetime.LocalDate

/**
 * One finished order, with its timestamp and total already turned into the strings the row shows.
 *
 * Formatting happens here rather than in the composable so parsing an ISO timestamp doesn't run
 * again on every recomposition.
 */
data class HistoryRow(
    val id: String,
    val customerName: String,
    val orderCode: String,
    val itemsCount: Int,
    val totalCost: Long,
    val totalLabel: String,
    val timeLabel: String,
)

/**
 * One local calendar day's worth of completed orders, with the day's takings summed.
 *
 * @param label what the sticky header shows — "Today", "Yesterday", or the full date.
 * @param date null for orders whose timestamp didn't parse; they collect in a trailing section
 *   rather than being dropped.
 */
data class HistorySection(
    val label: String,
    val date: LocalDate?,
    val rows: List<HistoryRow>,
) {
    val totalLabel: String = formatRupiah(rows.sumOf { it.totalCost })
}

/**
 * Groups [orders] by the local calendar day they were created on, newest day first, preserving the
 * server's ordering within each day.
 *
 * Takes [today] as an argument rather than reading the clock so the "Today"/"Yesterday" labels stay
 * a pure function of the inputs.
 */
fun groupOrdersByDay(orders: List<Order>, today: LocalDate): List<HistorySection> {
    val yesterday = LocalDate.fromEpochDays(today.toEpochDays() - 1)

    val byDay = orders.groupBy { order ->
        parseTimestamp(order.createdAt)?.toLocalDateTime()?.date
    }

    // Newest day first, with anything unparseable pushed to the end rather than dropped.
    val (dated, undated) = byDay.entries.partition { it.key != null }
    val ordered = dated.sortedByDescending { checkNotNull(it.key) } + undated

    return ordered.map { (date, dayOrders) ->
        HistorySection(
            label = when (date) {
                null -> "Undated"
                today -> "Today"
                yesterday -> "Yesterday"
                else -> date.formatLongDate()
            },
            date = date,
            rows = dayOrders.map { it.toRow() }
        )
    }
}

private fun Order.toRow(): HistoryRow = HistoryRow(
    id = id,
    customerName = customerName,
    orderCode = orderCode,
    itemsCount = itemsCount,
    totalCost = totalCost,
    totalLabel = formatRupiah(totalCost),
    timeLabel = parseTimestamp(createdAt)?.toLocalDateTime()?.formatTime().orEmpty(),
)
