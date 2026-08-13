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
import com.cashierserviceapp.ui.components.TextField

/**
 * Step 1 — who's dropping the device off. Only the name is required; the rest is contact detail the
 * shop can chase later, so an empty field is sent as null rather than an empty string.
 */
@Composable
internal fun CustomerStep(
    form: AddOrderForm,
    onFormChange: ((AddOrderForm) -> AddOrderForm) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val phoneFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val addressFocus = remember { FocusRequester() }

    Column(modifier) {
        TextField(
            value = form.name,
            onValueChange = { value -> onFormChange { it.copy(name = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = "Full name",
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { phoneFocus.requestFocus() })
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = form.phone,
            onValueChange = { value -> onFormChange { it.copy(phone = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = "Phone number",
            focusRequester = phoneFocus,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { emailFocus.requestFocus() })
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = form.email,
            onValueChange = { value -> onFormChange { it.copy(email = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = "Email",
            focusRequester = emailFocus,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { addressFocus.requestFocus() })
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = form.address,
            onValueChange = { value -> onFormChange { it.copy(address = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = "Address",
            focusRequester = addressFocus,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
    }
}
