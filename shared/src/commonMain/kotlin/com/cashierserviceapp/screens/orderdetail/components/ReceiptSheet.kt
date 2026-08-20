package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.action_close
import cashierserviceapp.shared.generated.resources.order_detail_receipt_body
import cashierserviceapp.shared.generated.resources.order_detail_receipt_title
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.screens.orderdetail.OrderDetailItemUiModel
import com.cashierserviceapp.screens.orderdetail.OrderDetailUiModel
import com.cashierserviceapp.screens.orderdetail.OrderPartUiModel
import com.cashierserviceapp.screens.orderdetail.ReceiptSegment
import com.cashierserviceapp.screens.orderdetail.buildReceipt
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.QrCode
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
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
    val segments = remember(detail) { buildReceipt(detail) }

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
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

private val previewReceiptDetail = OrderDetailUiModel(
    orderCode = "SV-1786641253",
    qrToken = "322F9819-579B-4DB7-8328-6530C5F386BF",
    customerName = "Rina Wijaya",
    customerPhone = "08123456789",
    cashierName = "Administrator",
    createdLabel = "14 Aug 2026, 00:14",
    totalLabel = "Rp 350.000",
    isUnpriced = false,
    items = listOf(
        OrderDetailItemUiModel(
            id = "1",
            deviceName = "Samsung Galaxy A54",
            complaint = "Layar mati setelah jatuh",
            status = OrderStatus.IN_PROGRESS,
            serviceFee = 50000L,
            serviceFeeLabel = "Rp 50.000",
            totalLabel = "Rp 350.000",
            parts = listOf(OrderPartUiModel("p1", "LCD Galaxy A54", 1, "Rp 300.000"))
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
        buildReceipt(previewReceiptDetail).forEach { segment ->
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
