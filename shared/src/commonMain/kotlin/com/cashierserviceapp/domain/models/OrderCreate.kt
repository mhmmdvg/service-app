package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request/response models for `POST /orders`.
 *
 * Every optional field defaults to `null` so kotlinx drops it from the payload entirely — the Vapor
 * DTOs decode absent keys as `nil`, which is how "fill it in later" is expressed on the wire.
 */

/**
 * Either points at an existing customer via [customerID], or carries the fields for a new one, in
 * which case the server requires a non-empty [name].
 */
@Serializable
data class CustomerInput(
    @SerialName("customer_id")
    val customerID: String? = null,
    val name: String? = null,
    val phone: String? = null,
//    val email: String? = null,
    val address: String? = null,
)

@Serializable
data class OrderPartInput(
    val sparePartID: String,
    val qty: Int,
)

/**
 * One device being taken in. Either [deviceID] for a device already on file, or [brand] + [model]
 * to register a new one — the server rejects the item if neither is supplied.
 *
 * [serviceFee] and [parts] are only filled in when the price is already certain without a
 * diagnosis. Left null they stay open, to be completed later through `PATCH /order-items/:id` and
 * the add-spare-part endpoint.
 */
@Serializable
data class OrderItemInput(
    val complaint: String,
    val deviceID: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val color: String? = null,
    val serviceFee: Long? = null,
    val parts: List<OrderPartInput>? = null,
)

@Serializable
data class CreateOrderRequest(
    val customer: CustomerInput,
    val items: List<OrderItemInput>,
)

/** Customer or cashier attached to an order. */
@Serializable
data class OrderParty(
    val id: String,
    val name: String,
)

@Serializable
data class CreatedOrder(
    val id: String? = null,
    @SerialName("order_code")
    val orderCode: String,
    @SerialName("qr_token")
    val qrToken: String,
    val customer: OrderParty,
    val cashier: OrderParty,
)

@Serializable
data class CreatedOrderItem(
    val id: String? = null,
    @SerialName("order_id")
    val orderID: String,
    @SerialName("device_id")
    val deviceID: String,
    val status: OrderStatus,
    val complaint: String? = null,
    @SerialName("service_fee")
    val serviceFee: Long? = null,
    @SerialName("final_cost")
    val finalCost: Long? = null,
)

@Serializable
data class CreateOrderResponse(
    val order: CreatedOrder,
    val items: List<CreatedOrderItem>,
)
