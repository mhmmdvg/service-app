package com.cashierserviceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.lerp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.ui.icons.ChevronLeftOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    expandedTitleSize: TextUnit = 24.sp,
    collapsedTitleSize: TextUnit = 17.sp,
    expandedTitleWeight: FontWeight = FontWeight.ExtraBold,
    collapsedTitleWeight: FontWeight = FontWeight.Bold,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    /**
     * Skips the tall state entirely: the bar starts, stays and ends at its collapsed height with a
     * small centred title. For screens the title is a label on rather than the headline of — an
     * order code above its own detail, say, where a 112.dp banner would push the content down for
     * no gain.
     */
    alwaysCollapsed: Boolean = false,
) {
    val density = LocalDensity.current
    val barHeight = if (alwaysCollapsed) {
        TopAppBarDefaults.TopAppBarExpandedHeight
    } else {
        TopAppBarDefaults.MediumAppBarExpandedHeight
    }
    val appBarHeightPx = with(density) { barHeight.toPx() }

    // Content scrolls underneath rather than being clipped, so the bar fades into the background
    // over its lower half instead of ending on a hard edge.
    val backdrop = modifier.background(
        brush = Brush.verticalGradient(
            colors = listOf(
                CashierServiceTheme.colors.mainBackground,
                Color.Transparent
            ),
            startY = appBarHeightPx / 2,
            endY = appBarHeightPx,
        )
    )

    val navigationSlot: @Composable () -> Unit = {
        onNavigationIconClick?.let {
            CircleIconButton(
                modifier = Modifier
                    .padding(start = 8.dp),
                onClick = onNavigationIconClick,
                icon = navigationIcon,
                contentDescription = title,
            )
        }
    }

    if (alwaysCollapsed) {
        // A single row — back button, centred title, actions — which is the shape of a SwiftUI
        // inline navigation bar. The medium bar can't do this at any height: it always stacks the
        // title in a second row beneath the icons, so shrinking it just leaves the title stranded
        // below a lone chevron.
        CenterAlignedTopAppBar(
            modifier = backdrop,
            colors = colors,
            title = {
                Text(
                    text = title,
                    style = CashierServiceTheme.typography.h1.copy(
                        fontSize = collapsedTitleSize,
                        fontWeight = collapsedTitleWeight,
                    ),
                    color = CashierServiceTheme.colors.primaryText,
                    maxLines = 1,
                )
            },
            navigationIcon = navigationSlot,
            actions = actions,
            scrollBehavior = scrollBehavior,
        )
        return
    }

    // Slides the title from start-aligned to centred as the bar collapses. collapsedFraction is
    // read inside align(), which runs during layout, so a scroll re-positions the title without
    // recomposing the app bar.
    val titleAlignment = remember(scrollBehavior) {
        object : Alignment.Horizontal {
            override fun align(size: Int, space: Int, layoutDirection: LayoutDirection): Int {
                val fraction = scrollBehavior?.state?.collapsedFraction ?: 0f
                val start = Alignment.Start.align(size, space, layoutDirection)
                val center = Alignment.CenterHorizontally.align(size, space, layoutDirection)
                return start + ((center - start) * fraction).roundToInt()
            }
        }
    }

    // Flexible variant purely because it's the one that exposes titleHorizontalAlignment;
    // MediumTopAppBar hardcodes Start. Both default to a 112.dp expanded height, so the bar
    // itself is unchanged.
    MediumFlexibleTopAppBar(
        modifier = backdrop,
        colors = colors,
        titleHorizontalAlignment = titleAlignment,
        title = {
            // This lambda renders twice — once in the collapsed row, once in the expanded row,
            // cross-faded. Interpolating the size here shrinks the title smoothly, whereas
            // Material's own small/large styles would snap between two fixed sizes (and were
            // being overridden by the hardcoded fontSize this replaces).
            val fraction = scrollBehavior?.state?.collapsedFraction ?: 0f
            Text(
                text = title,
                style = CashierServiceTheme.typography.h1.copy(
                    fontSize = lerp(expandedTitleSize, collapsedTitleSize, fraction),
                    // Interpolated for the same reason as the size, but note it only *glides* on a
                    // variable font. InterSans registers discrete weights, so intermediate values
                    // snap to the nearest registered one mid-scroll.
                    fontWeight = lerp(expandedTitleWeight, collapsedTitleWeight, fraction),
                ),
                color = CashierServiceTheme.colors.primaryText,
                maxLines = 1,
            )
        },
        actions = actions,
        navigationIcon = navigationSlot,
        scrollBehavior = scrollBehavior,
    )
}

@PreviewLightDark
@Composable
fun PreviewAppBar() = PreviewHelper {
    AppBar(
        title = "Hello",
    )
}