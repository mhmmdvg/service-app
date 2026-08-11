package com.cashierserviceapp.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_home
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.topInsetPadding
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen() {
    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_home),
        scrollable = false
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item("top_spacer") {
                Spacer(Modifier.height(topInsetPadding().calculateTopPadding() + 100.dp))
            }
            items(100) {
                Text("Hello world")
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() = PreviewHelper {
    HomeScreen()
}
