package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.Authentication
import com.cashierserviceapp.domain.models.AuthenticationPayload
import com.cashierserviceapp.domain.models.HttpResponse

interface AuthenticationApi {
    suspend fun login(payload: AuthenticationPayload): HttpResponse<Authentication>?
}