package com.cashierserviceapp.screens.addorder.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.screens.addorder.AddOrderStep
import com.cashierserviceapp.ui.components.CircleIconButton
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.icons.ChevronLeftOutlined
import com.cashierserviceapp.ui.icons.XOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme

@Suppress("DEPRECATION")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun StepHeader(
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
