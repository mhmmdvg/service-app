package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.action_close
import cashierserviceapp.shared.generated.resources.order_detail_receipt_body
import cashierserviceapp.shared.generated.resources.order_detail_receipt_title
import cashierserviceapp.shared.generated.resources.printer_access_body
import cashierserviceapp.shared.generated.resources.printer_access_grant
import cashierserviceapp.shared.generated.resources.printer_access_title
import cashierserviceapp.shared.generated.resources.printer_choose
import cashierserviceapp.shared.generated.resources.printer_failed
import cashierserviceapp.shared.generated.resources.printer_none_body
import cashierserviceapp.shared.generated.resources.printer_none_title
import cashierserviceapp.shared.generated.resources.printer_print
import cashierserviceapp.shared.generated.resources.printer_sending
import cashierserviceapp.shared.generated.resources.printer_sent
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.printing.PairedPrinter
import com.cashierserviceapp.printing.Printing
import com.cashierserviceapp.printing.rememberPrinting
import com.cashierserviceapp.screens.orderdetail.OrderDetailItemUiModel
import com.cashierserviceapp.screens.orderdetail.OrderDetailUiModel
import com.cashierserviceapp.screens.orderdetail.OrderPartUiModel
import com.cashierserviceapp.screens.orderdetail.ReceiptSegment
import com.cashierserviceapp.screens.orderdetail.buildReceipt
import com.cashierserviceapp.screens.orderdetail.rememberReceiptStrings
import com.cashierserviceapp.screens.orderdetail.toEscPos
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.QrCode
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * Exactly what a receipt printer would emit, in a monospace block at the printer's column width.
 *
 * Shown before printing rather than after, because the roll is the only other place to find out
 * whether the layout is right.
 */
@Composable
fun ReceiptSheet(
    detail: OrderDetailUiModel,
    onDismiss: () -> Unit,
) {
    val strings = rememberReceiptStrings()
    val segments = remember(detail, strings) { buildReceipt(detail, strings) }

    // Null on iOS and desktop, where there is no printer to reach — the button goes with it.
    val printing = rememberPrinting()
    val scope = rememberCoroutineScope()

    var choosingPrinter by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<PrintStatus?>(null) }

    BottomSheet(onDismissRequest = onDismiss) { hide ->
        Spacer(Modifier.height(20.dp))

        Column(
            Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                text = stringResource(Res.string.order_detail_receipt_title),
                style = CashierServiceTheme.typography.h3,
                color = CashierServiceTheme.colors.primaryText
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.order_detail_receipt_body),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )

            Spacer(Modifier.height(16.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp)
                    .clip(CashierServiceTheme.shapes.roundedCornerLg)
                    .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                segments.forEach { segment ->
                    when (segment) {
                        is ReceiptSegment.Lines -> Text(
                            text = segment.text,
                            style = receiptTextStyle(),
                            color = CashierServiceTheme.colors.primaryText,
                        )

                        is ReceiptSegment.Qr -> QrCode(
                            data = segment.data,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .size(132.dp),
                        )
                    }
                }
            }

            if (printing != null && choosingPrinter) {
                Spacer(Modifier.height(16.dp))

                PrinterPanel(
                    printing = printing,
                    status = status,
                    onPick = { printer ->
                        status = PrintStatus.Sending
                        scope.launch {
                            status = printing.print(printer, segments.toEscPos()).fold(
                                onSuccess = { PrintStatus.Sent },
                                onFailure = { PrintStatus.Failed(it.message) }
                            )
                        }
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    label = stringResource(Res.string.action_close),
                    onClick = hide,
                    modifier = Modifier.weight(1f),
                    primary = false
                )

                if (printing != null) {
                    Button(
                        label = stringResource(Res.string.printer_print),
                        onClick = {
                            status = null
                            choosingPrinter = true
                        },
                        modifier = Modifier.weight(1f),
                        primary = true,
                        enabled = status != PrintStatus.Sending
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

/** Where a print attempt has got to. Null until the Print button is pressed. */
private sealed interface PrintStatus {
    data object Sending : PrintStatus
    data object Sent : PrintStatus
    data class Failed(val message: String?) : PrintStatus
}

/**
 * The printer half of the sheet: permission, then the paired devices, then how it went.
 *
 * Inline rather than a sheet of its own — the receipt above it is the thing being printed, and
 * stacking a second sheet over it would hide the one piece of context that matters.
 */
@Composable
private fun PrinterPanel(
    printing: Printing,
    status: PrintStatus?,
    onPick: (PairedPrinter) -> Unit,
) {
    // Read once per Printing instance rather than per recomposition: each read is a call into the
    // Bluetooth service. A new instance arrives when permission changes, which re-reads it.
    val printers = remember(printing) { printing.printers }

    when {
        status != null -> PrinterNote(
            title = when (status) {
                PrintStatus.Sending -> stringResource(Res.string.printer_sending)
                PrintStatus.Sent -> stringResource(Res.string.printer_sent)
                is PrintStatus.Failed -> stringResource(Res.string.printer_failed)
            },
            body = (status as? PrintStatus.Failed)?.message
        )

        printing.needsAccess -> {
            PrinterNote(
                title = stringResource(Res.string.printer_access_title),
                body = stringResource(Res.string.printer_access_body)
            )

            Spacer(Modifier.height(12.dp))

            Button(
                label = stringResource(Res.string.printer_access_grant),
                onClick = printing::requestAccess,
                modifier = Modifier.fillMaxWidth(),
                primary = false
            )
        }

        // Pairing belongs to the system Bluetooth screen, so this is a signpost, not a dead end.
        printers.isEmpty() -> PrinterNote(
            title = stringResource(Res.string.printer_none_title),
            body = stringResource(Res.string.printer_none_body)
        )

        else -> {
            Text(
                text = stringResource(Res.string.printer_choose),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )

            printers.forEach { printer ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(CashierServiceTheme.shapes.roundedCornerLg)
                        .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
                        .clickable(role = Role.Button) { onPick(printer) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = printer.name,
                        modifier = Modifier.weight(1f),
                        style = CashierServiceTheme.typography.text1,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun PrinterNote(title: String, body: String?) {
    Text(
        text = title,
        style = CashierServiceTheme.typography.text1,
        color = CashierServiceTheme.colors.primaryText
    )

    if (body != null) {
        Spacer(Modifier.height(4.dp))

        Text(
            text = body,
            style = CashierServiceTheme.typography.text2,
            color = CashierServiceTheme.colors.secondaryText
        )
    }
}

private val previewReceiptDetail = OrderDetailUiModel(
    orderCode = "SV-1786641253",
    qrToken = "322F9819-579B-4DB7-8328-6530C5F386BF",
    customerName = "Rina Wijaya",
    customerPhone = "08123456789",
    cashierName = "Administrator",
    createdLabel = "14 Aug 2026, 00:14",
    items = listOf(
        OrderDetailItemUiModel(
            id = "1",
            deviceName = "Samsung Galaxy A54",
            complaint = "Layar mati setelah jatuh",
            status = OrderStatus.IN_PROGRESS,
            serviceFee = 50000L,
            serviceFeeLabel = "Rp 50.000",
            finalCost = 350000L,
            totalLabel = "Rp 350.000",
            parts = listOf(OrderPartUiModel("p1", "LCD Galaxy A54", 1, 300000L, "Rp 300.000"))
        )
    )
)

/** Monospace, or the column alignment the receipt is built around falls apart. */
@Composable
private fun receiptTextStyle(): TextStyle = CashierServiceTheme.typography.text2.copy(
    fontFamily = FontFamily.Monospace,
    fontSize = 12.sp,
    lineHeight = 17.sp,
)

@PreviewLightDark
@Composable
private fun ReceiptPreview() = PreviewHelper {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        buildReceipt(previewReceiptDetail, rememberReceiptStrings()).forEach { segment ->
            when (segment) {
                is ReceiptSegment.Lines -> Text(text = segment.text, style = receiptTextStyle())
                is ReceiptSegment.Qr -> QrCode(
                    data = segment.data,
                    modifier = Modifier.padding(vertical = 10.dp).size(132.dp),
                )
            }
        }
    }
}
