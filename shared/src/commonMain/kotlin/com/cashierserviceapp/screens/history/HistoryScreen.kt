package com.cashierserviceapp.screens.history

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_history
import com.cashierserviceapp.ScreenWithTitle
import org.jetbrains.compose.resources.stringResource

@Composable
fun HistoryScreen() {
    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_history),
    ) {
        Text("Hello World")
    }
}