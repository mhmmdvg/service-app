package com.cashierserviceapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.cashierserviceapp.ui.icons.HomeFilled
import com.cashierserviceapp.ui.icons.HomeOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.ui.utils.WidePreviewLightDark

data class MainNavDestination<T : Any>(
    val label: String,
    val icon: ImageVector,
    val route: T,
    val iconSelected: ImageVector = icon
)

private const val PressedScale = 0.85f

@Composable
private fun MainNavigationButton(
    modifier: Modifier = Modifier,
    iconOutlined: ImageVector,
    iconFilled: ImageVector,
    selectedColor: Color,
    contentDescription: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val iconColor by animateColorAsState(
        if (selected) selectedColor
        else CashierServiceTheme.colors.secondaryText
    )

    val interactionSource = remember { MutableInteractionSource() }
    // Driven from a coroutine and read only inside the graphicsLayer lambda below, so every
    // frame of the animation stays in the draw phase instead of recomposing the button.
    val scale = remember { Animatable(1f) }

    LaunchedEffect(interactionSource) {
        val presses = mutableListOf<PressInteraction.Press>()
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> presses.add(interaction)
                is PressInteraction.Release -> presses.remove(interaction.press)
                is PressInteraction.Cancel -> presses.remove(interaction.press)
            }
            val pressed = presses.isNotEmpty()
            // Animatable serializes animations itself, so a launch per event lets a release
            // interrupt an in-flight press instead of queueing behind it.
            this@LaunchedEffect.launch {
                scale.animateTo(
                    targetValue = if (pressed) PressedScale else 1f,
                    animationSpec = if (pressed) {
                        tween(
                            durationMillis = 120,
                            easing = LinearOutSlowInEasing
                        )
                    } else {
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    }
                )
            }
        }
    }

    Icon(
        modifier = modifier
            .graphicsLayer {
                val current = scale.value
                scaleX = current
                scaleY = current
            }
            .clip(CashierServiceTheme.shapes.roundedCornerMd)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
//                indication = LocalIndication.current,
                indication = null,
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
    modifier: Modifier = Modifier,
    currentDestination: MainNavDestination<T>?,
    destinations: List<MainNavDestination<T>>,
    selectedColor: Color = CashierServiceTheme.colors.primaryText,
    onSelect: (MainNavDestination<T>) -> Unit,
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
                selectedColor = selectedColor,
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
private fun MainNavigationBarPreview() =
    PreviewHelper(paddingEnabled = false) {
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
