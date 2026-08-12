package com.cashierserviceapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val status: Status,
    val customerName: String,
    val orderCode: String,
    val createdAt: String,
    val totalCost: Int,
    val itemsCount: Int,
)

