package com.cashierserviceapp.screens.ordersuccess

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.*
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.screens.orderdetail.components.DetailRow
import com.cashierserviceapp.screens.orderdetail.components.DetailSectionHeader
import com.cashierserviceapp.screens.orderdetail.components.DeviceCard
import com.cashierserviceapp.screens.orderdetail.components.LoadingSkeleton
import com.cashierserviceapp.ui.components.Avatar
import com.cashierserviceapp.ui.components.ContentMessage
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.icons.PrinterOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrderSuccessScreen(
    orderId: String,
    onClose: () -> Unit,
) {
    val viewModel: OrderSuccessViewModel =
        assistedMetroViewModel<OrderSuccessViewModel, OrderSuccessViewModel.Factory> {
            create(orderId)
        }

    val detailState by viewModel.detailState.collectAsStateWithLifecycle()

    ScreenWithTitle(
        title = stringResource(Res.string.order_success_title),
        onBack = onClose,
        actions = {
            if (detailState.data != null) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = PrinterOutlined,
                        contentDescription = null,
                        tint = CashierServiceTheme.colors.primaryText
                    )
                }
            }
        }
    ) {
        val orderDetail = detailState.data

        Spacer(Modifier.height(8.dp))

        when {
            detailState is Resource.Loading && orderDetail == null -> LoadingSkeleton()
            detailState is Resource.Error && orderDetail == null -> {
                ContentMessage(
                    title = stringResource(Res.string.order_detail_load_failed),
                    body = detailState.message ?: stringResource(Res.string.error_generic),
                    actionLabel = stringResource(Res.string.action_try_again),
                    onAction = viewModel::retry
                )
            }

            detailState is Resource.Success && orderDetail != null -> {
                DetailSectionHeader(stringResource(Res.string.order_detail_customer))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(CashierServiceTheme.shapes.roundedCornerLg)
                        .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Avatar(
                        name = orderDetail.customerName,
                        size = 42.dp,
                        initialSize = 20.sp
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = orderDetail.customerName,
                            style = CashierServiceTheme.typography.text2,
                            color = CashierServiceTheme.colors.secondaryText,
                            maxLines = 1,
                        )
                    }
                }


                Spacer(Modifier.height(28.dp))

                DetailSectionHeader(stringResource(Res.string.order_detail_section_details))

                DetailRow(
                    label = stringResource(Res.string.order_detail_order_code),
                    value = orderDetail.orderCode
                )

                Spacer(Modifier.height(28.dp))

                orderDetail.createdLabel?.let {
                    DetailRow(label = stringResource(Res.string.order_detail_created), value = it)
                }
                orderDetail.cashierName?.let {
                    DetailRow(label = stringResource(Res.string.order_detail_cashier), value = it)
                }
                DetailRow(
                    label = stringResource(Res.string.order_detail_qr_token),
                    value = orderDetail.qrToken
                )

                Spacer(Modifier.height(28.dp))

                DetailSectionHeader("${stringResource(Res.string.order_detail_devices)} (${orderDetail.items.size})")
                if (orderDetail.items.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.order_detail_no_devices),
                        style = CashierServiceTheme.typography.text2,
                        color = CashierServiceTheme.colors.secondaryText
                    )
                } else {
                    orderDetail.items.forEachIndexed { index, it ->
                        if (index > 0) Spacer(Modifier.height(8.dp))
                        DeviceCard(
                            item = it,
                            isUpdating = false
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewOrderSuccessScreen() = PreviewHelper {
    OrderSuccessScreen(orderId = "SRV-001", onClose = {})
}