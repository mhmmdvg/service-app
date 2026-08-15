package com.cashierserviceapp.screens.addorder

import com.cashierserviceapp.domain.models.CustomerInput
import com.cashierserviceapp.utils.formatRupiah

/**
 * The steps of the intake flow, in order. Each screen asks for one coherent group of fields, and
 * the flow only moves forward once the current one is [AddOrderForm.isValid].
 */
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

/**
 * One spare part on a device, before the order exists.
 *
 * @param sparePartId the catalogue entry this came from, or null when the cashier typed it in.
 *   Order creation can only reference parts that already exist, so a null one is registered with
 *   the server at submit time and swapped for its new id — see `AddOrderViewModel`.
 */
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

/**
 * One device being taken in. Held as raw strings so each field round-trips exactly what was typed.
 *
 * @param localId identity for the list and for editing; the server assigns the real one.
 */
data class DeviceDraft(
    val localId: String,
    val brand: String = "",
    val model: String = "",
    val color: String = "",
    val complaint: String = "",
    val serviceFee: String = "",
    val parts: List<PartDraft> = emptyList(),
) {
    /** Mirrors `OrderController.create`: a new device needs brand + model, and an item a complaint. */
    val isValid: Boolean
        get() = brand.isNotBlank() && model.isNotBlank() && complaint.isNotBlank()

    val name: String get() = listOf(brand, model).filter { it.isNotBlank() }.joinToString(" ")

    val serviceFeeValue: Long? get() = serviceFee.trim().ifBlank { null }?.toLongOrNull()

    /** Parts plus the fee — what this device is expected to come to. */
    val total: Long get() = parts.sumOf { it.subtotal } + (serviceFeeValue ?: 0)

    val hasPrice: Boolean get() = serviceFeeValue != null || parts.isNotEmpty()
}

/** Everything the flow collects. [AddOrderViewModel] turns it into the wire format at submit. */
data class AddOrderForm(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val devices: List<DeviceDraft> = emptyList(),
) {
    fun isValid(step: AddOrderStep): Boolean = when (step) {
        AddOrderStep.CUSTOMER -> name.isNotBlank()
        // At least one device, and every one of them complete — a half-filled second device would
        // otherwise be sent as a real item.
        AddOrderStep.DEVICE -> devices.isNotEmpty() && devices.all { it.isValid }
    }

    val total: Long get() = devices.sumOf { it.total }

    val hasAnyPrice: Boolean get() = devices.any { it.hasPrice }

    fun customerInput(): CustomerInput = CustomerInput(
        name = name.trim(),
        // Blank optional fields go as null rather than "", so the server stores nothing.
        phone = phone.trim().ifBlank { null },
        email = email.trim().ifBlank { null },
        address = address.trim().ifBlank { null },
    )
}
