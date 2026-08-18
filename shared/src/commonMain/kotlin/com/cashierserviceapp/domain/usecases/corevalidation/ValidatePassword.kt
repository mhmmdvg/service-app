package com.cashierserviceapp.domain.usecases.corevalidation

import dev.zacsweers.metro.Inject

@Inject
class ValidatePassword {
    fun execute(password: String): Validation {
        if (password.isBlank()) return Validation.Invalid(ValidationError.PasswordBlank)

        if (password.length < 8) return Validation.Invalid(ValidationError.PasswordTooShort)

        val containsLettersAndDigits =
            password.any { it.isDigit() } && password.any { it.isLetter() }

        if (!containsLettersAndDigits) {
            return Validation.Invalid(ValidationError.PasswordNeedsLetterAndDigit)
        }

        return Validation.Valid
    }
}
