package com.cashierserviceapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.MainNavDestination
import com.cashierserviceapp.ui.components.MainNavigationBar
import com.cashierserviceapp.utils.bottomInsetPadding


@Composable
internal fun BottomNavigation(
    currentRoute: TopLevelRoute?,
    destinations: List<MainNavDestination<TopLevelRoute>>,
    onSelectRoute: (TopLevelRoute) -> Unit,
) {
    val currentDestination = destinations.find { it.route == currentRoute }

    Column(
        Modifier
            .padding(bottomInsetPadding())
            .background(color = com.cashierserviceapp.ui.theme.CashierServiceTheme.colors.strokePale.copy(alpha = 0.03f))
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = com.cashierserviceapp.ui.theme.CashierServiceTheme.colors.strokePale
        )
        MainNavigationBar(
            modifier = Modifier.padding(vertical = 6.dp),
            currentDestination = currentDestination,
            destinations = destinations,
            onSelect = { selectDestinations ->
                onSelectRoute(selectDestinations.route)
            }
        )
    }
}