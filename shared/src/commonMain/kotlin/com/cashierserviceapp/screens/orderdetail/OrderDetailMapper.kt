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
    val items: List<OrderDetailItemUiModel>,
) {
    /** Only devices with a settled price count towards the total; the rest are still open. */
    private val pricedCosts: List<Long> = items.mapNotNull { it.finalCost }

    val totalLabel: String = formatRupiah(pricedCosts.sum())

    /** True when nothing has been priced yet, so the header shows a dash rather than Rp 0. */
    val isUnpriced: Boolean = pricedCosts.isEmpty()

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
    /** The raw cost behind [totalLabel], so an optimistic edit can re-derive the order's total. */
    val finalCost: Long?,
    val totalLabel: String?,
    val parts: List<OrderPartUiModel>,
)

data class OrderPartUiModel(
    val id: String,
    val name: String,
    val qty: Int,
    /** Raw, for the same reason as [OrderDetailItemUiModel.finalCost]. */
    val subtotal: Long,
    val subtotalLabel: String,
)

suspend fun OrderDetail.toUiModel(): OrderDetailUiModel {
    val moment = createdAt?.let { parseTimestamp(it)?.toLocalDateTime() }

    return OrderDetailUiModel(
        orderCode = orderCode,
        qrToken = qrToken,
        customerName = customerName,
        customerPhone = customerPhone.takeIf { it.isNotBlank() },
        cashierName = cashierName.takeIf { it.isNotBlank() },
        createdLabel = moment?.let { "${it.date.formatDate()}, ${it.formatTime()}" },
        items = items.mapIndexed { index, item -> item.toUiModel(index) },
    )
}

/**
 * The edit the server is about to make, applied locally so the screen answers the tap immediately
 * instead of after two round trips.
 *
 * Only what can be predicted exactly is predicted. The status is whatever was picked, and the cost
 * follows `recalculateFinalCost` on the server — the fee plus every part's subtotal — which is why
 * both are kept as raw numbers on the row. The order's own total and [OrderDetailUiModel.status]
 * fall out of the items, so they correct themselves here with no second copy of the rule.
 *
 * The refetch that follows still wins; this only decides what is on screen until it lands.
 */
fun OrderDetailUiModel.withItemStatus(
    itemId: String,
    status: OrderStatus,
    serviceFee: Long?,
): OrderDetailUiModel = copy(
    items = items.map { item ->
        if (item.id == itemId) item.withStatus(status, serviceFee) else item
    }
)

private fun OrderDetailItemUiModel.withStatus(
    status: OrderStatus,
    serviceFee: Long?,
): OrderDetailItemUiModel {
    // A null fee means the sheet said nothing about the price, so whatever was agreed still stands.
    val fee = serviceFee ?: this.serviceFee
    // The server recalculates on every PATCH, so an unpriced device becomes Rp 0 rather than
    // staying open — matching `(serviceFee ?? 0) + partsTotal`.
    val cost = (fee ?: 0L) + parts.sumOf { it.subtotal }

    return copy(
        status = status,
        serviceFee = fee,
        serviceFeeLabel = fee?.let { formatRupiah(it) },
        finalCost = cost,
        totalLabel = formatRupiah(cost),
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
        finalCost = finalCost,
        totalLabel = finalCost?.let { formatRupiah(it) },
        parts = parts.mapIndexed { partIndex, part ->
            OrderPartUiModel(
                id = part.id ?: "part-$index-$partIndex",
                name = part.sparePartName,
                qty = part.qty,
                subtotal = part.subtotal,
                subtotalLabel = formatRupiah(part.subtotal),
            )
        },
    )
