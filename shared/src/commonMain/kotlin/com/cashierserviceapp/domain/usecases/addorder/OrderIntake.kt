package com.cashierserviceapp.domain.usecases.addorder

import com.cashierserviceapp.domain.models.CustomerInput

/**
 * One repair being taken in, with the values already lifted out of the form.
 *
 * Deliberately not the wire format: parts may still be hand-typed and have no server id yet, which
 * [CreateOrderUseCase] resolves before the order can be created.
 */
data class OrderIntake(
    val customer: CustomerInput,
    val devices: List<DeviceIntake>,
)

data class DeviceIntake(
    val brand: String,
    val model: String,
    val color: String?,
    val complaint: String,
    val serviceFee: Long?,
    val parts: List<PartIntake>,
)

/** @param sparePartId the catalogue entry, or null when the cashier typed the part in by hand. */
data class PartIntake(
    val sparePartId: String?,
    val name: String,
    val unitPrice: Long,
    val qty: Int,
) {
    val isManual: Boolean get() = sparePartId == null
}
