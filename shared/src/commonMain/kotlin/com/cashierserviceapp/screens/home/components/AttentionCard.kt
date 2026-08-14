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
import com.cashierserviceapp.screens.home.AttentionRow
import com.cashierserviceapp.ui.components.Avatar
import com.cashierserviceapp.ui.components.Chip
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * An in-progress order in the home queue.
 *
 * Same avatar, tile and radius as the order and history rows. What differs is the right-hand side:
 * history shows what an order came to, this shows how long it has been sitting, because that's the
 * thing that decides what gets picked up next.
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
                text = "${row.orderCode} · ${row.itemsCount.deviceLabel()}",
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText,
                maxLines = 1
            )
        }

        Spacer(Modifier.width(8.dp))

        val waitLabel = row.daysWaiting.waitLabel()
        if (row.isOverdue) {
            Chip(label = waitLabel, color = CashierServiceTheme.colors.orangeText)
        } else {
            Text(
                text = waitLabel,
                style = CashierServiceTheme.typography.text2.copy(textAlign = TextAlign.End),
                color = CashierServiceTheme.colors.secondaryText,
                maxLines = 1
            )
        }
    }
}

private fun Int.deviceLabel(): String = if (this == 1) "1 device" else "$this devices"

private fun Int?.waitLabel(): String = when {
    this == null -> ""
    this <= 0 -> "Today"
    this == 1 -> "1 day"
    else -> "$this days"
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
}
