package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `GET /track/:qrToken` — what the QR code on a receipt resolves to.
 *
 * The only unauthenticated endpoint on the server: the token *is* the credential, so anyone holding
 * the printed receipt can check progress without an account.
 */
@Serializable
data class OrderTracking(
    @SerialName("order_code")
    val orderCode: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    val items: List<OrderItemTracking> = emptyList(),
)

@Serializable
data class OrderItemTracking(
    val id: String? = null,
    @SerialName("device_brand")
    val deviceBrand: String,
    @SerialName("device_model")
    val deviceModel: String,
    val status: OrderStatus,
    val complaint: String? = null,
    @SerialName("service_fee")
    val serviceFee: Long? = null,
    @SerialName("final_cost")
    val finalCost: Long? = null,
)
