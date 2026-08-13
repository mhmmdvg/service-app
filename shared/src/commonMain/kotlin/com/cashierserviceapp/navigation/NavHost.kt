package com.cashierserviceapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.cashierserviceapp.ThemeChangeAnimation
import com.cashierserviceapp.flags.LocalFlags
import com.cashierserviceapp.screens.authentication.LoginScreen
import com.cashierserviceapp.screens.history.HistoryScreen
import com.cashierserviceapp.screens.home.HomeScreen
import com.cashierserviceapp.screens.order.OrderScreen
import com.cashierserviceapp.screens.settings.SettingsScreen
import com.cashierserviceapp.ui.theme.CashierServiceDarkColors
import com.cashierserviceapp.ui.theme.CashierServiceLightColors
import com.cashierserviceapp.ui.theme.CashierServiceTheme

@Composable
internal fun NavHost(
    isOnboardingComplete: Boolean,
    isLoggedIn: Boolean,
    isDarkTheme: Boolean,
    onThemeChange: ((Boolean) -> Unit)?,
) {
//    val startRoute = remember {
//        if (isOnboardingComplete) HomeScreen else OnboardScreen
//    }
    // Resolved once: rememberNavState seeds its backstacks from this, so it must not change
    // underneath us. Signing in/out navigates explicitly instead.
    val startRoute: AppRoute = remember { if (isLoggedIn) HomeScreen else LoginScreen }
    val navState = rememberNavState(
        startRoute = startRoute,
        topLevelRoute = setOf(
            HomeScreen,
            OrderScreen,
            HistoryScreen,
            SettingsScreen
        ),
        primaryTopLevelRoute = HomeScreen
    )

    val topLevelBackEnabled = LocalFlags.current.enableBackOnTopLevelScreens
    val navigator = remember(navState, topLevelBackEnabled) {
        Navigator(navState, topLevelBackEnabled)
    }

    val entryProvider = entryProvider {
        screens(
            navigator = navigator,
            onBack = { navigator.goBack() }
        )
    }

    ThemeChangeAnimation(
        isDarkTheme = isDarkTheme,
        enabled = true
//        enabled = navState.currentBackstack.lastOrNull() is SettingsScreen
    ) { appliedIsDarkTheme ->
        val colors = when {
            appliedIsDarkTheme -> CashierServiceDarkColors
            else -> CashierServiceLightColors
        }

        if (onThemeChange != null) {
            LaunchedEffect(colors) { onThemeChange(colors.isDark) }
        }

        CashierServiceTheme(
            rippleEnabled = LocalFlags.current.rippleEnabled,
            colors = colors
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(CashierServiceTheme.colors.mainBackground)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                    )
            ) {
                NavScaffold(
                    navState = navState,
                    navigator = navigator,
                ) {
                    NavDisplay(
                        entries = navState.toDecoratedEntries(entryProvider),
                        onBack = navigator::goBack,
                    )
                }
            }
        }
    }

}

private fun EntryProviderScope<AppRoute>.screens(
    navigator: Navigator,
    onBack: () -> Unit
) {
    entry<LoginScreen> {
        LoginScreen(
            // Replaces the backstack so back doesn't return to the form once signed in.
            onLoginSuccess = { navigator.set(HomeScreen) }
        )
    }

    topLevelEntry<HomeScreen> {
        HomeScreen()
    }

    topLevelEntry<OrderScreen> {
        OrderScreen()
    }

    topLevelEntry<HistoryScreen> {
        HistoryScreen()
    }

    topLevelEntry<SettingsScreen> {
        SettingsScreen()
    }
}