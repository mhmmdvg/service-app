package com.cashierserviceapp.data.local.mappers

import com.cashierserviceapp.data.local.entities.OrderEntity
import com.cashierserviceapp.domain.models.Order

fun Order.toEntity(): OrderEntity = OrderEntity(
    id = id,
    status = status,
    customerName = customerName,
    orderCode = orderCode,
    createdAt = createdAt,
    totalCost = totalCost,
    itemsCount = itemsCount,
)

fun OrderEntity.toDomain(): Order = Order(
    id = id,
    status = status,
    customerName = customerName,
    orderCode = orderCode,
    createdAt = createdAt,
    totalCost = totalCost,
    itemsCount = itemsCount,
)

fun List<Order>.toEntities(): List<OrderEntity> = map { it.toEntity() }

fun List<OrderEntity>.toDomain(): List<Order> = map { it.toDomain() }
