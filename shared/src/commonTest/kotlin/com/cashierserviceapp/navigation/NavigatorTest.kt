package com.cashierserviceapp.navigation

import androidx.compose.runtime.mutableStateListOf
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `NavDisplay` throws on an empty entry list, and the entries come from [NavState.currentBackstack]
 * — so anything that can empty that backstack crashes the app rather than misrouting it.
 */
class NavigatorTest {

    private fun navigator(): Navigator {
        val home = mutableStateListOf<AppRoute>(HomeScreen)
        val order = mutableStateListOf<AppRoute>(OrderScreen)

        return Navigator(
            state = NavState(
                topLevelBackStacks = mapOf(HomeScreen to home, OrderScreen to order),
                defaultBackstack = mutableStateListOf(),
                primaryTopLevelRoute = HomeScreen,
                currentBackstack = mutableStateListOf(HomeScreen),
                coverBackstack = mutableStateListOf(),
            ),
            topLevelBackEnabled = true,
        )
    }

    @Test
    fun replacingACoverLeavesTheBackstackUnderneathIntact() {
        val navigator = navigator()
        navigator.present(AddOrderScreen)

        // What finishing an order does: swap the form's cover for the receipt's.
        navigator.set(OrderSuccessScreen("order-1"))

        assertEquals(listOf(HomeScreen), navigator.state.currentBackstack.toList())
        assertEquals(listOf(OrderSuccessScreen("order-1")), navigator.state.coverBackstack.toList())
    }

    @Test
    fun dismissingThatCoverReturnsToTheScreenUnderneath() {
        val navigator = navigator()
        navigator.add(OrderDetailScreen("order-1"))
        navigator.present(AddOrderScreen)
        navigator.set(OrderSuccessScreen("order-2"))
        navigator.dismissCover()

        assertEquals(emptyList(), navigator.state.coverBackstack.toList())
        assertEquals(
            listOf(HomeScreen, OrderDetailScreen("order-1")),
            navigator.state.currentBackstack.toList()
        )
    }

    @Test
    fun replacingANonCoverStillClearsTheBackstack() {
        val navigator = navigator()
        navigator.add(OrderDetailScreen("order-1"))

        // Signing out must not leave the signed-in screens reachable by back.
        navigator.set(LoginScreen)

        assertEquals(listOf(LoginScreen), navigator.state.currentBackstack.toList())
    }
}
