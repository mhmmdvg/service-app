package com.cashierserviceapp.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay

private const val FADE_OUT_DURATION_MILLIS = 90
private const val FADE_IN_DURATION_MILLIS = 210

/**
 * Material style fade-through: the outgoing screen fades out first, then the incoming one fades in.
 */
private fun fadeThrough(): ContentTransform =
    fadeIn(tween(FADE_IN_DURATION_MILLIS, delayMillis = FADE_OUT_DURATION_MILLIS)) togetherWith
            fadeOut(tween(FADE_OUT_DURATION_MILLIS))

/**
 * [androidx.navigation3.runtime.NavEntry] metadata that makes a screen fade instead of slide.
 *
 * Applied to [TopLevelRoute] entries only: switching tabs has no forward/backward direction, so the
 * default lateral slide would imply a hierarchy that isn't there. Nested routes keep the NavDisplay
 * defaults.
 */
internal val topLevelFadeTransition: Map<String, Any> =
    NavDisplay.transitionSpec { fadeThrough() } +
            NavDisplay.popTransitionSpec { fadeThrough() } +
            NavDisplay.predictivePopTransitionSpec { fadeThrough() }

/**
 * Whether the screen being composed is a tab root.
 *
 * Drives the app bar: a tab is a destination in its own right, so its title is the headline and
 * earns the tall bar that collapses on scroll. Anything pushed on top is somewhere you went *from*
 * a tab — its title is a label on the content, and a 112.dp banner would just push that content
 * down. `ScreenWithTitle` reads this so neither kind has to ask.
 *
 * Defaults to false: only [topLevelEntry] flips it, so a new pushed screen gets the compact bar
 * without anyone remembering to say so.
 */
val LocalIsTopLevelRoute = staticCompositionLocalOf { false }

/** Adds an entry for a [TopLevelRoute], animated with [topLevelFadeTransition]. */
internal inline fun <reified K : TopLevelRoute> EntryProviderScope<AppRoute>.topLevelEntry(
    noinline content: @Composable (K) -> Unit
) {
    entry<K>(metadata = topLevelFadeTransition) { route ->
        CompositionLocalProvider(LocalIsTopLevelRoute provides true) {
            content(route)
        }
    }
}
