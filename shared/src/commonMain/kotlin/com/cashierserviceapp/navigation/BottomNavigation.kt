package com.cashierserviceapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.components.MainNavDestination
import com.cashierserviceapp.components.MainNavigationBar
import com.cashierserviceapp.theme.CashierServiceTheme
import com.cashierserviceapp.theme.PreviewHelper
import com.cashierserviceapp.utils.PreviewLightDark
import com.composables.HomeFilled
import com.composables.HomeOutlined

@Composable
private fun bottomInsetPadding() = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).asPaddingValues()

@Composable
fun BottomNavigation() {
    var currentDestination by remember {
        mutableStateOf(
            MainNavDestination(
                label = "Home",
                icon = HomeOutlined,
                iconSelected = HomeFilled,
                route = "Home"
            )
        )
    }

    Column(
        Modifier
            .padding(bottomInsetPadding())
            .background(color = CashierServiceTheme.colors.strokePale.copy(alpha = 0.03f))
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = CashierServiceTheme.colors.strokePale
        )
        MainNavigationBar(
            modifier = Modifier.padding(vertical = 6.dp),
            currentDestination = currentDestination,
            destinations = listOf(
                MainNavDestination(
                    label = "Home",
                    icon = HomeOutlined,
                    iconSelected = HomeFilled,
                    route = "Home"
                ),
                MainNavDestination(
                    label = "Order",
                    icon = HomeOutlined,
                    iconSelected = HomeFilled,
                    route = "Order"
                ),
                MainNavDestination(
                    label = "History",
                    icon = HomeOutlined,
                    iconSelected = HomeFilled,
                    route = "History"
                ),
                MainNavDestination(
                    label = "Settings",
                    icon = HomeOutlined,
                    iconSelected = HomeFilled,
                    route = "Settings"
                ),
            ),
            onSelect = {}
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewBottomNavigation() = PreviewHelper {
    BottomNavigation()
}