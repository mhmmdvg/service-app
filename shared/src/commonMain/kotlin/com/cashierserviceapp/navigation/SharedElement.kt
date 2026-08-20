package com.cashierserviceapp.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.RemeasureToBounds
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope

/**
 * Compose's answer to SwiftUI's `matchedGeometryEffect`: one element tagged with the same key on
 * both sides of a navigation, morphed from one set of bounds to the other instead of the two copies
 * cross-fading in place.
 *
 * Two scopes are needed to draw one. [SharedTransitionScope] owns the overlay the moving element is
 * drawn into and matches keys across the whole app; [AnimatedVisibilityScope] tells a single element
 * which direction its screen is going. NavDisplay hands out the second one per entry, and
 * [SharedElementScopes] passes it on through a local so screens don't have to be given a scope
 * parameter each.
 *
 * Both locals default to null so a screen composed outside NavDisplay — every `@Preview` — simply
 * skips the effect instead of crashing on a missing scope.
 */
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

/** The current [androidx.navigation3.ui.NavDisplay] entry's enter/exit scope. See above. */
val LocalNavEntryAnimatedScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

/** Keys are matched across the whole app, so they live in one place rather than as loose strings. */
object SharedElementKey {
    /** Home's tap-to-search pill and the real field on the search screen. */
    const val SearchPill: String = "search-pill"
}

/**
 * Republishes each entry's animated scope as [LocalNavEntryAnimatedScope].
 *
 * A decorator rather than a wrapper around every screen: it runs at the point `NavEntry.Content()`
 * is invoked, which is inside NavDisplay's `AnimatedContent`, so the scope is available and no
 * entry can forget to provide it.
 */
internal val SharedElementScopes = NavEntryDecorator<AppRoute> { entry ->
    CompositionLocalProvider(
        LocalNavEntryAnimatedScope provides LocalNavAnimatedContentScope.current
    ) {
        entry.Content()
    }
}

/**
 * Tags this layout as one half of a shared-element transition. The other half is whatever carries
 * the same [key] on the screen being navigated to or from.
 *
 * [androidx.compose.animation.SharedTransitionScope.sharedBounds] rather than `sharedElement`: the
 * two halves are not the same content. Home shows a placeholder label, search shows a live text
 * field with a cursor — the bounds travel as one, while the contents cross-fade inside them.
 *
 * A no-op when either scope is missing, which is what makes it safe in previews.
 */
@Composable
fun Modifier.sharedElementBounds(
    key: Any,
    clipShape: Shape = CircleShape,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
): Modifier {
    val sharedScope = LocalSharedTransitionScope.current ?: return this
    val animatedScope = LocalNavEntryAnimatedScope.current ?: return this

    return with(sharedScope) {
        this@sharedElementBounds.sharedBounds(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = animatedScope,
            enter = enter,
            exit = exit,
            // The pill is a row of text and icons, not an image: re-laying it out at each animated
            // width keeps the magnifier 16.dp from the left edge the whole way. scaleToBounds, the
            // default, would stretch the glyphs instead.
            resizeMode = RemeasureToBounds,
            boundsTransform = { _, _ ->
                spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = Rect.VisibilityThreshold
                )
            },
            // While it travels the element is drawn in the layout's overlay, above both screens,
            // where nothing clips it to a pill any more unless we say so.
            clipInOverlayDuringTransition = OverlayClip(clipShape),
        )
    }
}
