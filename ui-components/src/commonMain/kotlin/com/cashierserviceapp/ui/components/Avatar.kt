package com.cashierserviceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    name: String,
    size: Dp = 48.dp,
    initialSize: TextUnit = 18.sp,
    color: Color = CashierServiceTheme.colors.primaryBackground
) {
    val initial = name.split(" ")
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")

    Box(
       modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = CashierServiceTheme.typography.text1.copy(
                fontSize = initialSize,
                fontWeight = FontWeight.Bold,
            ),
            color = color
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewAvatar() = PreviewHelper {
    Avatar(
        name = "Vikri"
    )
}