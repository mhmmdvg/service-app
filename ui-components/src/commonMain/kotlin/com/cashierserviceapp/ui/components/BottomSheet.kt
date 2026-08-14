package com.cashierserviceapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Gap between the sheet and the screen on the left, right and bottom. */
private val SheetInsetHorizontal = 12.dp
private val SheetInsetVertical = 8.dp

/** Past this much drag, letting go dismisses instead of springing back. */
private val DismissDragThreshold = 120.dp

/** Or past this flick speed, in pixels per second, however far it travelled. */
private const val DISMISS_VELOCITY = 1200f

private const val ENTER_DURATION_MILLIS = 340
private const val EXIT_DURATION_MILLIS = 240

/** The same easing the full-screen cover uses, so modals across the app move alike. */
private val EnterEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
private val ExitEasing = CubicBezierEasing(0.4f, 0f, 0.9f, 0.6f)

/**
 * A sheet that rises from the bottom edge and floats clear of it, inset on both sides and below —
 * the detached-container geometry of iOS 26's sheets, with the app's own solid surface rather than
 * any glass or blur.
 *
 * Not Material3's `ModalBottomSheet`: that one is edge-to-edge and square-shouldered at the bottom
 * by design, which is the opposite of the shape being asked for here.
 *
 * Dismisses on back, on a tap outside, and on a drag past [DismissDragThreshold] — each animating
 * out before [onDismissRequest] fires, so the caller can drop the sheet from composition without
 * cutting the animation short.
 */
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = CashierServiceTheme.shapes.roundedCornerXxl,
    content: @Composable ColumnScope.(hide: () -> Unit) -> Unit,
) {
    // Created already heading for visible, so the sheet animates in from hidden on its first
    // composition. The target must be set exactly once, at creation — setting it from an effect
    // that re-runs would keep re-raising the sheet the instant anything asked it to leave.
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }

    // Once the exit animation has finished — hidden, and no longer heading anywhere else — the
    // sheet is safe to take out of composition.
    LaunchedEffect(visibleState.currentState, visibleState.targetState) {
        if (!visibleState.currentState && !visibleState.targetState) onDismissRequest()
    }

    // Every way out goes through here: back, tap outside, drag, and the sheet's own buttons. That
    // way the exit always plays before the caller drops the sheet from composition.
    val requestHide = { visibleState.targetState = false }

    Dialog(
        onDismissRequest = requestHide,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                // The dialog window fills the screen, so "outside the sheet" is this box rather
                // than anywhere the platform would notice — hence handling the tap ourselves.
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = requestHide
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visibleState = visibleState,
                enter = slideInVertically(tween(ENTER_DURATION_MILLIS, easing = EnterEasing)) { it },
                exit = slideOutVertically(tween(EXIT_DURATION_MILLIS, easing = ExitEasing)) { it },
            ) {
                SheetSurface(
                    modifier = modifier,
                    shape = shape,
                    onHide = requestHide,
                    content = { content(requestHide) }
                )
            }
        }
    }
}

@Composable
private fun SheetSurface(
    modifier: Modifier,
    shape: RoundedCornerShape,
    onHide: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Plain state rather than an Animatable: the drag writes it on every frame, and only the
    // spring-back needs animating. Read from the layout lambda below, so neither recomposes.
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val thresholdPx = with(LocalDensity.current) { DismissDragThreshold.toPx() }

    val draggableState = rememberDraggableState { delta ->
        // Downward only: dragging up would lift the sheet off its own bottom inset.
        dragOffset = (dragOffset + delta).coerceAtLeast(0f)
    }

    Column(
        modifier
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            )
            .padding(
                horizontal = SheetInsetHorizontal,
                vertical = SheetInsetVertical
            )
            // Read in the layout phase, so dragging never recomposes the sheet's contents.
            .offset { IntOffset(0, dragOffset.roundToInt()) }
            .fillMaxWidth()
            .clip(shape)
            .background(CashierServiceTheme.colors.mainBackground)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.07f))
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    if (dragOffset > thresholdPx || velocity > DISMISS_VELOCITY) {
                        // Leaves dragOffset where the finger let go, so the exit slide carries on
                        // from there instead of snapping back up first.
                        onHide()
                    } else {
                        animate(
                            initialValue = dragOffset,
                            targetValue = 0f,
                            initialVelocity = velocity,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) { value, _ -> dragOffset = value }
                    }
                }
            )
            // Swallows taps so they don't reach the dismiss-on-outside handler behind the sheet.
            .clickable(interactionSource = null, indication = null, onClick = {})
    ) {
        Grabber()
        content()
    }
}

@Composable
private fun Grabber() {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Spacer(
            Modifier
                .padding(top = 8.dp)
                .size(width = 36.dp, height = 5.dp)
                .clip(CashierServiceTheme.shapes.roundedCornerSm)
                .background(CashierServiceTheme.colors.strokeHalf)
        )
    }
}

@PreviewLightDark
@Composable
private fun BottomSheetPreview() = PreviewHelper {
    // Rendered inline rather than in its Dialog, so the preview shows the surface itself.
    Column(
        Modifier
            .fillMaxWidth()
            .padding(
                horizontal = SheetInsetHorizontal,
                vertical = SheetInsetVertical
            )
            .clip(CashierServiceTheme.shapes.roundedCornerXxl)
            .background(CashierServiceTheme.colors.mainBackground)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.07f))
    ) {
        Grabber()
        Spacer(Modifier.height(24.dp))
        Text("Sheet content", Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(24.dp))
    }
}
