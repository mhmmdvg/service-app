package com.cashierserviceapp.screens.addorder.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.screens.addorder.AddOrderStep
import com.cashierserviceapp.ui.theme.CashierServiceTheme

@Composable
internal fun StepProgress(step: AddOrderStep) {
    // Kept as State and read only inside drawBehind, so filling the bar animates in the draw phase
    // without recomposing anything.
    val progress = animateFloatAsState(
        targetValue = (step.ordinal + 1f) / AddOrderStep.entries.size,
        animationSpec = tween(320),
        label = "stepProgress"
    )

    val trackColor = CashierServiceTheme.colors.strokePale
    val fillColor = CashierServiceTheme.colors.primaryBackground

    Spacer(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(3.dp)
            .drawBehind {
                val radius = CornerRadius(size.height / 2f)
                drawRoundRect(color = trackColor, cornerRadius = radius)
                drawRoundRect(
                    color = fillColor,
                    size = Size(size.width * progress.value, size.height),
                    cornerRadius = radius
                )
            }
    )
}