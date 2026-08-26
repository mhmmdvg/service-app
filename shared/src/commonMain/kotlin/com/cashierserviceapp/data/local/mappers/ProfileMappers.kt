package com.cashierserviceapp.data.local.mappers

import com.cashierserviceapp.data.local.entities.ProfileEntity
import com.cashierserviceapp.domain.models.Profile

fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    userId = id,
    name = name,
    email = email,
    role = role,
    phone = phone,
    createdAt = createdAt,
)

fun ProfileEntity.toDomain(): Profile = Profile(
    id = userId,
    name = name,
    email = email,
    role = role,
    phone = phone,
    createdAt = createdAt,
)
