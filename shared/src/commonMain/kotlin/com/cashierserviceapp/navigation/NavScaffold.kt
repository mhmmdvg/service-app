package com.cashierserviceapp.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.cashierserviceapp.localization.AppStrings
import com.cashierserviceapp.localization.LocalStrings
import com.cashierserviceapp.utils.LocalWindowSize
import com.cashierserviceapp.utils.WindowSize
import com.cashierserviceapp.ui.components.MainNavDestination
import com.cashierserviceapp.ui.icons.HistoryFilled
import com.cashierserviceapp.ui.icons.HistoryOutlined
import com.cashierserviceapp.ui.icons.HomeFilled
import com.cashierserviceapp.ui.icons.HomeOutlined
import com.cashierserviceapp.ui.icons.NotepadFilled
import com.cashierserviceapp.ui.icons.NotepadOutlined
import com.cashierserviceapp.ui.icons.PlusFilled
import com.cashierserviceapp.ui.icons.PlusOutlined
import com.cashierserviceapp.ui.icons.SettingFilled
import com.cashierserviceapp.ui.icons.SettingOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme

/**
 * Typed on AppRoute rather than TopLevelRoute: the add-order destination opens a cover instead of
 * switching tabs, so it never becomes the selected route.
 *
 * The labels are the icons' content descriptions, so they follow the chosen language along with
 * everything else.
 */
private fun bottomNavDestinations(strings: AppStrings): List<MainNavDestination<AppRoute>> = listOf(
    MainNavDestination(
        label = strings.navHome,
        icon = HomeOutlined,
        iconSelected = HomeFilled,
        route = HomeScreen
    ),
    MainNavDestination(
        label = strings.navOrder,
        icon = NotepadOutlined,
        iconSelected = NotepadFilled,
        route = OrderScreen
    ),
    MainNavDestination(
        label = strings.navAddOrder,
        icon = PlusOutlined,
        iconSelected = PlusFilled,
        route = AddOrderScreen
    ),
    MainNavDestination(
        label = strings.navHistory,
        icon = HistoryOutlined,
        iconSelected = HistoryFilled,
        route = HistoryScreen
    ),
    MainNavDestination(
        label = strings.navSettings,
        icon = SettingOutlined,
        iconSelected = SettingFilled,
        route = SettingsScreen
    )
)

@Composable
internal fun NavScaffold(
    navState: NavState,
    navigator: Navigator,
    content: @Composable (() -> Unit)
) {
    // Navigator.add() dispatches on the route type: tabs switch, covers are presented.
    val onSelectRoute: (AppRoute) -> Unit = { route ->
        navigator.add(route)
    }

    val strings = LocalStrings.current
    val destinations = remember(strings) { bottomNavDestinations(strings) }
    val windowSize = LocalWindowSize.current
    val showLargeNavigation = windowSize != WindowSize.Compact &&
            navState.topLevelRoute != null
    val showCompactNavigation = windowSize == WindowSize.Compact &&
            navState.topLevelRoute != null &&
            navState.currentBackstack.size == 1 &&
            !isKeyboardOpen()

    Row(
        Modifier
            .fillMaxSize()
            .background(color = CashierServiceTheme.colors.mainBackground)
    ) {
        val enterAnimSpec = tween<IntSize>(delayMillis = AnimationConstants.DefaultDurationMillis)

        AnimatedVisibility(
            visible = showLargeNavigation,
            enter = expandHorizontally(enterAnimSpec),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            SideNavigation(
                currentRoute = navState.topLevelRoute,
                destinations = destinations,
                onSelectRoute = onSelectRoute,
                expanded = windowSize == WindowSize.Large,
            )
        }

        Column(Modifier.weight(1f)) {
            Box(Modifier.weight(1f).clipToBounds()) {
                content()
            }

            AnimatedVisibility(
                visible = showCompactNavigation,
                enter = expandVertically(enterAnimSpec),
                exit = shrinkVertically() + fadeOut()
            ) {
                BottomNavigation(
                    currentRoute = navState.topLevelRoute,
                    destinations = destinations,
                    onSelectRoute = onSelectRoute
                )
            }
        }
    }
}

@Composable
private fun isKeyboardOpen(): Boolean {
    val bottomInset = WindowInsets.ime.getBottom(LocalDensity.current)
    return rememberUpdatedState(bottomInset > 300).value
}