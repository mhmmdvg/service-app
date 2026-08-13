package com.cashierserviceapp.screens.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_settings
import com.cashierserviceapp.ScreenWithTitle
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen() {
    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_settings)
    ) {
        Text("Hello World")
    }
}