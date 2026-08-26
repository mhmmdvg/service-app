package com.cashierserviceapp.screens.addorder

import com.cashierserviceapp.domain.models.CustomerInput
import com.cashierserviceapp.domain.usecases.corevalidation.ValidationError
import com.cashierserviceapp.utils.formatRupiah


enum class AddOrderStep {
    /** Who's dropping the devices off. */
    CUSTOMER,

    /** What they're dropping off, what's wrong with each one, and what it'll cost. */
    DEVICE;

    val isFirst: Boolean get() = ordinal == 0
    val isLast: Boolean get() = ordinal == entries.lastIndex

    fun next(): AddOrderStep = entries.getOrElse(ordinal + 1) { this }
    fun previous(): AddOrderStep = entries.getOrElse(ordinal - 1) { this }
}

data class PartDraft(
    val localId: String,
    val sparePartId: String?,
    val name: String,
    val unitPrice: Long,
    val qty: Int,
) {
    val subtotal: Long get() = unitPrice * qty
    val subtotalLabel: String get() = formatRupiah(subtotal)
    val isManual: Boolean get() = sparePartId == null
}

data class DeviceDraft(
    val localId: String,
    val brand: String = "",
    val model: String = "",
    val color: String = "",
    val complaint: String = "",
    val serviceFee: String = "",
    val parts: List<PartDraft> = emptyList(),
) {
    val name: String get() = listOf(brand, model).filter { it.isNotBlank() }.joinToString(" ")

    val serviceFeeValue: Long? get() = serviceFee.trim().ifBlank { null }?.toLongOrNull()

    /** Parts plus the fee — what this device is expected to come to. */
    val total: Long get() = parts.sumOf { it.subtotal } + (serviceFeeValue ?: 0)

    val hasPrice: Boolean get() = serviceFeeValue != null || parts.isNotEmpty()
}
/**
 * Everything the intake flow collects, plus what's wrong with it.
 *
 * Errors are held as the error *kind*, not a message: the screen resolves the copy through
 * `stringResource` so it follows the app language — same as `LoginFormState`.
 */
data class AddOrderFormState(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val devices: List<DeviceDraft> = emptyList(),
    val nameError: ValidationError? = null,
    val phoneError: ValidationError? = null,
    val emailError: ValidationError? = null,
) {
    val total: Long get() = devices.sumOf { it.total }

//    val hasAnyPrice: Boolean get() = devices.any { it.hasPrice }

    fun customerInput(): CustomerInput = CustomerInput(
        name = name.trim(),
        // Blank optional fields go as null rather than "", so the server stores nothing.
        phone = phone.trim().ifBlank { null },
//        email = email.trim().ifBlank { null },
        address = address.trim().ifBlank { null },
    )
}

/**
 * Every edit the flow can receive.
 *
 * The devices are a list of their own, so alongside the customer's fields there are events for the
 * rows themselves — all still one-way, all still handled in `AddOrderViewModel.onAddOrderEvent`.
 *
 * There's no event for a part: parts belong to the device draft the sheet is editing, which doesn't
 * reach the form until it's saved.
 */
sealed class AddOrderFormEvent {
    data class NameChanged(val name: String) : AddOrderFormEvent()
    data class PhoneChanged(val phone: String) : AddOrderFormEvent()
//    data class EmailChanged(val email: String) : AddOrderFormEvent()
    data class AddressChanged(val address: String) : AddOrderFormEvent()

    /**
     * The device sheet reports back on save and only on save — never keystroke by keystroke, and
     * never for a device still being filled in. An upsert, so the same event both adds a new
     * device and updates one being corrected.
     */
    data class DeviceSaved(val device: DeviceDraft) : AddOrderFormEvent()
    data class DeviceRemoved(val localId: String) : AddOrderFormEvent()
}
