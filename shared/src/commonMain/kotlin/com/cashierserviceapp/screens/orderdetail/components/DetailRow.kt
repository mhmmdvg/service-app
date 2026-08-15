package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * A labelled fact: muted label on the left, value on the right — the key/value rows a transaction
 * detail is mostly made of.
 *
 * The value takes the leftover width and truncates, so a long token can't push the label off.
 */
@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasised: Boolean = false,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = CashierServiceTheme.typography.text2,
            color = CashierServiceTheme.colors.secondaryText,
            maxLines = 1
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = value,
            modifier = Modifier.weight(1f, fill = false),
            style = CashierServiceTheme.typography.text2.copy(
                textAlign = TextAlign.End,
                fontWeight = if (emphasised) FontWeight.Bold else FontWeight.Normal
            ),
            color = CashierServiceTheme.colors.primaryText,
            maxLines = 1
        )
    }
}

@PreviewLightDark
@Composable
private fun DetailRowPreview() = PreviewHelper {
    DetailRow(label = "Order code", value = "SV-1786641253")
    DetailRow(label = "Created", value = "14 Aug 2026, 00:14")
    DetailRow(label = "QR token", value = "322F9819-579B-4DB7-8328-6530C5F386BF")
}
