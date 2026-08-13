package com.cashierserviceapp.data.remote.api

import com.cashierserviceapp.domain.models.Authentication
import com.cashierserviceapp.domain.models.AuthenticationPayload
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.RefreshTokenPayload
import com.cashierserviceapp.domain.network.AuthenticationApi
import com.cashierserviceapp.utils.Logger
import com.cashierserviceapp.utils.apiUrl
import com.cashierserviceapp.utils.safeApiCall
import com.cashierserviceapp.utils.tagged
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AuthenticationApiImpl(
    private val client: HttpClient,
    private val logger: Logger
) : AuthenticationApi {

    private val taggedLogger = logger.tagged("AuthenticationApi")

    override suspend fun login(payload: AuthenticationPayload): HttpResponse<Authentication>? =
        safeApiCall(taggedLogger) {
            client.post {
                apiUrl("login")
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()
        }

    override suspend fun logout(refreshToken: String): HttpResponse<String>? = safeApiCall(taggedLogger) {
        client.post {
            apiUrl("logout")
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenPayload(refreshToken))
        }.body()
    }
}