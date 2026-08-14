package com.cashierserviceapp.screens.order.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.extensions.shimmerEffect
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

@Composable
fun OrderCardSkeleton(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(CashierServiceTheme.colors.tileBackground.copy(0.05f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .size(42.dp)
                .shimmerEffect()
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Box(
                Modifier
                    .width(86.dp)
                    .height(12.dp)
                    .clip(CashierServiceTheme.shapes.roundedCornerSm)
                    .shimmerEffect()
            )
            Box(
                Modifier
                    .width(56.dp)
                    .height(10.dp)
                    .clip(CashierServiceTheme.shapes.roundedCornerSm)
                    .shimmerEffect()
            )
        }

        Spacer(Modifier.weight(1f))

        // Mirrors the card's stacked status-over-time column.
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                Modifier
                    .width(56.dp)
                    .height(12.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            Box(
                Modifier
                    .width(32.dp)
                    .height(10.dp)
                    .clip(CashierServiceTheme.shapes.roundedCornerSm)
                    .shimmerEffect()
            )
        }
    }
}


/* Preview */
@PreviewLightDark
@Composable
fun PreviewOrderCardSkeleton() = PreviewHelper {
    OrderCardSkeleton()
}