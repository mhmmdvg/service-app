package com.cashierserviceapp.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.icons.*
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

@Composable
private fun NavRailMenuItem(
    icon: ImageVector,
    iconFilled: ImageVector,
    label: String,
    selected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor by animateColorAsState(
        if (selected) CashierServiceTheme.colors.primaryText
        else CashierServiceTheme.colors.secondaryText
    )

    val itemModifier = modifier
        .clip(CashierServiceTheme.shapes.roundedCornerLg)
        .fillMaxWidth()
        .selectable(
            selected = selected,
            enabled = true,
            role = Role.Tab,
            onClick = onClick,
        )
        .padding(12.dp)

    val arrangement = Arrangement.spacedBy(8.dp)

    SharedTransitionLayout(
        modifier = itemModifier
    ) {
        AnimatedContent(expanded, modifier = Modifier.fillMaxWidth()) { isExpanded ->
            val itemContent = @Composable {
                Icon(
                    modifier = Modifier.size(28.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "icon"),
                            animatedVisibilityScope = this@AnimatedContent
                        ),
                    imageVector = if (selected) iconFilled else icon,
                    contentDescription = null,
                    tint = contentColor
                )

                Text(
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "text"),
                        animatedVisibilityScope = this@AnimatedContent
                    ),
                    text = label,
                    style = CashierServiceTheme.typography.text2,
                    color = contentColor
                )
            }

            if (isExpanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = arrangement,
                ) {
                    itemContent()
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = arrangement,
                ) {
                    itemContent()
                }
            }
        }
    }
}

@Composable
fun <T : Any> MainNavigationRail(
    currentDestination: MainNavDestination<T>?,
    destinations: List<MainNavDestination<T>>,
    onSelect: (MainNavDestination<T>) -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val width by animateDpAsState(
        if (expanded) 220.dp else 140.dp,
        animationSpec = spring(
            visibilityThreshold = Dp.VisibilityThreshold,
            stiffness = Spring.StiffnessLow,
        )
    )

    Column(
        modifier
            .width(width)
            .padding(12.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        destinations.forEach { destination ->
            NavRailMenuItem(
                icon = destination.icon,
                iconFilled = destination.iconSelected,
                label = destination.label,
                selected = destination == currentDestination,
                expanded = expanded,
                onClick = { onSelect(destination) }
            )
        }
    }
}

private class ExpandedPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
    override fun getDisplayName(index: Int) = if (index == 0) "collapsed" else "expanded"
}

@PreviewLightDark
@Composable
private fun MainNavigationRailPreview(
    @PreviewParameter(ExpandedPreviewProvider::class) expanded: Boolean,
) = PreviewHelper(paddingEnabled = false) {
    val navRailPreviewDestination = listOf(
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
            label = "Add Order",
            icon = PlusOutlined,
            iconSelected = PlusFilled,
            route = "Add Order"
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
        )
    )

    var currentDestination by remember { mutableStateOf(navRailPreviewDestination.first()) }

    MainNavigationRail(
        currentDestination = currentDestination,
        destinations = navRailPreviewDestination,
        onSelect = { currentDestination = it },
        expanded = expanded,
    )
}