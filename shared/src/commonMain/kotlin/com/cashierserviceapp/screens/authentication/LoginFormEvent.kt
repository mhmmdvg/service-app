package com.cashierserviceapp.screens.authentication

import com.cashierserviceapp.domain.usecases.corevalidation.ValidationError

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    // Held as the error *kind*, not a message: LoginScreen resolves the copy through
    // stringResource so it follows the app language.
    val emailError: ValidationError? = null,
    val passwordError: ValidationError? = null,
)

sealed class LoginFormEvent {
    data class EmailChanged(val email: String) : LoginFormEvent()
    data class PasswordChanged(val password: String) : LoginFormEvent()
}
