package com.cashierserviceapp.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.cashierserviceapp.ui.theme.CashierServiceTheme

private const val ENTER_DURATION_MILLIS = 380
private const val EXIT_DURATION_MILLIS = 300

/** UIKit's modal presentation curve: quick to leave the bottom edge, long settle at the top. */
private val EnterEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
private val ExitEasing = CubicBezierEasing(0.4f, 0f, 0.9f, 0.6f)

/**
 * Presents [route] as an opaque screen sliding up from the bottom edge over the entire app, and
 * slides it back down on dismissal — SwiftUI's `fullScreenCover`.
 *
 * Deliberately not a [androidx.navigation3.ui.NavDisplay] entry: entries render inside the
 * scaffold, so they can only ever animate within the content area, leaving the bottom navigation
 * visible below them. Drawn as a sibling above the scaffold instead, the cover really does cover
 * the whole window.
 */
// BackHandler points at androidx.navigationevent as its replacement, but that artifact isn't on
// our classpath — navigation3 keeps it internal — so this stays until it is.
@Suppress("DEPRECATION")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun <T : Any> FullScreenCover(
    route: T?,
    onDismiss: () -> Unit,
    content: @Composable (T) -> Unit,
) {
    // Retained so the screen keeps rendering itself while it slides back out, after the route that
    // asked for it is already gone.
    var displayedRoute by remember { mutableStateOf(route) }
    if (route != null) displayedRoute = route

    val visible = route != null

    BackHandler(enabled = visible, onBack = onDismiss)

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(tween(ENTER_DURATION_MILLIS, easing = EnterEasing)) { it },
        exit = slideOutVertically(tween(EXIT_DURATION_MILLIS, easing = ExitEasing)) { it },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(CashierServiceTheme.colors.mainBackground)
                .blockPointerInputBelow()
        ) {
            displayedRoute?.let { content(it) }
        }
    }
}

/**
 * Swallows anything the cover's own content didn't handle, so the screen and bottom navigation
 * underneath stay untouchable while the cover is on screen — they're hidden, but still composed
 * and otherwise still hit-testable.
 *
 * Consumes on [PointerEventPass.Main], which reaches this node only after its children have had
 * the event, so the cover's content keeps working normally.
 */
private fun Modifier.blockPointerInputBelow(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            awaitPointerEvent(PointerEventPass.Main).changes.forEach { it.consume() }
        }
    }
}
