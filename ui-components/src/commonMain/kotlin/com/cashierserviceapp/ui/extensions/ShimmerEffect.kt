package com.cashierserviceapp.ui.extensions

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.shimmerEffect(
    color: Color = Color.LightGray
): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        )
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                color.copy(alpha = 0.6f),
                color.copy(alpha = 0.2f),
                color.copy(alpha = 0.6f),
            ),
            start = Offset(x = translateAnim.value - 1000f, y = 0f),
            end = Offset(x = translateAnim.value, y = 1000f)
        )
    )
}