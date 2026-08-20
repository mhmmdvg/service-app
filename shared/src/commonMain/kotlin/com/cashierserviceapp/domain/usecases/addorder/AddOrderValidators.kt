package com.cashierserviceapp.domain.usecases.addorder

import com.cashierserviceapp.domain.usecases.corevalidation.ValidateEmail
import com.cashierserviceapp.domain.usecases.corevalidation.ValidatePhoneNumber
import com.cashierserviceapp.domain.usecases.corevalidation.ValidateRequired
import dev.zacsweers.metro.Inject

/** Every rule the intake flow needs, in one injectable bundle — same shape as `LoginValidators`. */
@Inject
data class AddOrderValidators(
    val validateRequired: ValidateRequired,
    val validatePhoneNumber: ValidatePhoneNumber,
    val validateEmail: ValidateEmail,
    val validateDevice: ValidateDevice,
)
