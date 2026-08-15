package com.cashierserviceapp.screens.order.components

import androidx.compose.runtime.Composable
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.order_status_completed
import cashierserviceapp.shared.generated.resources.order_status_diagnosing
import cashierserviceapp.shared.generated.resources.order_status_in_progress
import cashierserviceapp.shared.generated.resources.order_status_received
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.ui.components.Chip
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import org.jetbrains.compose.resources.stringResource

/**
 * The one place a repair status turns into a label and a colour. Lives in `shared` rather than
 * `ui-components` because it knows about [OrderStatus], which that module can't see.
 */
@Composable
fun OrderStatusChip(status: OrderStatus) {
    Chip(label = status.label(), color = status.color())
}

@Composable
fun OrderStatus.label(): String = stringResource(
    when (this) {
        OrderStatus.RECEIVED -> Res.string.order_status_received
        OrderStatus.DIAGNOSING -> Res.string.order_status_diagnosing
        OrderStatus.IN_PROGRESS -> Res.string.order_status_in_progress
        OrderStatus.COMPLETED -> Res.string.order_status_completed
    }
)

@Composable
fun OrderStatus.color() = when (this) {
    OrderStatus.RECEIVED -> CashierServiceTheme.colors.blueText
    OrderStatus.DIAGNOSING -> CashierServiceTheme.colors.orangeText
    OrderStatus.IN_PROGRESS -> CashierServiceTheme.colors.purpleText
    OrderStatus.COMPLETED -> CashierServiceTheme.colors.greenText
}

@PreviewLightDark
@Composable
private fun OrderStatusChipPreview() = PreviewHelper {
    OrderStatus.entries.forEach { OrderStatusChip(it) }
}
