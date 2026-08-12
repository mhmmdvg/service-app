package com.cashierserviceapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val orderCode: String,
    val qrToken: String,
    val customer: Customer,
    val cashier: Cashier
)

@Serializable
data class Customer(
    val id: String,
    val name: String,
)

@Serializable
data class Cashier(
    val id: String,
    val name: String,
)