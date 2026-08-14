package com.cashierserviceapp.screens.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_history
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.screens.history.components.HistoryOrderCard
import com.cashierserviceapp.screens.history.components.HistoryOrderCardSkeleton
import com.cashierserviceapp.screens.history.components.HistorySectionHeader
import com.cashierserviceapp.ui.components.ContentMessage
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.PullThreshold
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

private const val SKELETON_ROW_COUNT = 6

/**
 * How far to pull before letting go refreshes. Up from the 80.dp default, which fired on almost any
 * downward flick — the list is usually short enough that it isn't scrollable, so every drag lands
 * straight in overscroll with no scrolling phase to get through first.
 *
 * Note this is *half* the distance the finger actually travels: the modifier scales drag by a
 * private `DragMultiplier` of 0.5 before comparing against the threshold. So 120.dp here means
 * roughly 240.dp of pull, against 160.dp for the default.
 */

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = metroViewModel(),
) {
    val historyState by viewModel.historyState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    HistoryContent(
        state = historyState,
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HistoryContent(
    state: Resource<List<HistorySection>>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
) {
    val pullState = rememberPullToRefreshState()

    Box(Modifier.fillMaxSize()) {
        ScreenWithTitle(
            title = stringResource(Res.string.nav_destination_history),
            scrollable = false,
            // Deliberately handed to ScreenWithTitle rather than wrapping the list in a
            // PullToRefreshBox. ScreenWithTitle applies this modifier *before* the Scaffold's own
            // nestedScroll, which makes pull-to-refresh the OUTER connection of the two.
            //
            // That ordering is the whole point. exitUntilCollapsedScrollBehavior only re-expands
            // the collapsed title from the downward scroll left over once the list is at its top,
            // and nested scroll offers that leftover to inner connections first. Nested inside,
            // pull-to-refresh ate every scrap of it and the title could never expand again — only
            // dragging the app bar itself worked, since Material3 gives it a separate `draggable`.
            // Outside, the app bar expands first and only what it doesn't want reaches the pull.
            modifier = Modifier.pullToRefresh(
                isRefreshing = isRefreshing,
                state = pullState,
                threshold = PullThreshold,
                onRefresh = onRefresh
            )
        ) { innerPadding ->
            // Whatever we last managed to load. A failed refresh carries it forward, so the list
            // the user was reading stays put and the error is shown instead of an empty screen.
            val sections = state.data.orEmpty()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item("top_spacer") {
                    Spacer(Modifier.height(innerPadding.calculateTopPadding() - 5.dp))
                }

                when {
                    state is Resource.Loading && sections.isEmpty() -> items(SKELETON_ROW_COUNT) {
                        HistoryOrderCardSkeleton()
                    }

                    state is Resource.Error && sections.isEmpty() -> item("error") {
                        ContentMessage(
                            title = "Couldn't load history",
                            body = state.message ?: "Something went wrong.",
                            actionLabel = "Try again",
                            onAction = onRetry
                        )
                    }

                    sections.isEmpty() -> item("empty") {
                        ContentMessage(
                            title = "No completed orders yet",
                            body = "Once every device on an order is finished, the order moves " +
                                    "here."
                        )
                    }

                    else -> sections.forEach { section ->
                        stickyHeader(key = "header_${section.label}") {
                            HistorySectionHeader(
                                label = section.label,
                                total = section.totalLabel
                            )
                        }

                        items(
                            items = section.rows,
                            key = { it.id }
                        ) { row ->
                            HistoryOrderCard(
                                name = row.customerName,
                                code = row.orderCode,
                                itemsCount = row.itemsCount,
                                total = row.totalLabel,
                                time = row.timeLabel,
                                onClick = { println("Clicked ${row.orderCode}") }
                            )
                        }
                    }
                }

                item("bottom_spacer") {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        // Drawn as a sibling above the screen because the gesture now lives outside the Scaffold.
        // Inset from the top so it doesn't ride up under the status bar.
        PullToRefreshDefaults.Indicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
//                .padding(top = 8.dp),
            isRefreshing = isRefreshing,
            state = pullState
        )
    }
}


private val previewSections = listOf(
    HistorySection(
        label = "Today",
        date = null,
        rows = listOf(
            HistoryRow(
                id = "1",
                customerName = "Rina Wijaya",
                orderCode = "SV-1786641253",
                itemsCount = 1,
                totalCost = 350_000,
                totalLabel = "Rp 350.000",
                timeLabel = "14:32"
            ),
            HistoryRow(
                id = "2",
                customerName = "Bambang Kusuma",
                orderCode = "SV-1786641190",
                itemsCount = 2,
                totalCost = 1_250_000,
                totalLabel = "Rp 1.250.000",
                timeLabel = "11:08"
            ),
        )
    ),
    HistorySection(
        label = "13 August 2026",
        date = null,
        rows = listOf(
            HistoryRow(
                id = "3",
                customerName = "Pelanggan 1",
                orderCode = "SV-1786086981",
                itemsCount = 1,
                totalCost = 90_000,
                totalLabel = "Rp 90.000",
                timeLabel = "09:05"
            ),
        )
    ),
)

@PreviewLightDark
@Composable
private fun HistoryScreenPreview() = PreviewHelper(paddingEnabled = false) {
    HistoryContent(
        state = Resource.Success(previewSections),
        isRefreshing = false,
        onRefresh = {},
        onRetry = {}
    )
}

@PreviewLightDark
@Composable
private fun HistoryScreenEmptyPreview() = PreviewHelper(paddingEnabled = false) {
    HistoryContent(
        state = Resource.Success(emptyList()),
        isRefreshing = false,
        onRefresh = {},
        onRetry = {}
    )
}

@PreviewLightDark
@Composable
private fun HistoryScreenLoadingPreview() = PreviewHelper(paddingEnabled = false) {
    HistoryContent(
        state = Resource.Loading(),
        isRefreshing = false,
        onRefresh = {},
        onRetry = {}
    )
}

@PreviewLightDark
@Composable
private fun HistoryScreenErrorPreview() = PreviewHelper(paddingEnabled = false) {
    HistoryContent(
        state = Resource.Error("Couldn't reach the server. Check your connection and try again."),
        isRefreshing = false,
        onRefresh = {},
        onRetry = {}
    )
}
