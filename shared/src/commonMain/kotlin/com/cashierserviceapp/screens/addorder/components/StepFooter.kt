package com.cashierserviceapp.screens.addorder.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.action_back
import cashierserviceapp.shared.generated.resources.action_next
import cashierserviceapp.shared.generated.resources.add_order_create
import cashierserviceapp.shared.generated.resources.add_order_creating
import com.cashierserviceapp.screens.addorder.AddOrderStep
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun StepFooter(
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
                color = CashierServiceTheme.colors.dangerText
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
                    text = stringResource(Res.string.action_back),
                    modifier = Modifier.clickable(enabled = !isSubmitting, onClick = onBack),
                    style = CashierServiceTheme.typography.h4,
                    color = CashierServiceTheme.colors.primaryText
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                label = stringResource(
                    when {
                        isSubmitting -> Res.string.add_order_creating
                        step.isLast -> Res.string.add_order_create
                        else -> Res.string.action_next
                    }
                ),
                onClick = onNext,
                primary = true,
                enabled = canContinue
            )
        }
    }
}