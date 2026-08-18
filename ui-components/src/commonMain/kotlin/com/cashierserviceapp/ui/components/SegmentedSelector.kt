package com.cashierserviceapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

private val TrackShape = RoundedCornerShape(percent = 100)
private const val THUMB_INSET_DP = 3f

/**
 * A pill of mutually exclusive options with a thumb that slides between them.
 *
 * Suits settings where every option is worth seeing at once — two or three at most. Past that the
 * labels get too cramped and a list of rows reads better.
 *
 * @param options what to choose between, in display order. Must not be empty.
 * @param selected the current choice; if it isn't in [options] no thumb is drawn.
 */
@Composable
fun <T> SegmentedSelector(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    require(options.isNotEmpty()) { "SegmentedSelector needs at least one option" }

    val selectedIndex = options.indexOf(selected)
    val thumbColor = CashierServiceTheme.colors.mainBackground

    // Held as State and read only inside drawBehind, so the thumb slides in the draw phase without
    // recomposing the row on every frame.
    val thumbPosition = animateFloatAsState(
        targetValue = selectedIndex.coerceAtLeast(0).toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "segmentedThumb"
    )

    Row(
        modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(TrackShape)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.08f))
            .drawBehind {
                if (selectedIndex < 0) return@drawBehind

                val inset = THUMB_INSET_DP.dp.toPx()
                val segmentWidth = size.width / options.size
                val thumbSize = Size(segmentWidth - inset * 2, size.height - inset * 2)

                drawRoundRect(
                    color = thumbColor,
                    topLeft = Offset(segmentWidth * thumbPosition.value + inset, inset),
                    size = thumbSize,
                    cornerRadius = CornerRadius(thumbSize.height / 2f)
                )
            }
            .padding(THUMB_INSET_DP.dp)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val textColor by animateColorAsState(
                if (isSelected) CashierServiceTheme.colors.primaryText
                else CashierServiceTheme.colors.secondaryText
            )

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(TrackShape)
                    .selectable(
                        selected = isSelected,
                        enabled = enabled,
                        role = Role.RadioButton,
                        indication = null,
                        interactionSource = null,
                        onClick = { if (option != selected) onSelect(option) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label(option),
                    style = CashierServiceTheme.typography.text2.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = textColor,
                    maxLines = 1
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SegmentedSelectorPreview() = PreviewHelper {
    var theme by remember { mutableStateOf("System") }
    SegmentedSelector(
        options = listOf("System", "Light", "Dark"),
        selected = theme,
        onSelect = { theme = it },
        label = { it }
    )

    var language by remember { mutableStateOf("English") }
    SegmentedSelector(
        options = listOf("English", "Bahasa Indonesia"),
        selected = language,
        onSelect = { language = it },
        label = { it }
    )
}
