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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metrox.viewmodel.metroViewModel

/**
 * Taking in a new repair, one question at a time: who's dropping it off, then what they're dropping
 * off. Presented as a full-screen cover rather than a tab, so it closes itself instead of relying
 * on the bottom navigation — which is hidden underneath it.
 */
@Composable
fun AddOrderScreen(
    onClose: () -> Unit,
    viewModel: AddOrderViewModel = metroViewModel(),
) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val step by viewModel.step.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()

    LaunchedEffect(submitState) {
        if (submitState is Resource.Success) onClose()
    }

    // The cover isn't a nav entry, so this ViewModel outlives it. Without clearing it here the
    // next intake would open onto the previous one's half-filled form.
    DisposableEffect(viewModel) {
        onDispose { viewModel.reset() }
    }

    val isSubmitting = submitState is Resource.Loading

    AddOrderContent(
        form = form,
        step = step,
        isSubmitting = isSubmitting,
        errorMessage = (submitState as? Resource.Error)?.message,
        canContinue = form.isValid(step) && !isSubmitting,
        onFormChange = viewModel::update,
        onNext = viewModel::next,
        // The first step has nowhere to go back to, so back means leave.
        onBack = { if (!viewModel.back()) onClose() },
    )
}

@Composable
private fun AddOrderContent(
    form: AddOrderForm,
    step: AddOrderStep,
    isSubmitting: Boolean,
    errorMessage: String?,
    canContinue: Boolean,
    onFormChange: ((AddOrderForm) -> AddOrderForm) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
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
                        form = form,
                        onFormChange = onFormChange,
                        enabled = !isSubmitting
                    )

                    AddOrderStep.DEVICE -> DeviceStep(
                        form = form,
                        onFormChange = onFormChange,
                        enabled = !isSubmitting,
                        onSubmit = { if (canContinue) onNext() }
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

// Same story as FullScreenCover: the suggested androidx.navigationevent replacement isn't on our
// classpath yet.
@Suppress("DEPRECATION")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun StepHeader(
    step: AddOrderStep,
    onBack: () -> Unit,
    enabled: Boolean,
) {
    // Registered deeper than the cover's own handler, so it wins: back walks the steps first and
    // only closes the whole flow once there's nothing left to walk.
    BackHandler(enabled = enabled && !step.isFirst, onBack = onBack)

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(
                modifier = Modifier.size(44.dp),
                onClick = onBack,
                enabled = enabled,
                icon = if (step.isFirst) XOutlined else ChevronLeftOutlined,
                contentDescription = if (step.isFirst) "Close" else "Back"
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "Step ${step.ordinal + 1} of ${AddOrderStep.entries.size}",
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )
        }

        StepProgress(step)
    }
}

@Composable
private fun StepProgress(step: AddOrderStep) {
    // Kept as State and read only inside drawBehind, so filling the bar animates in the draw phase
    // without recomposing anything.
    val progress = animateFloatAsState(
        targetValue = (step.ordinal + 1f) / AddOrderStep.entries.size,
        animationSpec = tween(320),
        label = "stepProgress"
    )

    val trackColor = CashierServiceTheme.colors.strokePale
    val fillColor = CashierServiceTheme.colors.primaryBackground

    Spacer(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(3.dp)
            .drawBehind {
                val radius = CornerRadius(size.height / 2f)
                drawRoundRect(color = trackColor, cornerRadius = radius)
                drawRoundRect(
                    color = fillColor,
                    size = Size(size.width * progress.value, size.height),
                    cornerRadius = radius
                )
            }
    )
}

@Composable
private fun StepFooter(
    step: AddOrderStep,
    canContinue: Boolean,
    isSubmitting: Boolean,
    errorMessage: String?,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    Column {
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 12.dp),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.accentText
            )
        }

        HorizontalDivider(thickness = 1.dp, color = CashierServiceTheme.colors.strokePale)

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!step.isFirst) {
                Text(
                    text = "Back",
                    modifier = Modifier.clickable(enabled = !isSubmitting, onClick = onBack),
                    style = CashierServiceTheme.typography.h4,
                    color = CashierServiceTheme.colors.primaryText
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                label = when {
                    isSubmitting -> "Creating order…"
                    step.isLast -> "Create order"
                    else -> "Next"
                },
                onClick = onNext,
                primary = true,
                enabled = canContinue
            )
        }
    }
}

private fun AddOrderStep.title(): String = when (this) {
    AddOrderStep.CUSTOMER -> "Who's the customer?"
    AddOrderStep.DEVICE -> "What are we fixing?"
}

private fun AddOrderStep.subtitle(): String = when (this) {
    AddOrderStep.CUSTOMER -> "We'll use this to reach them when the repair is done. Only the " +
            "name is required."
    AddOrderStep.DEVICE -> "Describe the device and what's wrong with it."
}

@PreviewLightDark
@Composable
private fun AddOrderCustomerStepPreview() = PreviewHelper(paddingEnabled = false) {
    AddOrderContent(
        form = AddOrderForm(name = "Rina Wijaya", phone = "08123456789"),
        step = AddOrderStep.CUSTOMER,
        isSubmitting = false,
        errorMessage = null,
        canContinue = true,
        onFormChange = {},
        onNext = {},
        onBack = {}
    )
}

@PreviewLightDark
@Composable
private fun AddOrderDeviceStepPreview() = PreviewHelper(paddingEnabled = false) {
    AddOrderContent(
        form = AddOrderForm(
            name = "Rina Wijaya",
            brand = "Samsung",
            model = "Galaxy A54",
            complaint = "Screen won't turn on after a drop"
        ),
        step = AddOrderStep.DEVICE,
        isSubmitting = false,
        errorMessage = "Nama customer wajib diisi untuk customer baru",
        canContinue = true,
        onFormChange = {},
        onNext = {},
        onBack = {}
    )
}
