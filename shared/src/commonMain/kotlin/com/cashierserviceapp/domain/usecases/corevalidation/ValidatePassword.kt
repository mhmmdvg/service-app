package com.cashierserviceapp.domain.usecases.corevalidation

import com.cashierserviceapp.domain.models.Validation
import dev.zacsweers.metro.Inject

@Inject
class ValidatePassword {
    fun execute(password: String): Validation {
        val containsLettersAndDigits = password.any { it.isDigit() } && password.any { it.isLetter() }

        if (password.length < 8) {
            return Validation(
                success = false,
                message = "The password needs to consist of at least 8 characters"
            )
        }

        if (!containsLettersAndDigits) {
            return Validation(
                success = false,
                message = "The password needs to contain at least one letter and digit"
            )
        }

        return Validation(
            success = true
        )
    }
}