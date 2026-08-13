package com.cashierserviceapp.screens.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_history
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.screens.history.components.HistoryOrderCard
import com.cashierserviceapp.screens.history.components.HistoryOrderCardSkeleton
import com.cashierserviceapp.screens.history.components.HistorySectionHeader
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

private const val SKELETON_ROW_COUNT = 6

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
    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_history),
        scrollable = false,
    ) { innerPadding ->
        // Whatever we last managed to load. A failed refresh carries it forward, so the list the
        // user was reading stays put and the error is shown instead of an empty screen.
        val sections = state.data.orEmpty()

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
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
                        HistoryMessage(
                            title = "Couldn't load history",
                            body = state.message ?: "Something went wrong.",
                            actionLabel = "Try again",
                            onAction = onRetry
                        )
                    }

                    sections.isEmpty() -> item("empty") {
                        HistoryMessage(
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
    }
}

/** Shared shape for the empty and failed states, so neither is a bare line of text. */
@Composable
private fun HistoryMessage(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(top = 72.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = CashierServiceTheme.typography.h4.copy(textAlign = TextAlign.Center),
            color = CashierServiceTheme.colors.primaryText
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = body,
            style = CashierServiceTheme.typography.text2.copy(textAlign = TextAlign.Center),
            color = CashierServiceTheme.colors.secondaryText
        )

        if (actionLabel != null) {
            Spacer(Modifier.height(20.dp))

            Button(label = actionLabel, onClick = onAction, primary = false)
        }
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
