package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.order_detail_awaiting_price
import cashierserviceapp.shared.generated.resources.order_detail_service_fee
import cashierserviceapp.shared.generated.resources.order_detail_total
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.screens.order.components.OrderStatusChip
import com.cashierserviceapp.screens.orderdetail.OrderDetailItemUiModel
import com.cashierserviceapp.screens.orderdetail.OrderPartUiModel
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import org.jetbrains.compose.resources.stringResource

/**
 * One device on the order: what it is, what's wrong with it, and what it has cost so far.
 *
 * The money block is only drawn once something has been priced — a device still waiting on a
 * diagnosis says so rather than showing a row of zeroes.
 */
@Composable
fun DeviceCard(
    item: OrderDetailItemUiModel,
    modifier: Modifier = Modifier,
    isUpdating: Boolean = false,
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
            .padding(14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.deviceName,
                modifier = Modifier.weight(1f),
                style = CashierServiceTheme.typography.h4,
                maxLines = 1
            )

            Spacer(Modifier.width(8.dp))

            // Dimmed while its update is in flight; the control itself lives in the screen footer.
            Box(Modifier.graphicsLayer { alpha = if (isUpdating) 0.4f else 1f }) {
                OrderStatusChip(item.status)
            }
        }

        item.complaint?.let { complaint ->
            Spacer(Modifier.height(4.dp))

            Text(
                text = complaint,
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )
        }

        val hasMoney = item.parts.isNotEmpty() ||
                item.serviceFeeLabel != null ||
                item.totalLabel != null

        if (!hasMoney) {
            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(Res.string.order_detail_awaiting_price),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.noteText
            )
            return@Column
        }

        Spacer(Modifier.height(10.dp))
        HorizontalDivider(thickness = 1.dp, color = CashierServiceTheme.colors.tileBackground)

        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            item.parts.forEach { part ->
                DetailRow(
                    label = "${part.name} ×${part.qty}",
                    value = part.subtotalLabel
                )
            }

            item.serviceFeeLabel?.let { fee ->
                DetailRow(
                    label = stringResource(Res.string.order_detail_service_fee),
                    value = fee
                )
            }

            item.totalLabel?.let { total ->
                HorizontalDivider(
                    thickness = 1.dp,
                    color = CashierServiceTheme.colors.tileBackground
                )
                DetailRow(
                    label = stringResource(Res.string.order_detail_total),
                    value = total,
                    emphasised = true
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DeviceCardPreview() = PreviewHelper {
    DeviceCard(
        item = OrderDetailItemUiModel(
            id = "1",
            deviceName = "Samsung Galaxy A54",
            complaint = "Layar mati setelah jatuh",
            status = OrderStatus.IN_PROGRESS,
            serviceFeeLabel = "Rp 50.000",
            totalLabel = "Rp 350.000",
            parts = listOf(
                OrderPartUiModel("p1", "LCD Galaxy A54", 1, "Rp 300.000")
            )
        )
    )

    DeviceCard(
        item = OrderDetailItemUiModel(
            id = "2",
            deviceName = "Apple iPhone 13",
            complaint = "Battery drains fast",
            status = OrderStatus.RECEIVED,
            serviceFeeLabel = null,
            totalLabel = null,
            parts = emptyList()
        )
    )
}
