package com.cashierserviceapp.domain.usecases.corevalidation

import dev.zacsweers.metro.Inject

@Inject
class ValidateEmail {
    private val emailReg = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    /**
     * @param required when false a blank value passes, for forms where the field is optional but
     *   still has to be well-formed once someone types in it.
     */
    fun execute(email: String, required: Boolean = true): Validation {
        if (email.isBlank()) {
            return if (required) Validation.Invalid(ValidationError.EmailBlank) else Validation.Valid
        }

        if (!emailReg.matches(email)) return Validation.Invalid(ValidationError.EmailFormat)

        return Validation.Valid
    }
}
