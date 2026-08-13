package com.cashierserviceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

@Composable
fun Chip(
    label: String,
    color: Color,
) {
    Box(
        Modifier
            .clip(CircleShape)
            .background(color)
            .padding(4.dp)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = label,
            color = CashierServiceTheme.colors.primaryTextWhiteFixed,
            style = CashierServiceTheme.typography.text2.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@PreviewLightDark
@Composable
fun ChipPreview() = PreviewHelper {
    Chip(
        label = "Chip",
        color = CashierServiceTheme.colors.primaryBackground,
    )
}