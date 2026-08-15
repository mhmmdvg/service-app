package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.order_detail_status_sheet_body
import cashierserviceapp.shared.generated.resources.order_detail_status_sheet_title
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.screens.order.components.color
import com.cashierserviceapp.screens.order.components.label
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import org.jetbrains.compose.resources.stringResource

/**
 * Moves one device along the repair.
 *
 * Every status is selectable, in either direction — a device that was marked done by mistake has to
 * be able to go back, and the server records each move in the item's history either way.
 */
@Composable
fun StatusPickerSheet(
    deviceName: String,
    current: OrderStatus,
    onSelect: (OrderStatus) -> Unit,
    onDismiss: () -> Unit,
) {
    BottomSheet(onDismissRequest = onDismiss) { hide ->
        Spacer(Modifier.height(20.dp))

        Column(Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(Res.string.order_detail_status_sheet_title),
                style = CashierServiceTheme.typography.h3,
                color = CashierServiceTheme.colors.primaryText
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "$deviceName · ${stringResource(Res.string.order_detail_status_sheet_body)}",
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )

            Spacer(Modifier.height(16.dp))

            OrderStatus.entries.forEach { status ->
                StatusOption(
                    status = status,
                    selected = status == current,
                    onClick = {
                        // Hide first: the sheet is dismissed on its own animation, and the reload
                        // this kicks off would otherwise redraw the list underneath mid-slide.
                        hide()
                        if (status != current) onSelect(status)
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun StatusOption(
    status: OrderStatus,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val accent = status.color()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(
                if (selected) accent.copy(alpha = 0.12f)
                else CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f)
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // A filled dot in the status' own colour doubles as the selection mark, so the row needs
        // no separate radio control.
        Box(
            Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(if (selected) accent else CashierServiceTheme.colors.mainBackground)
                .border(width = 2.dp, color = accent, shape = CircleShape)
        )

        Text(
            text = status.label(),
            style = CashierServiceTheme.typography.text1,
            color = CashierServiceTheme.colors.primaryText
        )
    }
}

@PreviewLightDark
@Composable
private fun StatusOptionPreview() = PreviewHelper {
    OrderStatus.entries.forEach { status ->
        StatusOption(
            status = status,
            selected = status == OrderStatus.IN_PROGRESS,
            onClick = {}
        )
    }
}
