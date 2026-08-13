package com.cashierserviceapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Authentication(
    val user: User,
    val accessToken: String,
    val refreshToken: String,
    /** Access-token lifetime in seconds, as reported by the server. */
    val expiresIn: Int = 0,
)

/** Result of `POST /refresh`. The server rotates both tokens on every call. */
@Serializable
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int = 0,
)

@Serializable
data class RefreshTokenPayload(
    val refreshToken: String,
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