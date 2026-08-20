package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.order_detail_device_sheet_body
import cashierserviceapp.shared.generated.resources.order_detail_device_sheet_title
import com.cashierserviceapp.screens.order.components.OrderStatusChip
import com.cashierserviceapp.screens.orderdetail.OrderDetailItemUiModel
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import org.jetbrains.compose.resources.stringResource

/**
 * Asks which device the update is for, on orders that took in more than one.
 *
 * Skipped entirely when there's only one — see the caller, which goes straight to the status list
 * rather than making someone confirm the obvious.
 */
@Composable
fun DevicePickerSheet(
    items: List<OrderDetailItemUiModel>,
    onSelect: (OrderDetailItemUiModel) -> Unit,
    onDismiss: () -> Unit,
) {
    BottomSheet(onDismissRequest = onDismiss) { hide ->
        Spacer(Modifier.height(20.dp))

        Column(Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(Res.string.order_detail_device_sheet_title),
                style = CashierServiceTheme.typography.h3,
                color = CashierServiceTheme.colors.primaryText
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.order_detail_device_sheet_body),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )

            Spacer(Modifier.height(16.dp))

            items.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(CashierServiceTheme.shapes.roundedCornerLg)
                        .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
                        .clickable(role = Role.Button) {
                            hide()
                            onSelect(item)
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.deviceName,
                        modifier = Modifier.weight(1f),
                        style = CashierServiceTheme.typography.text1,
                        maxLines = 1
                    )

                    Spacer(Modifier.width(4.dp))

                    OrderStatusChip(item.status)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}
