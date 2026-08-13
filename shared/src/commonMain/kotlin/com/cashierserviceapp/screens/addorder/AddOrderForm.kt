package com.cashierserviceapp.screens.addorder

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CustomerInput
import com.cashierserviceapp.domain.models.OrderItemInput

/**
 * The steps of the intake flow, in order. Each screen asks for one coherent group of fields, and
 * the flow only moves forward once the current one is [AddOrderForm.isValid].
 */
enum class AddOrderStep {
    /** Who's dropping the device off. */
    CUSTOMER,

    /** What they're dropping off, and what's wrong with it. */
    DEVICE;

    val isFirst: Boolean get() = ordinal == 0
    val isLast: Boolean get() = ordinal == entries.lastIndex

    fun next(): AddOrderStep = entries.getOrElse(ordinal + 1) { this }
    fun previous(): AddOrderStep = entries.getOrElse(ordinal - 1) { this }
}

/**
 * Everything the flow collects, held as raw strings so each field round-trips exactly what was
 * typed. [toRequest] is the single place that turns it into the wire format.
 */
data class AddOrderForm(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val brand: String = "",
    val model: String = "",
    val color: String = "",
    val complaint: String = "",
    val serviceFee: String = "",
) {
    /**
     * Whether [step] has everything the server insists on. Mirrors the checks in
     * `OrderController.create`: a new customer needs a name, and a new device needs brand + model.
     * Complaint is non-optional on the item itself.
     */
    fun isValid(step: AddOrderStep): Boolean = when (step) {
        AddOrderStep.CUSTOMER -> name.isNotBlank()
        AddOrderStep.DEVICE -> brand.isNotBlank() && model.isNotBlank() && complaint.isNotBlank()
    }

    /**
     * Blank optional fields are sent as null rather than `""` so the server stores nothing instead
     * of an empty string.
     *
     * The order carries a single item: one device per intake. `parts` is always omitted — spare
     * parts are attached after the diagnosis, through the add-part endpoint. [serviceFee] goes out
     * only when the cashier already knows the price; left empty it stays open for a `PATCH` later.
     */
    fun toRequest(): CreateOrderRequest = CreateOrderRequest(
        customer = CustomerInput(
            name = name.trim(),
            phone = phone.trim().orNullIfBlank(),
            email = email.trim().orNullIfBlank(),
            address = address.trim().orNullIfBlank(),
        ),
        items = listOf(
            OrderItemInput(
                complaint = complaint.trim(),
                brand = brand.trim(),
                model = model.trim(),
                color = color.trim().orNullIfBlank(),
                serviceFee = serviceFee.toServiceFeeOrNull(),
            )
        )
    )
}

private fun String.orNullIfBlank(): String? = ifBlank { null }

/** Digits only, and 0 is a real fee — only a blank field means "not decided yet". */
private fun String.toServiceFeeOrNull(): Long? = trim().ifBlank { null }?.toLongOrNull()
