package com.cashierserviceapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

class Shapes(
    val roundedCornerSm: RoundedCornerShape,
    val roundedCornerMd: RoundedCornerShape,
)

internal val CashierServiceShapes: Shapes
    @Composable
    get() = Shapes(
        roundedCornerSm = RoundedCornerShape(4.dp),
        roundedCornerMd = RoundedCornerShape(8.dp),
    )