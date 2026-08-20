package com.cashierserviceapp.screens.addorder.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.action_back
import cashierserviceapp.shared.generated.resources.action_close
import cashierserviceapp.shared.generated.resources.add_order_step_progress
import com.cashierserviceapp.screens.addorder.AddOrderStep
import com.cashierserviceapp.ui.components.CircleIconButton
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.icons.ChevronLeftOutlined
import com.cashierserviceapp.ui.icons.XOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import org.jetbrains.compose.resources.stringResource

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
                contentDescription = stringResource(
                    if (step.isFirst) Res.string.action_close else Res.string.action_back
                )
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = stringResource(
                    Res.string.add_order_step_progress,
                    step.ordinal + 1,
                    AddOrderStep.entries.size
                ),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )
        }

        StepProgress(step)
    }
}
