package com.cashierserviceapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.ui.theme.CashierServiceTheme

private val LabelFontSize = 16.sp
private const val LABEL_COLLAPSED_SCALE = 11f / 16f

@Composable
internal fun FieldContainer(
    modifier: Modifier = Modifier,
    label: String,
    isFocused: Boolean = false,
    isFilled: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isLabelUp = isFocused || isFilled

    // These are deliberately kept as State rather than unwrapped with `by`: every read below
    // happens inside a layout/draw lambda, so the animations never recompose this function.
    val borderColor = animateColorAsState(
        targetValue = if (isFocused) CashierServiceTheme.colors.strokeInputFocus else CashierServiceTheme.colors.strokePale,
        label = "borderColor"
    )

    val borderSize = animateDpAsState(
        targetValue = if (isFocused) (1.2).dp else 1.dp,
        label = "borderSize"
    )

    val labelColor = animateColorAsState(
        targetValue = if (isFocused) CashierServiceTheme.colors.primaryText else CashierServiceTheme.colors.secondaryText,
        label = "labelColor"
    )

    val labelOffsetY = animateDpAsState(
        targetValue = if (isLabelUp) (-10).dp else 0.dp,
        label = "labelOffsetY"
    )

    val labelScale = animateFloatAsState(
        targetValue = if (isLabelUp) LABEL_COLLAPSED_SCALE else 1f,
        animationSpec = tween(150),
        label = "labelScale"
    )

    val contentOffsetY = animateDpAsState(
        targetValue = if (isLabelUp) 8.dp else 0.dp,
        animationSpec = tween(150),
        label = "contentOffsetY"
    )

    val shape = CashierServiceTheme.shapes.roundedCornerLg
    val labelStyle = CashierServiceTheme.typography.text1
    val expandedLabelStyle = remember(labelStyle) { labelStyle.copy(fontSize = LabelFontSize) }

    Box(
        modifier
            .heightIn(min = 52.dp)
            .drawWithContent {
                drawContent()
                // Mirrors Modifier.border: the stroke is inset by half its width so the
                // outer edge lands exactly on the layout bounds.
                val stroke = borderSize.value.toPx()
                val outerRadius = shape.topStart.toPx(size, this)
                drawRoundRect(
                    color = borderColor.value,
                    topLeft = Offset(stroke / 2f, stroke / 2f),
                    size = Size(size.width - stroke, size.height - stroke),
                    cornerRadius = CornerRadius(outerRadius - stroke / 2f),
                    style = Stroke(stroke)
                )
            }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicText(
            text = label,
            modifier = Modifier
                .align(Alignment.TopStart)
                .graphicsLayer {
                    translationY = labelOffsetY.value.toPx()
                    scaleX = labelScale.value
                    scaleY = labelScale.value
                    transformOrigin = TransformOrigin(0f, 0f)
                },
            style = expandedLabelStyle,
            color = { labelColor.value }
        )

        // The no-trailing branch is kept byte-for-byte as it was so wrap-content fields
        // keep sizing to their content instead of expanding to the incoming max width.
        if (trailing == null) {
            Box(Modifier.offset { IntOffset(0, contentOffsetY.value.roundToPx()) }) {
                content()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Only the content carries the label-driven offset. The trailing slot stays
                // centred in the field so it doesn't bob along with the floating label.
                Box(
                    Modifier
                        .weight(1f)
                        .offset { IntOffset(0, contentOffsetY.value.roundToPx()) }
                ) { content() }
                Spacer(Modifier.width(8.dp))
                trailing()
            }
        }
    }
}
