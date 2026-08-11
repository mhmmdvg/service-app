package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val orderCode: String,
    val qrToken: String,
    @SerialName("customerID") val customerId: String,
    @SerialName("cashierID") val cashierId: String
)
