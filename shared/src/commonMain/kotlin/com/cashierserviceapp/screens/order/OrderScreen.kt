package com.cashierserviceapp.screens.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_order
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.screens.order.components.OrderCard
import com.cashierserviceapp.screens.order.components.OrderCardSkeleton
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrderScreen(
    viewModel: OrderViewModel = metroViewModel(),
) {
    val orderState by viewModel.orderState.collectAsStateWithLifecycle()

    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_order),
        scrollable = false,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item("top_spacer") {
                Spacer(Modifier.height(innerPadding.calculateTopPadding() - 5.dp))
            }

            when (val state = orderState) {
                is Resource.Loading -> items(10) {
                    OrderCardSkeleton()
                }

                is Resource.Error -> item {
                    Box(Modifier.fillMaxSize()) {
                        Text(state.message ?: "")
                    }
                }

                is Resource.Success -> {
                    val orderData = state.data ?: emptyList()

                    if (orderData.isNotEmpty()) {
                        items(
                            items = orderData,
                            key = { it.id }
                        ) { order ->
                            OrderCard(
                                name = order.customerName,
                                code = order.orderCode,
                                status = order.status,
                                onClick = {
                                    println("Clicked ${order.orderCode}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun OrderScreenPreview() = PreviewHelper {
    OrderScreen()
}