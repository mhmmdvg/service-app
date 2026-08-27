package com.cashierserviceapp.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.action_close
import cashierserviceapp.shared.generated.resources.order_detail_qr_token
import cashierserviceapp.shared.generated.resources.scan_body
import cashierserviceapp.shared.generated.resources.scan_body_camera
import cashierserviceapp.shared.generated.resources.scan_camera
import cashierserviceapp.shared.generated.resources.scan_camera_failed
import cashierserviceapp.shared.generated.resources.scan_looking_up
import cashierserviceapp.shared.generated.resources.scan_lookup
import cashierserviceapp.shared.generated.resources.scan_not_found
import cashierserviceapp.shared.generated.resources.scan_title
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.domain.models.OrderItemTracking
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.scanning.rememberQrScanner
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.screens.order.components.OrderStatusChip
import com.cashierserviceapp.ui.components.SearchField
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
import org.jetbrains.compose.resources.stringResource

/**
 * Resolves a receipt's QR token to its order.
 *
 * Scanned or typed, the token takes the same path: [onLookup] with the decoded string. Where the
 * server names the order the token belongs to, the sheet steps out of the way and opens it through
 * [onOpenOrder] — a cashier holding the receipt wants the order, not a summary of it. Against a
 * server too old to send that id it stays put and shows the progress card instead.
 */
@Composable
fun ScanSheet(
    state: Resource<OrderTracking>?,
    onLookup: (String) -> Unit,
    onDismiss: () -> Unit,
    onOpenOrder: (String) -> Unit = {},
) {
    var token by remember { mutableStateOf("") }
    var scanError by remember { mutableStateOf<String?>(null) }

    // Null on iOS and desktop, where the field is the only way in.
    val scanner = rememberQrScanner()

    BottomSheet(onDismissRequest = onDismiss) { hide ->
        val orderId = state?.data?.id

        LaunchedEffect(orderId) {
            if (orderId != null) {
                hide()
                onOpenOrder(orderId)
            }
        }

        Spacer(Modifier.height(20.dp))

        Column(Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(Res.string.scan_title),
                style = CashierServiceTheme.typography.h3,
                color = CashierServiceTheme.colors.primaryText
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (scanner != null) stringResource(Res.string.scan_body_camera)
                else stringResource(Res.string.scan_body),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )

            Spacer(Modifier.height(16.dp))

            if (scanner != null) {
                Button(
                    label = stringResource(Res.string.scan_camera),
                    onClick = {
                        scanError = null
                        scanner.scan(
                            onResult = { scanned ->
                                token = scanned
                                onLookup(scanned)
                            },
                            onError = { scanError = it }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    primary = true,
                    enabled = state !is Resource.Loading
                )

                Spacer(Modifier.height(12.dp))
            }

            SearchField(
                value = token,
                onValueChange = { token = it },
                placeholder = stringResource(Res.string.order_detail_qr_token),
                enabled = state !is Resource.Loading,
                keyboardActions = KeyboardActions(onSearch = { onLookup(token) })
            )

            Spacer(Modifier.height(12.dp))

            val tracking = state?.data
            when {
                scanError != null -> ScanMessage(
                    text = scanError ?: stringResource(Res.string.scan_camera_failed),
                    isError = true
                )

                state is Resource.Loading -> ScanMessage(stringResource(Res.string.scan_looking_up))

                state is Resource.Error -> ScanMessage(
                    text = state.message ?: stringResource(Res.string.scan_not_found),
                    isError = true
                )

                tracking != null -> TrackingResult(tracking)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    label = stringResource(Res.string.action_close),
                    onClick = hide,
                    modifier = Modifier.weight(1f),
                    primary = false
                )
                Button(
                    label = stringResource(Res.string.scan_lookup),
                    onClick = { onLookup(token) },
                    modifier = Modifier.weight(1f),
                    primary = true,
                    enabled = token.isNotBlank() && state !is Resource.Loading
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ScanMessage(text: String, isError: Boolean = false) {
    Text(
        text = text,
        style = CashierServiceTheme.typography.text2,
        color = if (isError) CashierServiceTheme.colors.dangerText
        else CashierServiceTheme.colors.secondaryText
    )
}

@Composable
private fun TrackingResult(tracking: OrderTracking) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = tracking.orderCode,
            style = CashierServiceTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
        )

        tracking.items.forEach { item ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "${item.deviceBrand} ${item.deviceModel}",
                        style = CashierServiceTheme.typography.text2,
                        maxLines = 1
                    )
                    item.complaint?.takeIf { it.isNotBlank() }?.let { complaint ->
                        Text(
                            text = complaint,
                            style = CashierServiceTheme.typography.text2,
                            color = CashierServiceTheme.colors.secondaryText,
                            maxLines = 1
                        )
                    }
                }

                OrderStatusChip(item.status)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ScanSheetPreview() = PreviewHelper {
    TrackingResult(
        OrderTracking(
            orderCode = "SV-1786641253",
            items = listOf(
                OrderItemTracking(
                    deviceBrand = "Samsung",
                    deviceModel = "Galaxy A54",
                    status = OrderStatus.IN_PROGRESS,
                    complaint = "Layar mati setelah jatuh"
                ),
                OrderItemTracking(
                    deviceBrand = "Apple",
                    deviceModel = "iPhone 13",
                    status = OrderStatus.COMPLETED,
                    complaint = "Battery"
                ),
            )
        )
    )
}
