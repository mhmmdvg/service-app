package com.cashierserviceapp.screens.order

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_order
import com.cashierserviceapp.ScreenWithTitle
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrderScreen() {
    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_order)
    ) {
        Text("Hello")
    }
}