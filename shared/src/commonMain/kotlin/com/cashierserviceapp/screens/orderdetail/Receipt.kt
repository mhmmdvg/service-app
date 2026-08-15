package com.cashierserviceapp.screens.orderdetail

/**
 * Lays the order out as the fixed-width text a receipt printer takes.
 *
 * Mini dot-matrix and thermal printers are character devices: they accept a stream of lines at a
 * fixed column count and print them in a monospace font. So the "design" here is entirely a
 * question of where characters sit — the same string that renders in the preview is what gets
 * sent to the printer, which is why this is a pure function with no Compose in sight.
 *
 * @param width columns the printer can fit. 32 is the usual figure for a 58mm roll; 42 for 80mm.
 */
private const val DEFAULT_WIDTH = 32

// No shop record on the server yet, so the header is a constant. Move it to a settings screen or a
// `/shop` endpoint when there's more than one outlet.
private const val SHOP_NAME = "CASHIER SERVICE"
private const val SHOP_FOOTER = "Thank you — keep this receipt"

fun buildReceipt(detail: OrderDetailUiModel, width: Int = DEFAULT_WIDTH): String = buildString {
    appendLine(SHOP_NAME.center(width))
    appendLine("-".repeat(width))

    appendLine(labelled("Order", detail.orderCode, width))
    detail.createdLabel?.let { appendLine(labelled("Date", it, width)) }
    detail.cashierName?.let { appendLine(labelled("Cashier", it, width)) }
    appendLine(labelled("Customer", detail.customerName, width))
    detail.customerPhone?.let { appendLine(labelled("Phone", it, width)) }

    appendLine("-".repeat(width))

    detail.items.forEach { item ->
        appendLine(item.deviceName.take(width))
        item.complaint?.let { appendLine("  ${it.take(width - 2)}") }

        item.parts.forEach { part ->
            appendLine(row("  ${part.name} x${part.qty}", part.subtotalLabel, width))
        }
        item.serviceFeeLabel?.let { appendLine(row("  Service fee", it, width)) }
        item.totalLabel?.let { appendLine(row("  Subtotal", it, width)) }

        appendLine(row("  Status", item.status.receiptLabel(), width))
        appendLine()
    }

    appendLine("-".repeat(width))
    appendLine(row("TOTAL", if (detail.isUnpriced) "-" else detail.totalLabel, width))
    appendLine("-".repeat(width))
    appendLine()
    appendLine("Track this repair:".center(width))
    // Wrapped rather than truncated — a half-printed token is useless to whoever types it in.
    detail.qrToken.chunked(width).forEach { appendLine(it.center(width)) }
    appendLine()
    appendLine(SHOP_FOOTER.center(width))
}

/** `Label     value`, with the value hard against the right edge. */
private fun labelled(label: String, value: String, width: Int): String =
    row("$label:", value, width)

/**
 * Pads [left] and [right] apart to fill [width]. When the pair won't fit, the left side gives way
 * first — an order code or a price is the part worth keeping intact.
 */
private fun row(left: String, right: String, width: Int): String {
    val available = width - right.length - 1
    if (available <= 0) return right.takeLast(width)

    val trimmedLeft = if (left.length > available) left.take(available) else left
    val gap = width - trimmedLeft.length - right.length

    return trimmedLeft + " ".repeat(gap.coerceAtLeast(1)) + right
}

private fun String.center(width: Int): String {
    if (length >= width) return take(width)

    val padding = (width - length) / 2
    return " ".repeat(padding) + this
}

private fun com.cashierserviceapp.domain.models.OrderStatus.receiptLabel(): String = when (this) {
    com.cashierserviceapp.domain.models.OrderStatus.RECEIVED -> "Received"
    com.cashierserviceapp.domain.models.OrderStatus.DIAGNOSING -> "Diagnosing"
    com.cashierserviceapp.domain.models.OrderStatus.IN_PROGRESS -> "In Progress"
    com.cashierserviceapp.domain.models.OrderStatus.COMPLETED -> "Completed"
}
