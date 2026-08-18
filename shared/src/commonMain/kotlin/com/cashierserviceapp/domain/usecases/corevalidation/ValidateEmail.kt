package com.cashierserviceapp.domain.usecases.corevalidation

import dev.zacsweers.metro.Inject

@Inject
class ValidateEmail {
    private val emailReg = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun execute(email: String): Validation {
        if (email.isBlank()) return Validation.Invalid(ValidationError.EmailBlank)

        if (!emailReg.matches(email)) return Validation.Invalid(ValidationError.EmailFormat)

        return Validation.Valid
    }
}
