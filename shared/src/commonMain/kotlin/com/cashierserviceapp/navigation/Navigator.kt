package com.cashierserviceapp.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter

class Navigator(
    val state: NavState,
    val topLevelBackEnabled: Boolean
) {
    private val _tabReselections = MutableSharedFlow<TopLevelRoute>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun tabReselections(forRoute: TopLevelRoute): Flow<TopLevelRoute> =
        _tabReselections.filter { it == forRoute }

    fun goBack() {
        val currentBackstack = state.currentBackstack

        // A cover sits above everything, so it always consumes back first.
        if (state.coverBackstack.isNotEmpty()) {
            dismissCover()
            return
        }

        if (state.topLevelRoute == null) {
            // We're using the default stack, remove an entry if possible
            if (currentBackstack.size > 1) {
                currentBackstack.removeLastOrNull()
            }
            return
        }

        if (currentBackstack.size == 1 && state.topLevelRoute != state.primaryTopLevelRoute) {
            // Can't go further up on current backstack, but we're not on the primary route
            if (topLevelBackEnabled) {
                state.topLevelRoute = state.primaryTopLevelRoute
            }
        } else if (currentBackstack.size > 1) {
            currentBackstack.removeLastOrNull()
        }
    }

    fun add(route: AppRoute) {
        when (route) {
            is TopLevelRoute -> activate(route)
            is CoverRoute -> present(route)
            else -> state.currentBackstack.add(route)
        }
    }

    /**
     * Makes [route] the only thing showing, replacing what was there rather than stacking onto it.
     *
     * A [CoverRoute] is drawn *over* the app, not instead of it, so replacing one leaves the
     * backstack underneath untouched — clearing it would swap one cover for another and leave
     * nothing to draw when that cover is dismissed, which `NavDisplay` rejects outright.
     */
    fun set(route: AppRoute) {
        state.coverBackstack.clear()
        if (route !is CoverRoute) state.currentBackstack.clear()
        add(route)
    }

    /** Presents [route] modally over the whole app. Re-presenting the visible cover is a no-op. */
    fun present(route: CoverRoute) {
        if (state.coverRoute == route) return
        state.coverBackstack.add(route)
    }

    fun dismissCover() {
        state.coverBackstack.removeLastOrNull()
    }

    fun activate(route: TopLevelRoute, withReselection: Boolean = false) {
        if (withReselection && route == state.topLevelRoute) {
            val currentBackstack = state.currentBackstack

            // Reselected the current top-level route, clear to root
            if (currentBackstack.size > 1) {
                currentBackstack.removeRange(1, currentBackstack.size)
            } else {
                // Already at root, signal reselection for scroll-to-top
                _tabReselections.tryEmit(route)
            }
            return
        }
        state.topLevelRoute = route
    }
}