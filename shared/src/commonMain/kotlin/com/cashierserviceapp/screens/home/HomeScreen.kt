package com.cashierserviceapp.screens.home

import androidx.compose.runtime.Composable
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_home
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen() {

    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_home),
    ) {
        repeat(100) {
            Text("Hello")
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() = PreviewHelper {
    HomeScreen()
}
