package com.cashierserviceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.ui.icons.ChevronLeftOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper

object AppBarTokens {
    private val appBarHeight = TopAppBarDefaults.MediumAppBarExpandedHeight.value
    val GRADIENT_START_Y = appBarHeight + appBarHeight / 2
}

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavigationIconClick: (() -> Unit)? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
    ),
    navigationIcon: ImageVector = ChevronLeftOutlined,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    MediumTopAppBar(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    CashierServiceTheme.colors.mainBackground,
                    Color.Transparent
                ),
                startY = AppBarTokens.GRADIENT_START_Y,
            )
        ),
        colors = colors,
        title = {
            Text(
                text = title,
                style = CashierServiceTheme.typography.h1.copy(fontSize = 24.sp),
                color = CashierServiceTheme.colors.primaryText
            )
        },
        actions = actions,
        navigationIcon = {
            onNavigationIconClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = null,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@com.cashierserviceapp.ui.utils.PreviewLightDark
@Composable
fun PreviewAppBar() = PreviewHelper {
    AppBar(
        title = "Hello",
    )
}