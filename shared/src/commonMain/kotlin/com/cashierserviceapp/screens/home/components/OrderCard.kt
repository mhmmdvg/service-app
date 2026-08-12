package com.cashierserviceapp.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.icons.NotepadFilled
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

@Composable
fun OrderCard(
    name: String,
    device: String,
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .background(CashierServiceTheme.colors.primaryBackground.copy(0.25f))
                .padding(6.dp)
                .size(26.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = NotepadFilled,
                contentDescription = "order_icon",
                tint = CashierServiceTheme.colors.primaryBackground
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = name,
                style = CashierServiceTheme.typography.h4
            )
            Text(
                text = device,
                style = CashierServiceTheme.typography.text2
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = date,
            style = CashierServiceTheme.typography.text2
        )
    }
}

@PreviewLightDark
@Composable
fun OrderCardPreview() = PreviewHelper {
    OrderCard(
        name = "Vikri",
        device = "iPhone 13",
        date = "16 Jun 2024",
    )
}