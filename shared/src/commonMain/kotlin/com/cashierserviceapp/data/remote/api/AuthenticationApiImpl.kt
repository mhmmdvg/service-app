package com.cashierserviceapp.data.remote.api

import com.cashierserviceapp.domain.models.Authentication
import com.cashierserviceapp.domain.models.AuthenticationPayload
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.network.AuthenticationApi
import com.cashierserviceapp.utils.Logger
import com.cashierserviceapp.utils.apiUrl
import com.cashierserviceapp.utils.safeApiCall
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class AuthenticationApiImpl(
    private val client: HttpClient,
    private val logger: Logger
) : AuthenticationApi {
    override suspend fun login(payload: AuthenticationPayload): HttpResponse<Authentication>? = safeApiCall(logger) {
        client.post {
            apiUrl("login")
            setBody(payload)
        }.body()
    }
}