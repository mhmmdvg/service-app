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
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.utils.bottomInsetPadding


@Composable
internal fun BottomNavigation(
    currentRoute: AppRoute?,
    destinations: List<MainNavDestination<AppRoute>>,
    onSelectRoute: (AppRoute) -> Unit,
) {
    val currentDestination = destinations.find { it.route == currentRoute }

    Column(
        Modifier
            .background(color = CashierServiceTheme.colors.strokePale.copy(alpha = 0.03f))
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = CashierServiceTheme.colors.strokePale
        )
        MainNavigationBar(
            modifier = Modifier
                .padding(bottomInsetPadding()),
            currentDestination = currentDestination,
            destinations = destinations,
            onSelect = { selectDestinations ->
                onSelectRoute(selectDestinations.route)
            }
        )
    }
}