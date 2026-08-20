package com.cashierserviceapp.screens.orderdetail

import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.models.OrderDetailItem
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.utils.formatDate
import com.cashierserviceapp.utils.formatRupiah
import com.cashierserviceapp.utils.formatTime
import com.cashierserviceapp.utils.parseTimestamp
import com.cashierserviceapp.utils.toLocalDateTime

/**
 * The detail screen's view of one order, with money and timestamps already formatted.
 *
 * Same reasoning as the list rows: formatting here keeps ISO parsing and digit grouping out of
 * recomposition, and keeps the composables free of business rules like "an order is unpriced until
 * every device has a final cost".
 */
data class OrderDetailUiModel(
    val orderCode: String,
    val qrToken: String,
    val customerName: String,
    val customerPhone: String?,
    val cashierName: String?,
    val createdLabel: String?,
    val totalLabel: String,
    /** True when nothing has been priced yet, so the header shows a dash rather than Rp 0. */
    val isUnpriced: Boolean,
    val items: List<OrderDetailItemUiModel>,
) {
    val deviceCount: Int get() = items.size

    /**
     * The order's overall state, taken as the least-finished device — an order isn't done until
     * every device is, which is exactly how the server decides what counts as history.
     */
    val status: OrderStatus
        get() = items.minByOrNull { it.status.ordinal }?.status ?: OrderStatus.RECEIVED
}

data class OrderDetailItemUiModel(
    val id: String,
    val deviceName: String,
    val complaint: String?,
    val status: OrderStatus,
    /** The raw fee, kept alongside the label so the status sheet can put it back in its field. */
    val serviceFee: Long?,
    val serviceFeeLabel: String?,
    val totalLabel: String?,
    val parts: List<OrderPartUiModel>,
)

data class OrderPartUiModel(
    val id: String,
    val name: String,
    val qty: Int,
    val subtotalLabel: String,
)

suspend fun OrderDetail.toUiModel(): OrderDetailUiModel {
    val moment = createdAt?.let { parseTimestamp(it)?.toLocalDateTime() }
    // Only devices with a settled price count towards the total; the rest are still open.
    val pricedCosts = items.mapNotNull { it.finalCost }

    return OrderDetailUiModel(
        orderCode = orderCode,
        qrToken = qrToken,
        customerName = customerName,
        customerPhone = customerPhone.takeIf { it.isNotBlank() },
        cashierName = cashierName.takeIf { it.isNotBlank() },
        createdLabel = moment?.let { "${it.date.formatDate()}, ${it.formatTime()}" },
        totalLabel = formatRupiah(pricedCosts.sum()),
        isUnpriced = pricedCosts.isEmpty(),
        items = items.mapIndexed { index, item -> item.toUiModel(index) },
    )
}

private fun OrderDetailItem.toUiModel(index: Int): OrderDetailItemUiModel =
    OrderDetailItemUiModel(
        // The server can leave an id off; the position is a stable enough fallback for a list key
        // that never reorders within one response.
        id = id ?: "item-$index",
        deviceName = "$deviceBrand $deviceModel",
        complaint = complaint?.takeIf { it.isNotBlank() },
        status = status,
        serviceFee = serviceFee,
        serviceFeeLabel = serviceFee?.let { formatRupiah(it) },
        totalLabel = finalCost?.let { formatRupiah(it) },
        parts = parts.mapIndexed { partIndex, part ->
            OrderPartUiModel(
                id = part.id ?: "part-$index-$partIndex",
                name = part.sparePartName,
                qty = part.qty,
                subtotalLabel = formatRupiah(part.subtotal),
            )
        },
    )
