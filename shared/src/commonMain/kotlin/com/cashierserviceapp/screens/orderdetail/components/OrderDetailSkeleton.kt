package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.extensions.shimmerEffect
import com.cashierserviceapp.ui.theme.CashierServiceTheme

@Composable
internal fun LoadingSkeleton() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(128.dp)
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .shimmerEffect()
    )

    Spacer(Modifier.height(28.dp))

    Box(
        Modifier
            .fillMaxWidth()
            .height(66.dp)
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .shimmerEffect()
    )

    Spacer(Modifier.height(28.dp))

    repeat(4) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                Modifier
                    .size(width = 80.dp, height = 12.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            Box(
                Modifier
                    .size(width = 130.dp, height = 12.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
        }
    }
}