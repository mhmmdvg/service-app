package com.cashierserviceapp.domain.usecases.corevalidation

import com.cashierserviceapp.domain.models.Validation

class ValidatePhoneNumber {
    fun execute(phoneNumber: String): Validation {
        if (phoneNumber.isBlank()) {
            return Validation(
                success = false,
                message = "The phone number can't be blank"
            )
        }

        if (phoneNumber.length < 10) {
            return Validation(
                success = false,
                message = "The phone number needs to consist of at least 10 characters"
            )
        }

        return Validation(
            success = true,
        )
    }
}