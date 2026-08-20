package com.cashierserviceapp.domain.usecases.corevalidation

import dev.zacsweers.metro.Inject

@Inject
class ValidatePhoneNumber {
    /** @param required when false a blank value passes. See [ValidateEmail.execute]. */
    fun execute(phoneNumber: String, required: Boolean = true): Validation {
        if (phoneNumber.isBlank()) {
            return if (required) {
                Validation.Invalid(ValidationError.PhoneNumberBlank)
            } else {
                Validation.Valid
            }
        }

        if (phoneNumber.length < 10) {
            return Validation.Invalid(ValidationError.PhoneNumberTooShort)
        }

        return Validation.Valid
    }
}
