package com.cashierserviceapp.screens.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * Sticky day divider: the day on the left, the day's takings on the right.
 *
 * Paints an opaque [CashierServiceTheme.colors.mainBackground] because it stays pinned while rows
 * scroll underneath it — a transparent header would let them show through.
 */
@Composable
fun HistorySectionHeader(
    label: String,
    total: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(CashierServiceTheme.colors.mainBackground)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = CashierServiceTheme.typography.text2.copy(fontWeight = FontWeight.SemiBold),
            color = CashierServiceTheme.colors.secondaryText,
            maxLines = 1
        )
        Text(
            text = total,
            style = CashierServiceTheme.typography.text2.copy(fontWeight = FontWeight.SemiBold),
            color = CashierServiceTheme.colors.secondaryText,
            maxLines = 1
        )
    }
}

@PreviewLightDark
@Composable
private fun HistorySectionHeaderPreview() = PreviewHelper {
    HistorySectionHeader(label = "Today", total = "Rp 1.250.000")
    HistorySectionHeader(label = "13 August 2026", total = "Rp 350.000")
}
