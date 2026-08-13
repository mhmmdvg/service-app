package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    @SerialName("admin") ADMIN,
    @SerialName("cashier") CASHIER,
}

/**
 * `UserPublicDTO` from `GET /me` — the signed-in user, fuller than the [User] embedded in the login
 * response, which carries only role, name and email.
 */
@Serializable
data class Profile(
    val id: String? = null,
    val name: String,
    val email: String,
    val role: UserRole,
    val phone: String? = null,
    val createdAt: String? = null,
)
