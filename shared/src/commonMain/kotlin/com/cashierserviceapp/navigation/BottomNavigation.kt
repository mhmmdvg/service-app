package com.cashierserviceapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.MainNavDestination
import com.cashierserviceapp.ui.components.MainNavigationBar
import com.cashierserviceapp.ui.icons.HistoryFilled
import com.cashierserviceapp.ui.icons.HistoryOutlined
import com.cashierserviceapp.ui.icons.HomeFilled
import com.cashierserviceapp.ui.icons.HomeOutlined
import com.cashierserviceapp.ui.icons.NotepadFilled
import com.cashierserviceapp.ui.icons.NotepadOutlined
import com.cashierserviceapp.ui.icons.SettingFilled
import com.cashierserviceapp.ui.icons.SettingOutlined
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

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
            .background(color = com.cashierserviceapp.ui.theme.CashierServiceTheme.colors.strokePale.copy(alpha = 0.03f))
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = com.cashierserviceapp.ui.theme.CashierServiceTheme.colors.strokePale
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
                    icon = NotepadOutlined,
                    iconSelected = NotepadFilled,
                    route = "Order"
                ),
                MainNavDestination(
                    label = "History",
                    icon = HistoryOutlined,
                    iconSelected = HistoryFilled,
                    route = "History"
                ),
                MainNavDestination(
                    label = "Settings",
                    icon = SettingOutlined,
                    iconSelected = SettingFilled,
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