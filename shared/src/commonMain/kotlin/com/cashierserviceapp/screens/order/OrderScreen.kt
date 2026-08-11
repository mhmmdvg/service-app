package com.cashierserviceapp.screens.order

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_order
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.topInsetPadding
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrderScreen() {
    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_order)
    ) {
        Text("Hello")
    }
}

@PreviewLightDark
@Composable
fun OrderScreenPreview() = PreviewHelper {
    OrderScreen()
}