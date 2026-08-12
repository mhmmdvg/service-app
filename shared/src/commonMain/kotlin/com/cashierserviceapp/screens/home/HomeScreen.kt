package com.cashierserviceapp.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_home
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.domain.models.Post
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.topInsetPadding
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    orderViewModel: HomeViewModel = metroViewModel()
) {
    val orderList by orderViewModel.orderState.collectAsStateWithLifecycle()
    val postList by orderViewModel.postState.collectAsStateWithLifecycle()

    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_home),
        scrollable = false
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
            items(postList, key = { post -> post.id }) { post ->
                PostRow(post)
            }
        }
    }
}

@Composable
private fun PostRow(post: Post) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = post.title,
            style = CashierServiceTheme.typography.text1,
            maxLines = 2
        )
        Text(
            text = post.body,
            color = CashierServiceTheme.colors.secondaryText,
            style = CashierServiceTheme.typography.text2,
            maxLines = 3
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() = PreviewHelper {
    HomeScreen()
}
