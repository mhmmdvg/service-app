package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.Order

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>
}