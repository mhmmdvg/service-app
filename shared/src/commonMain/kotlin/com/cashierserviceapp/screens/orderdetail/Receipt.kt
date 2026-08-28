package com.cashierserviceapp.screens.orderdetail

import androidx.compose.runtime.Composable
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.order_status_completed
import cashierserviceapp.shared.generated.resources.order_status_diagnosing
import cashierserviceapp.shared.generated.resources.order_status_in_progress
import cashierserviceapp.shared.generated.resources.order_status_received
import cashierserviceapp.shared.generated.resources.receipt_cashier
import cashierserviceapp.shared.generated.resources.receipt_customer
import cashierserviceapp.shared.generated.resources.receipt_date
import cashierserviceapp.shared.generated.resources.receipt_footer
import cashierserviceapp.shared.generated.resources.receipt_order
import cashierserviceapp.shared.generated.resources.receipt_phone
import cashierserviceapp.shared.generated.resources.receipt_service_fee
import cashierserviceapp.shared.generated.resources.receipt_status
import cashierserviceapp.shared.generated.resources.receipt_subtotal
import cashierserviceapp.shared.generated.resources.receipt_total
import cashierserviceapp.shared.generated.resources.receipt_track
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.utils.trackingLink
import org.jetbrains.compose.resources.stringResource

/**
 * A piece of a receipt, in the order it comes off the roll.
 *
 * Mini dot-matrix and thermal printers are character devices: they accept a stream of lines at a
 * fixed column count and print them in a monospace font. That's what [Lines] is. But a QR code is
 * not a character — the printer either draws it from its own firmware or takes it as a raster — so
 * it can't live inside the text and needs a segment of its own.
 */
sealed interface ReceiptSegment {
    /** Lines already laid out to the printer's column width, newline-separated. */
    data class Lines(val text: String) : ReceiptSegment

    /** A QR code encoding [data], printed centred at this point in the roll. */
    data class Qr(val data: String) : ReceiptSegment
}

private const val DEFAULT_WIDTH = 32

// No shop record on the server yet, so the header is a constant. Move it to a settings screen or a
// `/shop` endpoint when there's more than one outlet. Not translated: it's the shop's name.
private const val SHOP_NAME = "CASHIER SERVICE"

/**
 * Every word the receipt prints that isn't the order's own data.
 *
 * Handed in rather than read from the catalog here so [buildReceipt] stays a pure function of its
 * arguments — the same reason the layout lives in this file at all. Build one with
 * [rememberReceiptStrings] from a composable; the preview and the printer then share both the
 * wording and the layout.
 */
data class ReceiptStrings(
    val order: String,
    val date: String,
    val cashier: String,
    val customer: String,
    val phone: String,
    val serviceFee: String,
    val subtotal: String,
    val status: String,
    val total: String,
    val track: String,
    val footer: String,
    val statusLabels: Map<OrderStatus, String>,
)

/** The catalog's receipt wording, in the language the app is currently rendering in. */
@Composable
fun rememberReceiptStrings(): ReceiptStrings = ReceiptStrings(
    order = stringResource(Res.string.receipt_order),
    date = stringResource(Res.string.receipt_date),
    cashier = stringResource(Res.string.receipt_cashier),
    customer = stringResource(Res.string.receipt_customer),
    phone = stringResource(Res.string.receipt_phone),
    serviceFee = stringResource(Res.string.receipt_service_fee),
    subtotal = stringResource(Res.string.receipt_subtotal),
    status = stringResource(Res.string.receipt_status),
    total = stringResource(Res.string.receipt_total),
    track = stringResource(Res.string.receipt_track),
    footer = stringResource(Res.string.receipt_footer),
    statusLabels = mapOf(
        OrderStatus.RECEIVED to stringResource(Res.string.order_status_received),
        OrderStatus.DIAGNOSING to stringResource(Res.string.order_status_diagnosing),
        OrderStatus.IN_PROGRESS to stringResource(Res.string.order_status_in_progress),
        OrderStatus.COMPLETED to stringResource(Res.string.order_status_completed),
    ),
)

/**
 * Lays the order out as the segments a receipt printer takes.
 *
 * The text here is a pure function of the order with no Compose in sight, which is the point: the
 * same strings that render in the preview are what gets sent to the printer.
 *
 * @param width columns the printer can fit. 32 is the usual figure for a 58mm roll; 42 for 80mm.
 */
fun buildReceipt(
    detail: OrderDetailUiModel,
    strings: ReceiptStrings,
    width: Int = DEFAULT_WIDTH,
): List<ReceiptSegment> = listOf(
    ReceiptSegment.Lines(buildOrderLines(detail, strings, width)),
    ReceiptSegment.Qr(trackingLink(detail.qrToken)),
    ReceiptSegment.Lines(buildFooterLines(detail, strings, width)),
)

private fun buildOrderLines(
    detail: OrderDetailUiModel,
    strings: ReceiptStrings,
    width: Int,
): String = buildString {
    appendLine(SHOP_NAME.center(width))
    appendLine("-".repeat(width))

    appendLine(labelled(strings.order, detail.orderCode, width))
    detail.createdLabel?.let { appendLine(labelled(strings.date, it, width)) }
    detail.cashierName?.let { appendLine(labelled(strings.cashier, it, width)) }
    appendLine(labelled(strings.customer, detail.customerName, width))
    detail.customerPhone?.let { appendLine(labelled(strings.phone, it, width)) }

    appendLine("-".repeat(width))

    detail.items.forEach { item ->
        appendLine(item.deviceName.take(width))
        item.complaint?.let { appendLine("  ${it.take(width - 2)}") }

        item.parts.forEach { part ->
            appendLine(row("  ${part.name} x${part.qty}", part.subtotalLabel, width))
        }
        item.serviceFeeLabel?.let { appendLine(row("  ${strings.serviceFee}", it, width)) }
        item.totalLabel?.let { appendLine(row("  ${strings.subtotal}", it, width)) }

        appendLine(row("  ${strings.status}", strings.statusLabels.getValue(item.status), width))
        appendLine()
    }

    appendLine("-".repeat(width))
    appendLine(row(strings.total, if (detail.isUnpriced) "-" else detail.totalLabel, width))
    appendLine("-".repeat(width))
    appendLine()
    append(strings.track.center(width))
}

/**
 * What follows the QR code.
 *
 * The token stays in print under the code. A phone with a cracked camera, a smudged roll, a
 * customer reading it down the phone — the QR is the convenience, the characters are the fallback,
 * and the scan sheet on the home screen exists to take them by hand.
 */
private fun buildFooterLines(
    detail: OrderDetailUiModel,
    strings: ReceiptStrings,
    width: Int,
): String = buildString {
    // Wrapped rather than truncated — a half-printed token is useless to whoever types it in.
    detail.qrToken.chunked(width).forEach { appendLine(it.center(width)) }
    appendLine()
    append(strings.footer.center(width))
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
