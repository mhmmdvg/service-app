package com.cashierserviceapp.screens.addorder

import com.cashierserviceapp.domain.usecases.addorder.DeviceIntake
import com.cashierserviceapp.domain.usecases.addorder.OrderIntake
import com.cashierserviceapp.domain.usecases.addorder.PartIntake

/**
 * Form → intake. Only lifts the typed strings into parsed values; trimming and the wire shape are
 * [com.cashierserviceapp.domain.usecases.addorder.CreateOrderUseCase]'s job.
 */
internal fun AddOrderFormState.toIntake(): OrderIntake = OrderIntake(
    customer = customerInput(),
    devices = devices.map { device ->
        DeviceIntake(
            brand = device.brand,
            model = device.model,
            color = device.color,
            complaint = device.complaint,
            serviceFee = device.serviceFeeValue,
            parts = device.parts.map { part ->
                PartIntake(
                    sparePartId = part.sparePartId,
                    name = part.name,
                    unitPrice = part.unitPrice,
                    qty = part.qty,
                )
            },
        )
    },
)
