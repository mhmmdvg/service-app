package com.cashierserviceapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Authentication(
    val user: User,
    val accessToken: String,
)

@Serializable
data class User(
    val role: String,
    val name: String,
    val email: String,
)

@Serializable
data class AuthenticationPayload(
    val email: String,
    val password: String,
)