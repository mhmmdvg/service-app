package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `GET /orders/:orderID` — one order in full, for the detail screen and the printed receipt. */
@Serializable
data class OrderDetail(
    val id: String? = null,
    @SerialName("order_code")
    val orderCode: String,
    @SerialName("qr_token")
    val qrToken: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("customer_name")
    val customerName: String,
    @SerialName("customer_phone")
    val customerPhone: String = "",
    @SerialName("cashier_name")
    val cashierName: String = "",
    val items: List<OrderDetailItem> = emptyList(),
)

@Serializable
data class OrderDetailItem(
    val id: String? = null,
    @SerialName("device_brand")
    val deviceBrand: String,
    @SerialName("device_model")
    val deviceModel: String,
    val status: OrderStatus,
    val complaint: String? = null,
    /** Null while the price is still open — see the add-order flow. */
    @SerialName("service_fee")
    val serviceFee: Long? = null,
    /** Service fee plus every part; null until either has been set. */
    @SerialName("final_cost")
    val finalCost: Long? = null,
    val parts: List<OrderPartDetail> = emptyList(),
)

@Serializable
data class OrderPartDetail(
    val id: String? = null,
    @SerialName("spare_part_id")
    val sparePartID: String? = null,
    @SerialName("spare_part_name")
    val sparePartName: String,
    val sku: String = "",
    val qty: Int,
    @SerialName("price_at_use")
    val priceAtUse: Long,
    val subtotal: Long,
)
