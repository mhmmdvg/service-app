package com.cashierserviceapp.screens.addorder

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.add_order_step_customer_subtitle
import cashierserviceapp.shared.generated.resources.add_order_step_customer_title
import cashierserviceapp.shared.generated.resources.add_order_step_device_subtitle
import cashierserviceapp.shared.generated.resources.add_order_step_device_title
import com.cashierserviceapp.screens.addorder.components.StepFooter
import com.cashierserviceapp.screens.addorder.components.StepHeader
import com.cashierserviceapp.screens.addorder.steps.CustomerStep
import com.cashierserviceapp.screens.addorder.steps.DeviceStep
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.CircleIconButton
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.icons.ChevronLeftOutlined
import com.cashierserviceapp.ui.icons.XOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.domain.usecases.corevalidation.isValid
import com.cashierserviceapp.utils.Resource
import com.cashierserviceapp.screens.addorder.components.DeviceFormSheet
import com.cashierserviceapp.screens.addorder.components.PartPickerSheet
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

/**
 * Taking in a new repair, one question at a time: who's dropping it off, then what they're dropping
 * off. Presented as a full-screen cover rather than a tab, so it closes itself instead of relying
 * on the bottom navigation — which is hidden underneath it.
 */
@Composable
fun AddOrderScreen(
    onClose: () -> Unit,
    onSuccess: (String) -> Unit,
    viewModel: AddOrderViewModel = metroViewModel(),
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val step by viewModel.step.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()

    LaunchedEffect(submitState) {
        val state = submitState
        if (state is Resource.Success && state.data?.order?.id != null) {
            onSuccess(state.data.order.id)
        }
    }

    // The cover isn't a nav entry, so this ViewModel outlives it. Without clearing it here the
    // next intake would open onto the previous one's half-filled form.
    DisposableEffect(viewModel) {
        onDispose { viewModel.reset() }
    }

    val catalogue by viewModel.catalogue.collectAsStateWithLifecycle()
    val isSubmitting = submitState is Resource.Loading

    // The device being edited, and whether its parts sheet is on top of that. Both are just sheets
    // being open, so they live here rather than in the ViewModel.
    //
    // The draft itself lives here too, not in the form: a device only joins the order once it's
    // saved, so a half-filled one never shows up as a row behind the sheet.
    var editingDevice by remember { mutableStateOf<DeviceDraft?>(null) }
    var addingPart by remember { mutableStateOf(false) }

    AddOrderContent(
        formState = formState,
        step = step,
        isSubmitting = isSubmitting,
        errorMessage = (submitState as? Resource.Error)?.message,
        canContinue = viewModel.isStepComplete(formState, step) && !isSubmitting,
        onInputChanged = viewModel::onAddOrderEvent,
        onNext = viewModel::next,
        // The first step has nowhere to go back to, so back means leave.
        onBack = { if (!viewModel.back()) onClose() },
        onAddDevice = { editingDevice = viewModel.newDevice() },
        onEditDevice = { device -> editingDevice = device },
    )

    editingDevice?.let { device ->
        DeviceFormSheet(
            device = device,
            validate = viewModel::validateDevice,
            onDraftChange = { draft -> editingDevice = draft },
            onSave = { saved -> viewModel.onAddOrderEvent(AddOrderFormEvent.DeviceSaved(saved)) },
            onRemove = {
                viewModel.onAddOrderEvent(AddOrderFormEvent.DeviceRemoved(device.localId))
                editingDevice = null
            },
            onAddPart = { draft ->
                editingDevice = draft
                addingPart = true
            },
            // Nothing to undo: an unsaved device was never in the order, and an edit to an existing
            // one only ever touched this draft.
            onDismiss = { editingDevice = null }
        )
    }

    if (addingPart) {
        PartPickerSheet(
            catalogue = catalogue,
            newLocalId = viewModel::newPartLocalId,
            onAdd = { part ->
                editingDevice = editingDevice?.let { it.copy(parts = it.parts + part) }
                addingPart = false
            },
            onDismiss = { addingPart = false }
        )
    }
}

@Composable
private fun AddOrderContent(
    modifier: Modifier = Modifier,
    formState: AddOrderFormState,
    step: AddOrderStep,
    isSubmitting: Boolean,
    errorMessage: String?,
    canContinue: Boolean,
    onInputChanged: (AddOrderFormEvent) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onAddDevice: () -> Unit = {},
    onEditDevice: (DeviceDraft) -> Unit = {},
) {
    Column(
        modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
    ) {
        StepHeader(step = step, onBack = onBack, enabled = !isSubmitting)

        AnimatedContent(
            targetState = step,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                // Pages travel in the direction of travel: forward slides in from the right,
                // going back slides in from the left.
                val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                val enter = slideInHorizontally(tween(280)) { width -> direction * width / 4 } +
                        fadeIn(tween(220))
                val exit = slideOutHorizontally(tween(280)) { width -> -direction * width / 4 } +
                        fadeOut(tween(140))

                enter togetherWith exit using SizeTransform(clip = false)
            }
        ) { currentStep ->
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = currentStep.title(),
                    style = CashierServiceTheme.typography.h1,
                    color = CashierServiceTheme.colors.primaryText
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = currentStep.subtitle(),
                    style = CashierServiceTheme.typography.text1,
                    color = CashierServiceTheme.colors.secondaryText
                )

                Spacer(Modifier.height(28.dp))

                when (currentStep) {
                    AddOrderStep.CUSTOMER -> CustomerStep(
                        formState = formState,
                        onInputChanged = onInputChanged,
                        enabled = !isSubmitting
                    )

                    AddOrderStep.DEVICE -> DeviceStep(
                        devices = formState.devices,
                        enabled = !isSubmitting,
                        onAddDevice = onAddDevice,
                        onEditDevice = onEditDevice,
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        StepFooter(
            step = step,
            canContinue = canContinue,
            isSubmitting = isSubmitting,
            errorMessage = errorMessage,
            onNext = onNext,
            onBack = onBack
        )
    }
}

@Composable
private fun AddOrderStep.title(): String = stringResource(
    when (this) {
        AddOrderStep.CUSTOMER -> Res.string.add_order_step_customer_title
        AddOrderStep.DEVICE -> Res.string.add_order_step_device_title
    }
)

@Composable
private fun AddOrderStep.subtitle(): String = stringResource(
    when (this) {
        AddOrderStep.CUSTOMER -> Res.string.add_order_step_customer_subtitle
        AddOrderStep.DEVICE -> Res.string.add_order_step_device_subtitle
    }
)

@PreviewLightDark
@Composable
private fun AddOrderCustomerStepPreview() = PreviewHelper(paddingEnabled = false) {
    AddOrderContent(
        formState = AddOrderFormState(name = "Rina Wijaya", phone = "08123456789"),
        step = AddOrderStep.CUSTOMER,
        isSubmitting = false,
        errorMessage = null,
        canContinue = true,
        onInputChanged = {},
        onNext = {},
        onBack = {}
    )
}

@PreviewLightDark
@Composable
private fun AddOrderDeviceStepPreview() = PreviewHelper(paddingEnabled = false) {
    AddOrderContent(
        formState = AddOrderFormState(
            name = "Rina Wijaya",
            devices = listOf(
                DeviceDraft(
                    localId = "1",
                    brand = "Samsung",
                    model = "Galaxy A54",
                    complaint = "Screen won't turn on after a drop",
                    serviceFee = "50000",
                )
            )
        ),
        step = AddOrderStep.DEVICE,
        isSubmitting = false,
        errorMessage = "Nama customer wajib diisi untuk customer baru",
        canContinue = true,
        onInputChanged = {},
        onNext = {},
        onBack = {}
    )
}
