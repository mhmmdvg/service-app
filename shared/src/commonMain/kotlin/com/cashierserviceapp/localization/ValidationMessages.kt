package com.cashierserviceapp.localization

import androidx.compose.runtime.Composable
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.validation_device_brand_blank
import cashierserviceapp.shared.generated.resources.validation_device_complaint_blank
import cashierserviceapp.shared.generated.resources.validation_device_model_blank
import cashierserviceapp.shared.generated.resources.validation_email_blank
import cashierserviceapp.shared.generated.resources.validation_email_format
import cashierserviceapp.shared.generated.resources.validation_password_blank
import cashierserviceapp.shared.generated.resources.validation_password_letter_and_digit
import cashierserviceapp.shared.generated.resources.validation_password_too_short
import cashierserviceapp.shared.generated.resources.validation_name_blank
import cashierserviceapp.shared.generated.resources.validation_phone_blank
import cashierserviceapp.shared.generated.resources.validation_phone_too_short
import com.cashierserviceapp.domain.usecases.corevalidation.ValidationError
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The single place a [ValidationError] turns into words. Every form shares it, so a rule added in
 * `corevalidation` only needs its copy written once — and the `when` is exhaustive, so a new error
 * kind won't compile until it has a string.
 */
val ValidationError.stringResource: StringResource
    get() = when (this) {
        ValidationError.EmailBlank -> Res.string.validation_email_blank
        ValidationError.EmailFormat -> Res.string.validation_email_format
        ValidationError.PasswordBlank -> Res.string.validation_password_blank
        ValidationError.PasswordTooShort -> Res.string.validation_password_too_short
//        ValidationError.PasswordNeedsLetterAndDigit ->
//            Res.string.validation_password_letter_and_digit
        ValidationError.PhoneNumberBlank -> Res.string.validation_phone_blank
        ValidationError.PhoneNumberTooShort -> Res.string.validation_phone_too_short
        ValidationError.NameBlank -> Res.string.validation_name_blank
        ValidationError.DeviceBrandBlank -> Res.string.validation_device_brand_blank
        ValidationError.DeviceModelBlank -> Res.string.validation_device_model_blank
        ValidationError.DeviceComplaintBlank -> Res.string.validation_device_complaint_blank
    }

@Composable
fun ValidationError.message(): String = stringResource(stringResource)
