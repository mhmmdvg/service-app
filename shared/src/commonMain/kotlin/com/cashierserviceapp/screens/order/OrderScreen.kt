package com.cashierserviceapp.screens.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import cashierserviceapp.shared.generated.resources.action_try_again
import cashierserviceapp.shared.generated.resources.error_generic
import cashierserviceapp.shared.generated.resources.nav_destination_order
import cashierserviceapp.shared.generated.resources.order_list_empty_body
import cashierserviceapp.shared.generated.resources.order_list_empty_title
import cashierserviceapp.shared.generated.resources.order_list_load_failed
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.screens.order.components.OrderCard
import com.cashierserviceapp.screens.order.components.OrderCardSkeleton
import com.cashierserviceapp.ui.components.ContentMessage
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.PullThreshold
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrderScreen(
    onOpenOrder: (String) -> Unit = {},
    viewModel: OrderViewModel = metroViewModel(),
) {
    val orderState by viewModel.orderState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    OrderContent(
        state = orderState,
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry,
        onOpenOrder = onOpenOrder,
    )
}

@Composable
private fun OrderContent(
    state: Resource<List<OrderRow>>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onOpenOrder: (String) -> Unit = {},
) {
    val pullState = rememberPullToRefreshState()

    Box(Modifier.fillMaxSize()) {
        ScreenWithTitle(
            title = stringResource(Res.string.nav_destination_order),
            scrollable = false,
            modifier = Modifier.pullToRefresh(
                isRefreshing = isRefreshing,
                state = pullState,
                threshold = PullThreshold,
                onRefresh = onRefresh
            )
        ) { innerPadding ->
            val orderData = state.data.orEmpty()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item("top_spacer") {
                    Spacer(Modifier.height(innerPadding.calculateTopPadding() - 5.dp))
                }

                when {
                    state is Resource.Loading && orderData.isEmpty() -> items(6) {
                        OrderCardSkeleton()
                    }

                    state is Resource.Error && orderData.isEmpty() -> item {
                        ContentMessage(
                            title = stringResource(Res.string.order_list_load_failed),
                            body = state.message ?: stringResource(Res.string.error_generic),
                            actionLabel = stringResource(Res.string.action_try_again),
                            onAction = onRetry
                        )
                    }

                    orderData.isEmpty() -> item("empty") {
                        ContentMessage(
                            title = stringResource(Res.string.order_list_empty_title),
                            body = stringResource(Res.string.order_list_empty_body)
                        )
                    }

                    else ->
                        items(
                            items = orderData,
                            key = { it.id }
                        ) { order ->
                            OrderCard(
                                name = order.customerName,
                                code = order.orderCode,
                                itemsCount = order.itemsCount,
                                status = order.status,
                                time = order.timeLabel,
                                onClick = { onOpenOrder(order.id) }
                            )
                        }
                }
            }
        }

        PullToRefreshDefaults.Indicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
            isRefreshing = isRefreshing,
            state = pullState,
        )
    }
}

private val previewOrder = listOf(
    OrderRow(
        id = "TEST-100",
        customerName = "Vikri",
        orderCode = "SV-12345",
        itemsCount = 2,
        status = OrderStatus.IN_PROGRESS,
        timeLabel = "14:32"
    ),
    OrderRow(
        id = "TEST-200",
        customerName = "Testing",
        orderCode = "SV-89656",
        itemsCount = 1,
        status = OrderStatus.IN_PROGRESS,
        timeLabel = "Yesterday"
    ),
    OrderRow(
        id = "TEST-300",
        customerName = "Enji",
        orderCode = "SV-877234",
        itemsCount = 3,
        status = OrderStatus.IN_PROGRESS,
        timeLabel = "7 Aug"
    )
)

@PreviewLightDark
@Composable
private fun OrderScreenPreview() = PreviewHelper(paddingEnabled = false) {
    OrderContent(
        state = Resource.Success(previewOrder),
        isRefreshing = false,
        onRefresh = {},
        onRetry = {},
    )
}

@PreviewLightDark
@Composable
private fun OrderScreenEmptyPreview() = PreviewHelper(paddingEnabled = false) {
    OrderContent(
        state = Resource.Success(emptyList<OrderRow>()),
        isRefreshing = false,
        onRefresh = {},
        onRetry = {},
    )
}

@PreviewLightDark
@Composable
private fun OrderScreenLoadingPreview() = PreviewHelper(paddingEnabled = false) {
    OrderContent(
        state = Resource.Loading(),
        isRefreshing = false,
        onRefresh = {},
        onRetry = {},
    )
}

@PreviewLightDark
@Composable
private fun OrderScreenErrorPreview() = PreviewHelper(paddingEnabled = false) {
    OrderContent(
        state = Resource.Error("Couldn't reach the server. Check your connection and try again."),
        isRefreshing = false,
        onRefresh = {},
        onRetry = {},
    )
}