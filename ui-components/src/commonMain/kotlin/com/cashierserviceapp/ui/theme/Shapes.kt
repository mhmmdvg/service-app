package com.cashierserviceapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

class Shapes(
    val roundedCornerSm: RoundedCornerShape,
    val roundedCornerMd: RoundedCornerShape,
    val roundedCornerLg: RoundedCornerShape,
    val roundedCornerXl: RoundedCornerShape,
)

internal val CashierServiceShapes: Shapes
    @Composable
    get() = Shapes(
        roundedCornerSm = RoundedCornerShape(4.dp),
        roundedCornerMd = RoundedCornerShape(8.dp),
        roundedCornerLg = RoundedCornerShape(12.dp),
        roundedCornerXl = RoundedCornerShape(16.dp),
    )