package com.cashierserviceapp

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.navigation.LocalIsTopLevelRoute
import com.cashierserviceapp.ui.components.AppBar
import com.cashierserviceapp.ui.icons.ChevronLeftOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme

@Composable
fun ScreenWithTitle(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    navigationIcon: ImageVector = ChevronLeftOutlined,
    scrollable: Boolean = true,
    contentScrollState: ScrollState = rememberScrollState(),
    /**
     * Keeps the bar at its short height with a small centred title, instead of starting tall and
     * collapsing on scroll. Pins the behaviour too, so nothing tries to animate a range that isn't
     * there.
     *
     * Defaults off the route type — tab roots expand, pushed screens don't — so screens only pass
     * this to deviate from that. See [LocalIsTopLevelRoute].
     */
    alwaysCollapsed: Boolean = !LocalIsTopLevelRoute.current,
    scrollBehavior: TopAppBarScrollBehavior =
        if (alwaysCollapsed) TopAppBarDefaults.pinnedScrollBehavior()
        else TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.(PaddingValues) -> Unit,
) {
    Scaffold(
        modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = CashierServiceTheme.colors.mainBackground,
        topBar = {
            AppBar(
                title = title,
                scrollBehavior = scrollBehavior,
                onNavigationIconClick = onBack,
                navigationIcon = navigationIcon,
                actions = actions,
                alwaysCollapsed = alwaysCollapsed,
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateStartPadding(LocalLayoutDirection.current)
                )
                .let { if (scrollable) it.verticalScroll(contentScrollState) else it }
                .padding(horizontal = 17.dp),
        ) {
            if (scrollable) {
                Spacer(Modifier.height(innerPadding.calculateTopPadding() - 5.dp))
            }
            content(innerPadding)
        }
    }
}