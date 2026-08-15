package com.cashierserviceapp.screens.addorder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.action_cancel
import cashierserviceapp.shared.generated.resources.add_order_device_brand
import cashierserviceapp.shared.generated.resources.add_order_device_color
import cashierserviceapp.shared.generated.resources.add_order_device_complaint
import cashierserviceapp.shared.generated.resources.add_order_device_model
import cashierserviceapp.shared.generated.resources.add_order_device_remove
import cashierserviceapp.shared.generated.resources.add_order_device_save
import cashierserviceapp.shared.generated.resources.add_order_device_title
import cashierserviceapp.shared.generated.resources.add_order_part_add_another
import cashierserviceapp.shared.generated.resources.add_order_parts_title
import cashierserviceapp.shared.generated.resources.add_order_service_fee
import com.cashierserviceapp.screens.addorder.DeviceDraft
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.components.TextField
import com.cashierserviceapp.ui.icons.XOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import org.jetbrains.compose.resources.stringResource

/**
 * The whole of one device: what it is, what's wrong with it, and what it's expected to cost.
 *
 * Edits a copy and only reports back on save, so backing out of the sheet leaves the order as it
 * was — important when the same sheet is used to add a device and to correct one.
 */
@Composable
fun DeviceFormSheet(
    device: DeviceDraft,
    onSave: (DeviceDraft) -> Unit,
    onRemove: () -> Unit,
    onAddPart: (DeviceDraft) -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by remember(device.localId) { mutableStateOf(device) }

    // Parts are added through a sheet of their own, so they arrive on the incoming [device] rather
    // than through this local copy — take them whenever they change.
    if (draft.parts != device.parts) draft = draft.copy(parts = device.parts)

    BottomSheet(onDismissRequest = onDismiss) { hide ->
        Spacer(Modifier.height(20.dp))

        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .heightIn(max = 520.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(Res.string.add_order_device_title),
                style = CashierServiceTheme.typography.h3,
                color = CashierServiceTheme.colors.primaryText
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = draft.brand,
                    onValueChange = { draft = draft.copy(brand = it) },
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.add_order_device_brand),
                    singleLine = true,
                )
                TextField(
                    value = draft.model,
                    onValueChange = { draft = draft.copy(model = it) },
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.add_order_device_model),
                    singleLine = true,
                )
            }

            Spacer(Modifier.height(12.dp))

            TextField(
                value = draft.color,
                onValueChange = { draft = draft.copy(color = it) },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.add_order_device_color),
                singleLine = true,
            )

            Spacer(Modifier.height(12.dp))

            TextField(
                value = draft.complaint,
                onValueChange = { draft = draft.copy(complaint = it) },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.add_order_device_complaint),
            )

            Spacer(Modifier.height(12.dp))

            TextField(
                value = draft.serviceFee,
                onValueChange = { value ->
                    draft = draft.copy(serviceFee = value.filter { it.isDigit() })
                },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.add_order_service_fee),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.add_order_parts_title),
                style = CashierServiceTheme.typography.text2.copy(fontWeight = FontWeight.SemiBold),
                color = CashierServiceTheme.colors.secondaryText
            )

            Spacer(Modifier.height(8.dp))

            draft.parts.forEach { part ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(CashierServiceTheme.shapes.roundedCornerLg)
                        .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "${part.name} ×${part.qty}",
                            style = CashierServiceTheme.typography.text2,
                            maxLines = 1
                        )
                        Text(
                            text = part.subtotalLabel,
                            style = CashierServiceTheme.typography.text2,
                            color = CashierServiceTheme.colors.secondaryText
                        )
                    }

                    Icon(
                        imageVector = XOutlined,
                        contentDescription = stringResource(Res.string.add_order_device_remove),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null,
                                role = Role.Button
                            ) {
                                draft = draft.copy(
                                    parts = draft.parts.filterNot { it.localId == part.localId }
                                )
                                onSave(draft)
                            },
                        tint = CashierServiceTheme.colors.secondaryText
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                label = stringResource(Res.string.add_order_part_add_another),
                onClick = {
                    // Persist what's typed before the parts sheet takes over, or it'd be lost.
                    onSave(draft)
                    onAddPart(draft)
                },
                modifier = Modifier.fillMaxWidth(),
                primary = false,
            )

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    label = stringResource(
                        if (device.isValid) Res.string.add_order_device_remove
                        else Res.string.action_cancel
                    ),
                    onClick = {
                        hide()
                        onRemove()
                    },
                    modifier = Modifier.weight(1f),
                    primary = false,
                )
                Button(
                    label = stringResource(Res.string.add_order_device_save),
                    onClick = {
                        onSave(draft)
                        hide()
                    },
                    modifier = Modifier.weight(1f),
                    primary = true,
                    enabled = draft.isValid,
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}
