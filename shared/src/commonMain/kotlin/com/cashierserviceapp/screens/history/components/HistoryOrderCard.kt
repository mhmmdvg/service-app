package com.cashierserviceapp.screens.history.components

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
import com.cashierserviceapp.ui.components.Avatar
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * A finished repair, laid out like a transaction: who and what on the left, what it came to on the
 * right.
 *
 * Shares [Avatar], the tile background and the corner radius with
 * [com.cashierserviceapp.screens.order.components.OrderCard] so the two lists read as one app. The
 * status chip is deliberately gone — every row here is completed, so a badge saying so on all of
 * them carries no information. The total takes that space instead.
 */
@Composable
fun HistoryOrderCard(
    name: String,
    code: String,
    itemsCount: Int,
    total: String,
    time: String,
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
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = total,
                style = CashierServiceTheme.typography.h4.copy(textAlign = TextAlign.End),
                maxLines = 1
            )
            Text(
                text = time,
                style = CashierServiceTheme.typography.text2.copy(textAlign = TextAlign.End),
                color = CashierServiceTheme.colors.secondaryText,
                maxLines = 1
            )
        }
    }
}

private fun Int.deviceLabel(): String = if (this == 1) "1 device" else "$this devices"

@PreviewLightDark
@Composable
private fun HistoryOrderCardPreview() = PreviewHelper {
    HistoryOrderCard(
        name = "Rina Wijaya",
        code = "SV-1786641253",
        itemsCount = 1,
        total = "Rp 350.000",
        time = "14:32"
    )
}

@PreviewLightDark
@Composable
private fun HistoryOrderCardLongPreview() = PreviewHelper {
    HistoryOrderCard(
        name = "Bambang Kusumawardhana",
        code = "SV-1786641253",
        itemsCount = 3,
        total = "Rp 12.750.000",
        time = "09:05"
    )
}
