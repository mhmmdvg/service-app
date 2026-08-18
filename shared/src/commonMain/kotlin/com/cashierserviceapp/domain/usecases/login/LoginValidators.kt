package com.cashierserviceapp.domain.usecases.login

import com.cashierserviceapp.domain.usecases.corevalidation.ValidateEmail
import com.cashierserviceapp.domain.usecases.corevalidation.ValidatePassword
import dev.zacsweers.metro.Inject

@Inject
data class LoginValidators(
    val validateEmail: ValidateEmail,
    val validatePassword: ValidatePassword,
)