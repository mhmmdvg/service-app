package com.cashierserviceapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.theme.CashierServiceTheme

@Composable
fun ContentMessage(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(top = 72.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = CashierServiceTheme.typography.h4.copy(textAlign = TextAlign.Center),
            color = CashierServiceTheme.colors.primaryText
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = body,
            style = CashierServiceTheme.typography.text2.copy(textAlign = TextAlign.Center),
            color = CashierServiceTheme.colors.secondaryText
        )

        if (actionLabel != null) {
            Spacer(Modifier.height(20.dp))

            Button(label = actionLabel, onClick = onAction, primary = false)
        }
    }
}