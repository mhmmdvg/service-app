package com.cashierserviceapp.screens.addorder.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.domain.usecases.corevalidation.ValidationError
import com.cashierserviceapp.localization.message
import com.cashierserviceapp.screens.addorder.AddOrderFormEvent
import com.cashierserviceapp.screens.addorder.AddOrderFormState
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.components.TextField
import com.cashierserviceapp.ui.theme.CashierServiceTheme

/**
 * Step 1 — who's dropping the device off. Only the name is required; the rest is contact detail the
 * shop can chase later, so an empty field is sent as null rather than an empty string.
 */
@Composable
internal fun CustomerStep(
    formState: AddOrderFormState,
    onInputChanged: (AddOrderFormEvent) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val phoneFocus = remember { FocusRequester() }
//    val emailFocus = remember { FocusRequester() }
    val addressFocus = remember { FocusRequester() }

    Column(modifier) {
        TextField(
            value = formState.name,
            onValueChange = { onInputChanged(AddOrderFormEvent.NameChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            error = formState.nameError != null,
            label = "Full name",
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { phoneFocus.requestFocus() })
        )

        FieldError(formState.nameError)

        Spacer(Modifier.height(12.dp))

        TextField(
            value = formState.phone,
            onValueChange = { onInputChanged(AddOrderFormEvent.PhoneChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            error = formState.phoneError != null,
            label = "Phone number",
            focusRequester = phoneFocus,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { addressFocus.requestFocus() })
        )

        FieldError(formState.phoneError)

//        Spacer(Modifier.height(12.dp))
//
//        TextField(
//            value = formState.email,
//            onValueChange = { onInputChanged(AddOrderFormEvent.EmailChanged(it)) },
//            modifier = Modifier.fillMaxWidth(),
//            error = formState.emailError != null,
//            label = "Email",
//            focusRequester = emailFocus,
//            enabled = enabled,
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Email,
//                imeAction = ImeAction.Next
//            ),
//            keyboardActions = KeyboardActions(onNext = { addressFocus.requestFocus() })
//        )
//
//        FieldError(formState.emailError)

        Spacer(Modifier.height(12.dp))

        TextField(
            value = formState.address,
            onValueChange = { onInputChanged(AddOrderFormEvent.AddressChanged(it)) },
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

/** The message under a field, or nothing at all when it's fine. */
@Composable
private fun FieldError(error: ValidationError?) {
    error ?: return

    Text(
        text = error.message(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        style = CashierServiceTheme.typography.text2,
        color = CashierServiceTheme.colors.dangerText
    )
}
