package com.cashierserviceapp.screens.orderdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.*
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.screens.order.components.OrderStatusChip
import com.cashierserviceapp.screens.orderdetail.components.DetailRow
import com.cashierserviceapp.screens.orderdetail.components.DetailSectionHeader
import com.cashierserviceapp.screens.orderdetail.components.DeviceCard
import com.cashierserviceapp.screens.orderdetail.components.DevicePickerSheet
import com.cashierserviceapp.screens.orderdetail.components.ReceiptSheet
import com.cashierserviceapp.screens.orderdetail.components.StatusPickerSheet
import com.cashierserviceapp.ui.icons.PrinterOutlined
import com.cashierserviceapp.screens.orderdetail.components.LoadingSkeleton
import com.cashierserviceapp.screens.orderdetail.components.UpdateErrorBanner
import com.cashierserviceapp.screens.orderdetail.components.UpdateStatusFooter
import com.cashierserviceapp.ui.components.Avatar
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.ContentMessage
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource


/** Height the scroll content leaves free at the bottom for [UpdateStatusFooter]. */
private val FOOTER_CLEARANCE = 96.dp

@Composable
fun OrderDetailScreen(
    orderId: String,
    onBack: () -> Unit,
    viewModel: OrderDetailViewModel = metroViewModel(),
) {
    // The id arrives as a nav argument rather than through the ViewModel's constructor, so the
    // screen can use the plain factory. The ViewModel ignores repeats of the same id.
    LaunchedEffect(orderId) { viewModel.load(orderId) }

    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    val updatingItemId by viewModel.updatingItemId.collectAsStateWithLifecycle()
    val updateError by viewModel.updateError.collectAsStateWithLifecycle()

    // Which device's status is being changed, if any. Held here rather than in the ViewModel: it's
    // a sheet being open, not anything the app needs to remember.
    var editing by remember { mutableStateOf<OrderDetailItemUiModel?>(null) }
    var choosingDevice by remember { mutableStateOf(false) }
    var printing by remember { mutableStateOf(false) }

    val items = detailState.data?.items.orEmpty()

    OrderDetailContent(
        state = detailState,
        updatingItemId = updatingItemId,
        updateError = updateError,
        onRetry = viewModel::retry,
        onBack = onBack,
        // One device is the common case, so don't make anyone confirm which one it is.
        onUpdateStatus = {
            if (items.size == 1) editing = items.first() else choosingDevice = true
        },
        onPrint = { printing = true },
        onDismissUpdateError = viewModel::clearUpdateError,
    )

    if (choosingDevice) {
        DevicePickerSheet(
            items = items,
            onSelect = { item -> editing = item },
            onDismiss = { choosingDevice = false }
        )
    }

    editing?.let { item ->
        StatusPickerSheet(
            deviceName = item.deviceName,
            current = item.status,
            onSelect = { status -> viewModel.setItemStatus(item.id, status) },
            onDismiss = { editing = null }
        )
    }

    if (printing) {
        detailState.data?.let { detail ->
            ReceiptSheet(detail = detail, onDismiss = { printing = false })
        }
    }
}

@Composable
internal fun OrderDetailContent(
    state: Resource<OrderDetailUiModel>,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    updatingItemId: String? = null,
    updateError: String? = null,
    onUpdateStatus: () -> Unit = {},
    onPrint: () -> Unit = {},
    onDismissUpdateError: () -> Unit = {},
) {
    val detail = state.data

    Box(Modifier.fillMaxSize()) {
    ScreenWithTitle(
        title = detail?.orderCode ?: stringResource(Res.string.order_detail_title),
        onBack = onBack,
        actions = {
            if (detail != null) {
                IconButton(onClick = onPrint) {
                    Icon(
                        imageVector = PrinterOutlined,
                        contentDescription = stringResource(Res.string.order_detail_print),
                        tint = CashierServiceTheme.colors.primaryText
                    )
                }
            }
        },
    ) {
        Spacer(Modifier.height(8.dp))

        if (updateError != null) {
            UpdateErrorBanner(message = updateError, onDismiss = onDismissUpdateError)
            Spacer(Modifier.height(12.dp))
        }

        when {
            detail != null -> {
                // The headline figure, MoonPay-style: what this order came to, with its state underneath.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(CashierServiceTheme.shapes.roundedCornerLg)
                        .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
                        .padding(vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (detail.isUnpriced) "—" else detail.totalLabel,
                        style = CashierServiceTheme.typography.h1.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = if (detail.isUnpriced) CashierServiceTheme.colors.noteText
                        else CashierServiceTheme.colors.primaryText,
                        maxLines = 1
                    )

                    Spacer(Modifier.height(10.dp))

                    OrderStatusChip(detail.status)

                    if (detail.isUnpriced) {
                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.order_detail_not_priced),
                            style = CashierServiceTheme.typography.text2,
                            color = CashierServiceTheme.colors.noteText
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

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
                    Avatar(name = detail.customerName, size = 42.dp, initialSize = 20.sp)

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = detail.customerName,
                            style = CashierServiceTheme.typography.h4,
                            maxLines = 1
                        )
                        detail.customerPhone?.let { phone ->
                            Text(
                                text = phone,
                                style = CashierServiceTheme.typography.text2,
                                color = CashierServiceTheme.colors.secondaryText,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                DetailSectionHeader(stringResource(Res.string.order_detail_section_details))

                DetailRow(
                    label = stringResource(Res.string.order_detail_order_code),
                    value = detail.orderCode
                )
                detail.createdLabel?.let {
                    DetailRow(label = stringResource(Res.string.order_detail_created), value = it)
                }
                detail.cashierName?.let {
                    DetailRow(label = stringResource(Res.string.order_detail_cashier), value = it)
                }
                DetailRow(
                    label = stringResource(Res.string.order_detail_qr_token),
                    value = detail.qrToken
                )

                Spacer(Modifier.height(28.dp))

                DetailSectionHeader("${stringResource(Res.string.order_detail_devices)} (${detail.deviceCount})")

                if (detail.items.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.order_detail_no_devices),
                        style = CashierServiceTheme.typography.text2,
                        color = CashierServiceTheme.colors.secondaryText
                    )
                } else {
                    detail.items.forEachIndexed { index, item ->
                        if (index > 0) Spacer(Modifier.height(8.dp))
                        DeviceCard(
                            item = item,
                            isUpdating = item.id == updatingItemId
                        )
                    }
                }
            }

            state is Resource.Error -> ContentMessage(
                title = stringResource(Res.string.order_detail_load_failed),
                body = state.message ?: stringResource(Res.string.error_generic),
                actionLabel = stringResource(Res.string.action_try_again),
                onAction = onRetry
            )

            else -> LoadingSkeleton()
        }

        // Clears the footer so the last card isn't trapped underneath it.
        Spacer(Modifier.height(if (detail != null) FOOTER_CLEARANCE else 32.dp))
    }

        if (detail != null && detail.status != OrderStatus.COMPLETED) {
            UpdateStatusFooter(
                modifier = Modifier.align(Alignment.BottomCenter),
                enabled = updatingItemId == null,
                onClick = onUpdateStatus
            )
        }
    }
}




private val previewDetail = OrderDetailUiModel(
    orderCode = "SV-1786641253",
    qrToken = "322F9819-579B-4DB7-8328-6530C5F386BF",
    customerName = "Rina Wijaya",
    customerPhone = "08123456789",
    cashierName = "Administrator",
    createdLabel = "14 Aug 2026, 00:14",
    totalLabel = "Rp 350.000",
    isUnpriced = false,
    items = listOf(
        OrderDetailItemUiModel(
            id = "1",
            deviceName = "Samsung Galaxy A54",
            complaint = "Layar mati setelah jatuh",
            status = OrderStatus.IN_PROGRESS,
            serviceFeeLabel = "Rp 50.000",
            totalLabel = "Rp 350.000",
            parts = listOf(OrderPartUiModel("p1", "LCD Galaxy A54", 1, "Rp 300.000"))
        ),
        OrderDetailItemUiModel(
            id = "2",
            deviceName = "Apple iPhone 13",
            complaint = "Battery drains fast",
            status = OrderStatus.RECEIVED,
            serviceFeeLabel = null,
            totalLabel = null,
            parts = emptyList()
        ),
    )
)

@PreviewLightDark
@Composable
private fun OrderDetailPreview() = PreviewHelper(paddingEnabled = false) {
    OrderDetailContent(
        state = Resource.Success(previewDetail),
        onRetry = {},
        onBack = {}
    )
}

@PreviewLightDark
@Composable
private fun OrderDetailUnpricedPreview() = PreviewHelper(paddingEnabled = false) {
    OrderDetailContent(
        state = Resource.Success(
            previewDetail.copy(
                isUnpriced = true,
                items = previewDetail.items.map {
                    it.copy(serviceFeeLabel = null, totalLabel = null, parts = emptyList())
                }
            )
        ),
        onRetry = {},
        onBack = {}
    )
}

@PreviewLightDark
@Composable
private fun OrderDetailLoadingPreview() = PreviewHelper(paddingEnabled = false) {
    OrderDetailContent(state = Resource.Loading(), onRetry = {}, onBack = {})
}
