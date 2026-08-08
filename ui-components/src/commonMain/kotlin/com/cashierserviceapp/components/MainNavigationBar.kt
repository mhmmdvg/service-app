package com.cashierserviceapp.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.theme.CashierServiceTheme
import com.cashierserviceapp.theme.PreviewHelper
import com.cashierserviceapp.utils.PreviewLightDark
import com.cashierserviceapp.utils.WidePreviewLightDark
import com.composables.HomeFilled
import com.composables.HomeOutlined
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class MainNavDestination<T : Any>(
    val label: String,
    val icon: ImageVector,
    val route: T,
    val iconSelected: ImageVector = icon
)

@Composable
private fun MainNavigationButton(
    iconOutlined: ImageVector,
    iconFilled: ImageVector,
    contentDescription: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor by animateColorAsState(
        if (selected) CashierServiceTheme.colors.primaryText
        else CashierServiceTheme.colors.secondaryText
    )

    Icon(
        modifier = modifier
            .clip(CashierServiceTheme.shapes.roundedCornerMd)
            .selectable(
                selected = selected,
                enabled = true,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(10.dp)
            .size(28.dp),
        imageVector = if (selected) iconFilled else iconOutlined,
        contentDescription = contentDescription,
        tint = iconColor,
    )
}

@Composable
fun <T : Any> MainNavigationBar(
    currentDestination: MainNavDestination<T>?,
    destinations: List<MainNavDestination<T>>,
    onSelect: (MainNavDestination<T>) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        destinations.forEach { destination ->
            MainNavigationButton(
                iconOutlined = destination.icon,
                iconFilled = destination.iconSelected,
                contentDescription = destination.label,
                selected = destination == currentDestination,
                onClick = { onSelect(destination) },
                modifier = Modifier
                    .widthIn(max = 150.dp)
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            )
        }
    }
}

@PreviewLightDark
@WidePreviewLightDark
@Composable
private fun MainNavigationBarPreview() = PreviewHelper(paddingEnabled = false) {
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
    MainNavigationBar(
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
        onSelect = { currentDestination = it },
    )
}
