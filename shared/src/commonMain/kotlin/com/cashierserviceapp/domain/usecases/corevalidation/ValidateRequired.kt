package com.cashierserviceapp.domain.usecases.corevalidation

import dev.zacsweers.metro.Inject

/**
 * A field that simply has to be filled in.
 *
 * The caller names the failure, so one rule serves every required field without each one needing a
 * near-identical class of its own.
 */
@Inject
class ValidateRequired {
    fun execute(value: String, whenBlank: ValidationError): Validation =
        if (value.isBlank()) Validation.Invalid(whenBlank) else Validation.Valid
}
