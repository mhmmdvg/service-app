package com.cashierserviceapp.domain.usecases.corevalidation

import com.cashierserviceapp.domain.models.Validation
import dev.zacsweers.metro.Inject

@Inject
class ValidateEmail {
    private val emailReg = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    fun execute(email: String): Validation {
        if (email.isBlank()) {
            return Validation(
                success = false,
                message = "The email can't be blank",
            )
        }

        if (!emailReg.matches(email)) {
            return Validation(
                success = false,
                message = "The email format is not valid",
            )
        }

        return Validation(
            success = true,
        )
    }
}