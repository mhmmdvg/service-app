package com.cashierserviceapp.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_home
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
import com.cashierserviceapp.utils.topInsetPadding
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    orderViewModel: HomeViewModel = metroViewModel()
) {
    val orderState by orderViewModel.orderState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_home),
        scrollable = false,
//        scrollBehavior = scrollBehavior,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item("top_spacer") {
                Spacer(
                    Modifier.height(
                        topInsetPadding().calculateTopPadding() +
                                TopAppBarDefaults.MediumAppBarExpandedHeight
                    )
                )
            }

            when (val state = orderState) {
                is Resource.Loading -> item {

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
                            Text(order.customer.name)
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() = PreviewHelper {
    HomeScreen()
}
