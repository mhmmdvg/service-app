package com.cashierserviceapp.domain.usecases.addorder

import com.cashierserviceapp.domain.usecases.corevalidation.ValidateRequired
import com.cashierserviceapp.domain.usecases.corevalidation.Validation
import com.cashierserviceapp.domain.usecases.corevalidation.ValidationError
import com.cashierserviceapp.domain.usecases.corevalidation.firstInvalid
import dev.zacsweers.metro.Inject

/**
 * Mirrors `OrderController.create` on the backend: a new device needs a brand and model, and the
 * item it becomes needs a complaint.
 *
 * Lived on `DeviceDraft.isValid` before, where a UI data class was quietly the authority on a
 * server contract.
 */
@Inject
class ValidateDevice(private val validateRequired: ValidateRequired) {
    fun execute(brand: String, model: String, complaint: String): Validation = firstInvalid(
        validateRequired.execute(brand, ValidationError.DeviceBrandBlank),
        validateRequired.execute(model, ValidationError.DeviceModelBlank),
        validateRequired.execute(complaint, ValidationError.DeviceComplaintBlank),
    )
}
