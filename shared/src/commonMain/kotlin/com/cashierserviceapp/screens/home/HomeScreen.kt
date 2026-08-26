package com.cashierserviceapp.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.action_see_all
import cashierserviceapp.shared.generated.resources.action_try_again
import cashierserviceapp.shared.generated.resources.error_generic
import cashierserviceapp.shared.generated.resources.home_attention_title
import cashierserviceapp.shared.generated.resources.home_empty_body
import cashierserviceapp.shared.generated.resources.home_empty_title
import cashierserviceapp.shared.generated.resources.home_greeting
import cashierserviceapp.shared.generated.resources.home_orders_load_failed
import cashierserviceapp.shared.generated.resources.home_scan_qr
import cashierserviceapp.shared.generated.resources.home_stat_all_income
import cashierserviceapp.shared.generated.resources.home_stat_in_progress
import cashierserviceapp.shared.generated.resources.nav_destination_home
import cashierserviceapp.shared.generated.resources.search_placeholder
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.navigation.SharedElementKey
import com.cashierserviceapp.navigation.sharedElementBounds
import com.cashierserviceapp.ui.components.ContentMessage
import com.cashierserviceapp.screens.history.components.HistoryOrderCardSkeleton
import com.cashierserviceapp.screens.home.components.AttentionCard
import com.cashierserviceapp.screens.home.components.ScanSheet
import com.cashierserviceapp.screens.home.components.StatTile
import com.cashierserviceapp.screens.home.components.StatTileSkeleton
import com.cashierserviceapp.ui.components.SearchFieldButton
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.icons.ScanOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

private const val SKELETON_ROW_COUNT = 3

@Composable
fun HomeScreen(
    onOpenOrders: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenOrder: (String) -> Unit = {},
    viewModel: HomeViewModel = metroViewModel(),
) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val trackingState by viewModel.trackingState.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    var scanning by remember { mutableStateOf(false) }

    HomeContent(
        state = homeState,
        userName = userName,
        onSearchClick = onOpenSearch,
        onScanClick = { scanning = true },
        onRetry = viewModel::retry,
        onOpenOrders = onOpenOrders,
        onOpenOrder = onOpenOrder,
    )

    if (scanning) {
        ScanSheet(
            state = trackingState,
            onLookup = viewModel::track,
            onDismiss = {
                scanning = false
                viewModel.dismissTracking()
            }
        )
    }
}

@Composable
private fun HomeContent(
    state: Resource<HomeSnapshot>,
    userName: String?,
    onSearchClick: () -> Unit,
    onScanClick: () -> Unit,
    onRetry: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenOrder: (String) -> Unit = {},
) {
    val snapshot = state.data

    ScreenWithTitle(
        title = userName?.firstName()
            ?.let { stringResource(Res.string.home_greeting, it) }
            ?: stringResource(Res.string.nav_destination_home),
        scrollable = false,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item("top_spacer") {
                Spacer(Modifier.height(innerPadding.calculateTopPadding() - 5.dp))
            }

            item("search") {
                SearchFieldButton(
                    // Half of the morph into the search screen; the field there carries the twin.
                    modifier = Modifier.sharedElementBounds(SharedElementKey.SearchPill),
                    placeholder = stringResource(Res.string.search_placeholder),
                    onClick = onSearchClick,
                    trailing = { ScanButton(onClick = onScanClick) }
                )
            }

            item("stats") {
                Spacer(Modifier.height(4.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (snapshot == null) {
                        StatTileSkeleton(Modifier.weight(1f))
                        StatTileSkeleton(Modifier.weight(1f))
                    } else {
                        StatTile(
                            value = snapshot.orderCount.toString(),
                            label = stringResource(Res.string.home_stat_in_progress),
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            // Stays a dash until the server can report it.
                            value = snapshot.incomeLabel,
                            label = stringResource(Res.string.home_stat_all_income),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            attentionSection(
                state = state,
                snapshot = snapshot,
                onRetry = onRetry,
                onOpenOrders = onOpenOrders,
                onOpenOrder = onOpenOrder
            )

            item("bottom_spacer") { Spacer(Modifier.height(16.dp)) }
        }
    }
}

private fun LazyListScope.attentionSection(
    state: Resource<HomeSnapshot>,
    snapshot: HomeSnapshot?,
    onRetry: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenOrder: (String) -> Unit,
) {
    val attention = snapshot?.attention.orEmpty()

    item("attention_header") {
        SectionHeader(
            title = stringResource(Res.string.home_attention_title),
            // Against the queue's true size, not the pages loaded — there is more to see even
            // when only one page has arrived.
            action = if ((snapshot?.orderCount ?: 0) > ATTENTION_PREVIEW_COUNT) {
                stringResource(Res.string.action_see_all)
            } else {
                null
            },
            onAction = onOpenOrders
        )
    }

    when {
        state is Resource.Loading && snapshot == null -> items(SKELETON_ROW_COUNT) {
            HistoryOrderCardSkeleton()
        }

        state is Resource.Error && snapshot == null -> item("error") {
            ContentMessage(
                title = stringResource(Res.string.home_orders_load_failed),
                body = state.message ?: stringResource(Res.string.error_generic),
                actionLabel = stringResource(Res.string.action_try_again),
                onAction = onRetry
            )
        }

        attention.isEmpty() -> item("empty") {
            ContentMessage(
                title = stringResource(Res.string.home_empty_title),
                body = stringResource(Res.string.home_empty_body)
            )
        }

        else -> items(
            items = attention.take(ATTENTION_PREVIEW_COUNT),
            key = { it.id }
        ) { row ->
            AttentionCard(row = row, onClick = { onOpenOrder(row.id) })
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = CashierServiceTheme.typography.text2.copy(fontWeight = FontWeight.SemiBold),
            color = CashierServiceTheme.colors.secondaryText
        )

        if (action != null) {
            Text(
                text = action,
                modifier = Modifier.clickable(
                    interactionSource = null,
                    indication = null,
                    role = Role.Button,
                    onClick = onAction
                ),
                style = CashierServiceTheme.typography.text2.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = CashierServiceTheme.colors.primaryBackground
            )
        }
    }
}

@Composable
private fun ScanButton(onClick: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(CashierServiceTheme.colors.mainBackground)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ScanOutlined,
            contentDescription = stringResource(Res.string.home_scan_qr),
            modifier = Modifier.size(19.dp),
            tint = CashierServiceTheme.colors.primaryText
        )
    }
}

/** Greetings use the given name — "Hi, Muhammad Vikri" reads like a form letter. */
private fun String.firstName(): String? =
    trim().split(" ").firstOrNull()?.takeIf { it.isNotBlank() }

private val previewSnapshot = HomeSnapshot(
    attention = listOf(
        AttentionRow("1", "Pelanggan 1", "SV-1786086981", 2, "Rp 0", daysWaiting = 7),
        AttentionRow("2", "Rina Wijaya", "SV-1786641253", 1, "Rp 350.000", daysWaiting = 1),
        AttentionRow("3", "Testing", "SV-1786641407", 1, "Rp 0", daysWaiting = 0),
        AttentionRow("4", "Budi Santoso", "SV-1786641500", 3, "Rp 0", daysWaiting = 0),
    )
)

@PreviewLightDark
@Composable
private fun HomeScreenPreview() = PreviewHelper(paddingEnabled = false) {
    HomeContent(
        state = Resource.Success(previewSnapshot),
        userName = "Muhammad Vikri",
        onSearchClick = {},
        onScanClick = {},
        onRetry = {},
        onOpenOrders = {}
    )
}

@PreviewLightDark
@Composable
private fun HomeScreenIncomePreview() = PreviewHelper(paddingEnabled = false) {
    HomeContent(
        state = Resource.Success(previewSnapshot.copy(incomeLabel = "Rp 12.450.000")),
        userName = "Muhammad Vikri",
        onSearchClick = {},
        onScanClick = {},
        onRetry = {},
        onOpenOrders = {}
    )
}

@PreviewLightDark
@Composable
private fun HomeScreenLoadingPreview() = PreviewHelper(paddingEnabled = false) {
    HomeContent(
        state = Resource.Loading(),
        userName = null,
        onSearchClick = {},
        onScanClick = {},
        onRetry = {},
        onOpenOrders = {}
    )
}
