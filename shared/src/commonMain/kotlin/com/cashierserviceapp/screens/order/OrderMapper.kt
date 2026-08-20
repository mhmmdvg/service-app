package com.cashierserviceapp.screens.order

import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.utils.formatRelativeTimestamp
import kotlinx.datetime.LocalDate

/**
 * One in-progress order as the list shows it, with its timestamp already turned into the string the
 * row displays — same reason as [com.cashierserviceapp.screens.history.HistoryRow]: parsing an ISO
 * instant shouldn't run again on every recomposition.
 */
data class OrderRow(
    val id: String,
    val customerName: String,
    val orderCode: String,
    val itemsCount: Int,
    val status: OrderStatus,
    val timeLabel: String,
)

suspend fun List<Order>.toOrderRows(today: LocalDate): List<OrderRow> = map { order ->
    OrderRow(
        id = order.id,
        customerName = order.customerName,
        orderCode = order.orderCode,
        itemsCount = order.itemsCount,
        status = order.status,
        timeLabel = formatRelativeTimestamp(order.createdAt, today),
    )
}
