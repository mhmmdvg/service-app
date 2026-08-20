package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** An item in the parts inventory. `GET /spare-parts`. */
@Serializable
data class SparePart(
    val id: String? = null,
    val name: String,
    val stock: Int = 0,
    @SerialName("cost_price")
    val costPrice: Long = 0,
    @SerialName("sell_price")
    val sellPrice: Long,
    val sku: String = "",
)

/**
 * `POST /spare-parts`.
 *
 * Also how a part typed in by hand reaches the server: order creation can only reference parts that
 * already exist, so an ad-hoc one is registered first and then attached by id. [stock] therefore
 * has to cover the quantity being used straight away, or `attachPart` rejects the order.
 */
@Serializable
data class CreateSparePartRequest(
    val name: String,
    val stock: Int,
    @SerialName("cost_price")
    val costPrice: Long = 0,
    @SerialName("sell_price")
    val sellPrice: Long,
    val sku: String,
)
