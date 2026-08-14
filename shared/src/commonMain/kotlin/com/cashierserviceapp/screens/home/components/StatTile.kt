package com.cashierserviceapp.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.extensions.shimmerEffect
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * A number worth glancing at, with its label underneath.
 *
 * A null [value] means the figure isn't available yet — it renders as a muted dash rather than a
 * zero, so a missing number never reads as a real one.
 *
 * [accent] tints the figure when it's one the cashier should act on.
 */
@Composable
fun StatTile(
    value: String?,
    label: String,
    modifier: Modifier = Modifier,
    accent: Color? = null,
) {
    Column(
        modifier
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value ?: "—",
            style = CashierServiceTheme.typography.h1.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            color = when {
                value == null -> CashierServiceTheme.colors.noteText
                else -> accent ?: CashierServiceTheme.colors.primaryText
            },
            maxLines = 1
        )
        Text(
            text = label,
            style = CashierServiceTheme.typography.text2,
            color = CashierServiceTheme.colors.secondaryText,
            maxLines = 1
        )
    }
}

@Composable
fun StatTileSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .width(40.dp)
                .height(22.dp)
                .clip(CashierServiceTheme.shapes.roundedCornerSm)
                .shimmerEffect()
        )
        Box(
            Modifier
                .width(72.dp)
                .height(11.dp)
                .clip(CashierServiceTheme.shapes.roundedCornerSm)
                .shimmerEffect()
        )
    }
}

@PreviewLightDark
@Composable
private fun StatTilePreview() = PreviewHelper {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatTile(value = "7", label = "In progress", modifier = Modifier.weight(1f))
        StatTile(value = null, label = "All income", modifier = Modifier.weight(1f))
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatTileSkeleton(Modifier.weight(1f))
        StatTileSkeleton(Modifier.weight(1f))
    }
}
