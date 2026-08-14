package com.cashierserviceapp.screens.order.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.ui.components.Avatar
import com.cashierserviceapp.ui.components.Chip
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * An order in the in-progress list.
 *
 * The right column stacks the status over when the order came in — the same two-line shape as the
 * history and home rows, so all three lists scan identically.
 */
@Composable
fun OrderCard(
    name: String,
    code: String,
    itemsCount: Int,
    status: OrderStatus,
    time: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .clickable(onClick = onClick, role = Role.Button)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Avatar(
            name = name,
            size = 42.dp,
            initialSize = 20.sp
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = name,
                style = CashierServiceTheme.typography.h4,
                maxLines = 1
            )
            Text(
                text = "$code · ${itemsCount.deviceLabel()}",
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText,
                maxLines = 1
            )
        }

        Spacer(Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Chip(label = status.label(), color = status.color())

            if (time.isNotEmpty()) {
                Text(
                    text = time,
                    style = CashierServiceTheme.typography.text2.copy(textAlign = TextAlign.End),
                    color = CashierServiceTheme.colors.secondaryText,
                    maxLines = 1
                )
            }
        }
    }
}

private fun Int.deviceLabel(): String = if (this == 1) "1 device" else "$this devices"

private fun OrderStatus.label(): String = when (this) {
    OrderStatus.RECEIVED -> "Received"
    OrderStatus.DIAGNOSING -> "Diagnosing"
    OrderStatus.IN_PROGRESS -> "In Progress"
    OrderStatus.COMPLETED -> "Completed"
}

@Composable
private fun OrderStatus.color() = when (this) {
    OrderStatus.RECEIVED -> CashierServiceTheme.colors.blueText
    OrderStatus.DIAGNOSING -> CashierServiceTheme.colors.orangeText
    OrderStatus.IN_PROGRESS -> CashierServiceTheme.colors.purpleText
    OrderStatus.COMPLETED -> CashierServiceTheme.colors.greenText
}

@PreviewLightDark
@Composable
fun OrderCardPreview() = PreviewHelper {
    OrderCard(
        name = "Rina Wijaya",
        code = "SV-1786641253",
        itemsCount = 1,
        status = OrderStatus.IN_PROGRESS,
        time = "14:32"
    )
    OrderCard(
        name = "Pelanggan 1",
        code = "SV-1786086981",
        itemsCount = 3,
        status = OrderStatus.RECEIVED,
        time = "Yesterday"
    )
    OrderCard(
        name = "Bambang Kusumawardhana",
        code = "SV-1786086111",
        itemsCount = 2,
        status = OrderStatus.DIAGNOSING,
        time = "7 Aug"
    )
}
