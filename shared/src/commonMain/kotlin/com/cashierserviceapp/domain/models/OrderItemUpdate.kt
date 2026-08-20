package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `PATCH /order-items/:orderItemID`.
 *
 * Every field is optional and omitted when null, so a status change doesn't disturb a price that
 * was already settled. The server appends a status-history row whenever [status] actually differs
 * from what's stored, and recalculates the item's final cost either way.
 */
@Serializable
data class UpdateOrderItemRequest(
    val status: OrderStatus? = null,
    /** Free text kept with the history entry — why it moved, who to chase. */
    val note: String? = null,
    @SerialName("service_fee")
    val serviceFee: Long? = null,
)

/**
 * What `PATCH /order-items/:orderItemID` echoes back.
 *
 * Deliberately not [OrderDetailItem]: the patch returns the stored row, not the joined view, so
 * the device columns aren't in it. Every field is optional — the caller refetches the whole order
 * anyway, so a response that only confirms the write is still a success.
 */
@Serializable
data class UpdatedOrderItem(
    val id: String? = null,
    val status: OrderStatus? = null,
    @SerialName("service_fee")
    val serviceFee: Long? = null,
    @SerialName("final_cost")
    val finalCost: Long? = null,
)
