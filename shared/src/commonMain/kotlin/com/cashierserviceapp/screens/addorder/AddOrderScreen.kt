package com.cashierserviceapp.screens.addorder

import androidx.compose.runtime.Composable
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.nav_destination_add_order
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.icons.XOutlined
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import org.jetbrains.compose.resources.stringResource

/**
 * Presented as a full-screen cover rather than a tab, so it closes itself instead of relying on the
 * bottom navigation — which is hidden underneath it.
 */
@Composable
fun AddOrderScreen(onClose: () -> Unit) {
    ScreenWithTitle(
        title = stringResource(Res.string.nav_destination_add_order),
        onBack = onClose,
        navigationIcon = XOutlined,
    ) {
        repeat(50) {
            Text("New order")
        }
    }
}

@PreviewLightDark
@Composable
private fun AddOrderScreenPreview() = PreviewHelper {
    AddOrderScreen(onClose = {})
}
