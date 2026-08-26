package com.cashierserviceapp.screens.home.components

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
import com.cashierserviceapp.localization.deviceCountLabel
import com.cashierserviceapp.localization.waitLabel
import com.cashierserviceapp.screens.home.AttentionRow
import com.cashierserviceapp.screens.order.components.OrderStatusChip
import com.cashierserviceapp.ui.components.Avatar
import com.cashierserviceapp.ui.components.Chip
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * An order in the home queue, or in a search result.
 *
 * Same avatar, tile and radius as the order and history rows. What differs is the right-hand side:
 * history shows what an order came to, this shows how long it has been sitting, because that's the
 * thing that decides what gets picked up next.
 *
 * Search reaches finished orders too, and a wait time on one of those would be nonsense — it
 * stopped waiting when it was collected. Those rows show the status instead.
 */
@Composable
fun AttentionCard(
    row: AttentionRow,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
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
            name = row.customerName,
            size = 42.dp,
            initialSize = 20.sp
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = row.customerName,
                style = CashierServiceTheme.typography.h4,
                maxLines = 1
            )
            Text(
                text = "${row.orderCode} · ${deviceCountLabel(row.itemsCount)}",
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText,
                maxLines = 1
            )
        }

        Spacer(Modifier.width(8.dp))

        val wait = waitLabel(row.daysWaiting)
        when {
            row.isCompleted -> OrderStatusChip(OrderStatus.COMPLETED)

            row.isOverdue -> Chip(label = wait, color = CashierServiceTheme.colors.orangeText)

            else -> Text(
                text = wait,
                style = CashierServiceTheme.typography.text2.copy(textAlign = TextAlign.End),
                color = CashierServiceTheme.colors.secondaryText,
                maxLines = 1
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AttentionCardPreview() = PreviewHelper {
    AttentionCard(
        row = AttentionRow(
            id = "1",
            customerName = "Rina Wijaya",
            orderCode = "SV-1786641253",
            itemsCount = 1,
            totalLabel = "Rp 350.000",
            daysWaiting = 0
        )
    )
    AttentionCard(
        row = AttentionRow(
            id = "2",
            customerName = "Pelanggan 1",
            orderCode = "SV-1786086981",
            itemsCount = 2,
            totalLabel = "Rp 0",
            daysWaiting = 7
        )
    )
    AttentionCard(
        row = AttentionRow(
            id = "3",
            customerName = "Budi Santoso",
            orderCode = "SV-1786641500",
            itemsCount = 3,
            totalLabel = "Rp 1.250.000",
            daysWaiting = 12,
            status = OrderStatus.COMPLETED
        )
    )
}
