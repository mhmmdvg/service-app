package com.cashierserviceapp.domain.usecases.addorder

import com.cashierserviceapp.domain.models.CreateOrderRequest
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.CreateSparePartRequest
import com.cashierserviceapp.domain.models.OrderItemInput
import com.cashierserviceapp.domain.models.OrderPartInput
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.domain.repositories.SparePartRepository
import dev.zacsweers.metro.Inject
import kotlin.time.Clock

/**
 * Creates one order, registering any hand-typed spare part first.
 *
 * The two-repository sequence is the whole reason this exists: order creation can only reference
 * parts that already have a catalogue id, so a part the cashier typed in has to become a real
 * inventory entry before the order using it can be sent. Any failure along the way aborts the
 * order rather than quietly dropping the part.
 */
@Inject
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val sparePartRepository: SparePartRepository,
) {
    suspend fun execute(intake: OrderIntake): Result<CreateOrderResponse> = runCatching {
        val items = intake.devices.map { device ->
            OrderItemInput(
                complaint = device.complaint.trim(),
                brand = device.brand.trim(),
                model = device.model.trim(),
                color = device.color?.trim()?.ifBlank { null },
                serviceFee = device.serviceFee,
                parts = device.parts
                    .map { part ->
                        val id = part.sparePartId ?: register(part).getOrThrow()
                        OrderPartInput(sparePartID = id, qty = part.qty)
                    }
                    .ifEmpty { null },
            )
        }

        orderRepository
            .createOrder(CreateOrderRequest(customer = intake.customer, items = items))
            .getOrThrow()
    }

    /**
     * Adds a hand-typed part to inventory and returns its new id.
     *
     * Stock is set to exactly the quantity being used, because `attachPart` refuses to attach more
     * than a part has in stock and immediately decrements it — a new part with zero stock would
     * fail the very order it was created for.
     */
    private suspend fun register(part: PartIntake): Result<String> = sparePartRepository
        .createSparePart(
            CreateSparePartRequest(
                name = part.name,
                stock = part.qty,
                // The counter only knows what it's charging; margin can be corrected in
                // inventory later.
                costPrice = 0,
                sellPrice = part.unitPrice,
                sku = generateSku(part.name),
            )
        )
        .mapCatching { created ->
            created.id
                ?: throw Exception("The server created \"${part.name}\" but didn't return its id.")
        }
}

/**
 * Inventory needs an SKU and the counter shouldn't have to invent one. Not unique-constrained
 * server-side, so a readable prefix plus a timestamp is enough to tell entries apart.
 */
private fun generateSku(name: String): String {
    val prefix = name.filter { it.isLetterOrDigit() }.take(6).uppercase().ifBlank { "PART" }
    return "$prefix-${Clock.System.now().toEpochMilliseconds() % 1_000_000}"
}
