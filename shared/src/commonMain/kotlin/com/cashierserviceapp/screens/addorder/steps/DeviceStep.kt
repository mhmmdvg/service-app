package com.cashierserviceapp.screens.addorder.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.add_order_device_add
import cashierserviceapp.shared.generated.resources.add_order_devices_empty
import cashierserviceapp.shared.generated.resources.add_order_estimated_total
import com.cashierserviceapp.screens.addorder.DeviceDraft
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.formatRupiah
import org.jetbrains.compose.resources.stringResource

/**
 * Step 2 — every device coming in on this order.
 *
 * A list rather than a form: an order can take in several devices, and each carries its own
 * complaint, fee and parts. Tapping one opens the editor; the estimated total only appears once
 * something has actually been priced, so an unpriced intake doesn't claim to be worth Rp 0.
 */
@Composable
internal fun DeviceStep(
    devices: List<DeviceDraft>,
    enabled: Boolean,
    onAddDevice: () -> Unit,
    onEditDevice: (DeviceDraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        if (devices.isEmpty()) {
            Text(
                text = stringResource(Res.string.add_order_devices_empty),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )
            Spacer(Modifier.height(16.dp))
        }

        devices.let {
            it.forEach { device ->
                DeviceRow(
                    device = device,
                    enabled = enabled,
                    onClick = { onEditDevice(device) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        Button(
            label = stringResource(Res.string.add_order_device_add),
            onClick = onAddDevice,
            modifier = Modifier.fillMaxWidth(),
            primary = false,
            enabled = enabled,
        )

        val total = devices.sumOf { it.total }
        if (devices.any { it.hasPrice }) {
            Spacer(Modifier.height(20.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.add_order_estimated_total),
                    style = CashierServiceTheme.typography.text2,
                    color = CashierServiceTheme.colors.secondaryText
                )
                Text(
                    text = formatRupiah(total),
                    style = CashierServiceTheme.typography.h4
                )
            }
        }
    }
}

@Composable
private fun DeviceRow(
    device: DeviceDraft,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Always a real name: a device only reaches this list once ValidateDevice has passed
            // it, and that needs a brand and a model.
            Text(
                text = device.name,
                modifier = Modifier.weight(1f),
                style = CashierServiceTheme.typography.h4,
                color = CashierServiceTheme.colors.primaryText,
                maxLines = 1
            )

            if (device.hasPrice) {
                Text(
                    text = formatRupiah(device.total),
                    style = CashierServiceTheme.typography.text2.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )
            }
        }

        if (device.complaint.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = device.complaint,
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText,
                maxLines = 1
            )
        }

        if (device.parts.isNotEmpty()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = device.parts.joinToString { "${it.name} ×${it.qty}" },
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.noteText,
                maxLines = 1
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun DeviceStepPreview() = PreviewHelper {
    DeviceStep(
        devices = listOf(
            DeviceDraft(
                localId = "1",
                brand = "Samsung",
                model = "Galaxy A54",
                complaint = "Layar mati setelah jatuh",
                serviceFee = "50000",
            ),
            DeviceDraft(localId = "2", brand = "Apple", model = "iPhone 13", complaint = "Battery"),
        ),
        enabled = true,
        onAddDevice = {},
        onEditDevice = {},
    )
}
