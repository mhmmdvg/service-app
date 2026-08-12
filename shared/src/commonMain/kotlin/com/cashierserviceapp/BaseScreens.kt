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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.AppBar
import com.cashierserviceapp.ui.icons.ChevronLeftOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme

@Composable
fun ScreenWithTitle(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    scrollable: Boolean = true,
    contentScrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier
            .fillMaxSize()
            .let { if(scrollable) it.nestedScroll(scrollBehavior.nestedScrollConnection) else it },
        containerColor = CashierServiceTheme.colors.mainBackground,
        topBar = {
            AppBar(
                title = title,
                scrollBehavior = scrollBehavior,
                onNavigationIconClick = onBack,
                navigationIcon = ChevronLeftOutlined,
                actions = actions,
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
                .padding(horizontal = 18.dp),
        ) {
            if (scrollable) {
                Spacer(Modifier.height(innerPadding.calculateTopPadding() - 5.dp))
            }
            content()
        }
    }
}