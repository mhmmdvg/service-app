package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme


@Composable
internal fun DetailSectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(bottom = 10.dp),
        style = CashierServiceTheme.typography.text2.copy(fontWeight = FontWeight.SemiBold),
        color = CashierServiceTheme.colors.secondaryText
    )
}