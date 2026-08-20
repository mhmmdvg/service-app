package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.order_detail_status_sheet_body
import cashierserviceapp.shared.generated.resources.order_detail_status_sheet_fee
import cashierserviceapp.shared.generated.resources.order_detail_status_sheet_fee_locked
import cashierserviceapp.shared.generated.resources.order_detail_status_sheet_fee_zero
import cashierserviceapp.shared.generated.resources.order_detail_status_sheet_save
import cashierserviceapp.shared.generated.resources.order_detail_status_sheet_title
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.domain.models.acceptsPrice
import com.cashierserviceapp.screens.order.components.color
import com.cashierserviceapp.screens.order.components.label
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.components.TextField
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import org.jetbrains.compose.resources.stringResource

/**
 * Moves one device along the repair, and prices it on the way through.
 *
 * Every status is selectable, in either direction — a device that was marked done by mistake has to
 * be able to go back, and the server records each move in the item's history either way.
 *
 * The fee only appears once the chosen status [acceptsPrice]: quoting a job that hasn't been
 * diagnosed is guesswork, and going through this sheet is what stops a device reaching Completed
 * at Rp 0 without anyone noticing. Because both the status and the fee are on one sheet, the choice
 * is applied on save rather than on tap — otherwise picking In Progress would commit the move
 * before the price next to it had been typed.
 */
@Composable
fun StatusPickerSheet(
    deviceName: String,
    current: OrderStatus,
    currentServiceFee: Long?,
    hasParts: Boolean,
    onSubmit: (status: OrderStatus, serviceFee: Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    var selected by remember { mutableStateOf(current) }
    var fee by remember { mutableStateOf(currentServiceFee?.toString().orEmpty()) }

    val feeValue = fee.toLongOrNull()

    // A status that can't hold a price sends none, so whatever is stored is left alone. Clearing
    // the field does the same: the patch omits a null fee, so it can be corrected but not unset.
    val submittedFee = feeValue.takeIf { selected.acceptsPrice }
    val feeChanged = selected.acceptsPrice && feeValue != null && feeValue != currentServiceFee
    val canSave = selected != current || feeChanged

    // Nothing to bill and nothing to bill it against: this device is about to be finished at zero.
    val completingUnpriced = selected == OrderStatus.COMPLETED && submittedFee == null && !hasParts

    BottomSheet(onDismissRequest = onDismiss) { hide ->
        Spacer(Modifier.height(20.dp))

        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .heightIn(max = 520.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
                    selected = status == selected,
                    onClick = { selected = status }
                )
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(visible = selected.acceptsPrice) {
                Column {
                    TextField(
                        value = fee,
                        onValueChange = { value -> fee = value.filter { it.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(Res.string.order_detail_status_sheet_fee),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }

            AnimatedVisibility(visible = !selected.acceptsPrice || completingUnpriced) {
                Column {
                    Text(
                        text = stringResource(
                            if (completingUnpriced) Res.string.order_detail_status_sheet_fee_zero
                            else Res.string.order_detail_status_sheet_fee_locked
                        ),
                        style = CashierServiceTheme.typography.text2,
                        color = CashierServiceTheme.colors.noteText
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                label = stringResource(Res.string.order_detail_status_sheet_save),
                onClick = {
                    // Hide first: the sheet is dismissed on its own animation, and the reload this
                    // kicks off would otherwise redraw the list underneath mid-slide.
                    hide()
                    onSubmit(selected, submittedFee)
                },
                modifier = Modifier.fillMaxWidth(),
                primary = true,
                enabled = canSave,
            )
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
