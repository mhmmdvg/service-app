package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Authentication(
    val user: User,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    /** Access-token lifetime in seconds, as reported by the server. */
    @SerialName("expires_in")
    val expiresIn: Int = 0,
)

/** Result of `POST /refresh`. The server rotates both tokens on every call. */
@Serializable
data class TokenPair(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("expires_in")
    val expiresIn: Int = 0,
)

@Serializable
data class RefreshTokenPayload(
    @SerialName("refresh_token")
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