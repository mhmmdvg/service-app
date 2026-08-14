package com.cashierserviceapp.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.MainNavDestination
import com.cashierserviceapp.ui.components.MainNavigationRail
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.utils.topInsetPadding

@Composable
internal fun SideNavigation(
    currentRoute: AppRoute?,
    destinations: List<MainNavDestination<AppRoute>>,
    onSelectRoute: (AppRoute) -> Unit,
    expanded: Boolean,
) {
    val currentDestination = destinations.find { it.route == currentRoute }

    Row {
        MainNavigationRail(
            currentDestination = currentDestination,
            destinations = destinations,
            onSelect = { selectedDestination ->
                onSelectRoute(selectedDestination.route)
            },
            expanded = expanded,
            modifier = Modifier.padding(topInsetPadding())
        )
        VerticalDivider(thickness = 1.dp, color = CashierServiceTheme.colors.strokePale)
    }
}