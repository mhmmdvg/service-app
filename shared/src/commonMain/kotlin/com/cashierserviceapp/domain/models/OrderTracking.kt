package com.cashierserviceapp.domain.models

import kotlinx.serialization.Serializable

/**
 * `GET /track/:qrToken` — what the QR code on a receipt resolves to.
 *
 * The only unauthenticated endpoint on the server: the token *is* the credential, so anyone holding
 * the printed receipt can check progress without an account.
 */
@Serializable
data class OrderTracking(
    val orderCode: String,
    val createdAt: String? = null,
    val items: List<OrderItemTracking> = emptyList(),
)

@Serializable
data class OrderItemTracking(
    val id: String? = null,
    val deviceBrand: String,
    val deviceModel: String,
    val status: OrderStatus,
    val complaint: String? = null,
    val serviceFee: Long? = null,
    val finalCost: Long? = null,
)
