package com.cashierserviceapp.screens.addorder.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.screens.addorder.AddOrderForm
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.components.TextField
import com.cashierserviceapp.ui.theme.CashierServiceTheme

/**
 * Step 2 — the device itself, plus what's wrong with it.
 *
 * Service fee sits here rather than on a step of its own because it's often already known at the
 * counter ("screen swap, flat rate"). When it isn't, the field stays empty and the order item is
 * created without a price, to be filled in after the diagnosis.
 */
@Composable
internal fun DeviceStep(
    form: AddOrderForm,
    onFormChange: ((AddOrderForm) -> AddOrderForm) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onSubmit: () -> Unit = {},
) {
    val modelFocus = remember { FocusRequester() }
    val colorFocus = remember { FocusRequester() }
    val complaintFocus = remember { FocusRequester() }

    Column(modifier) {
        TextField(
            value = form.brand,
            onValueChange = { value -> onFormChange { it.copy(brand = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = "Brand",
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { modelFocus.requestFocus() })
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = form.model,
            onValueChange = { value -> onFormChange { it.copy(model = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = "Model",
            focusRequester = modelFocus,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { colorFocus.requestFocus() })
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = form.color,
            onValueChange = { value -> onFormChange { it.copy(color = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = "Colour",
            focusRequester = colorFocus,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { complaintFocus.requestFocus() })
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "What's the problem?",
            style = CashierServiceTheme.typography.h4,
            color = CashierServiceTheme.colors.primaryText
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = form.complaint,
            onValueChange = { value -> onFormChange { it.copy(complaint = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = "Complaint",
            focusRequester = complaintFocus,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Service fee",
            style = CashierServiceTheme.typography.h4,
            color = CashierServiceTheme.colors.primaryText
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Only if the price is already settled. Leave it empty to quote after the " +
                    "diagnosis — spare parts get added then too.",
            style = CashierServiceTheme.typography.text2,
            color = CashierServiceTheme.colors.secondaryText
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            // Filtered rather than validated: a fee is a whole number of rupiah, so anything else
            // simply never lands in the field.
            value = form.serviceFee,
            onValueChange = { value ->
                val digits = value.filter { it.isDigit() }
                onFormChange { it.copy(serviceFee = digits) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Service fee (optional)",
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onSubmit() })
        )
    }
}
