package com.cashierserviceapp.domain.usecases.corevalidation

import dev.zacsweers.metro.Inject

@Inject
class ValidatePhoneNumber {
    fun execute(phoneNumber: String): Validation {
        if (phoneNumber.isBlank()) return Validation.Invalid(ValidationError.PhoneNumberBlank)

        if (phoneNumber.length < 10) {
            return Validation.Invalid(ValidationError.PhoneNumberTooShort)
        }

        return Validation.Valid
    }
}
