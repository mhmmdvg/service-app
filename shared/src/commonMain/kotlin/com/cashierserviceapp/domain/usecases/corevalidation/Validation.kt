package com.cashierserviceapp.domain.usecases.corevalidation

/**
 * The outcome of one field check.
 *
 * A failure carries a [ValidationError] rather than a message: the copy is localised, and this
 * layer has no business knowing which language the UI is in.
 */
sealed interface Validation {
    data object Valid : Validation

    data class Invalid(val error: ValidationError) : Validation
}

/** Every way a field can fail. The screen maps these to strings from `composeResources`. */
enum class ValidationError {
    EmailBlank,
    EmailFormat,
    PasswordBlank,
    PasswordTooShort,
    PasswordNeedsLetterAndDigit,
    PhoneNumberBlank,
    PhoneNumberTooShort,
}

/** Reads better than `is Validation.Invalid` at the call sites that only need a yes/no. */
val Validation.isValid: Boolean
    get() = this is Validation.Valid

/** The reason this check failed, or null when it passed. */
val Validation.errorOrNull: ValidationError?
    get() = (this as? Validation.Invalid)?.error
