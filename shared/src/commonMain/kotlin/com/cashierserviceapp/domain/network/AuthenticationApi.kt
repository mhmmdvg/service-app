package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.Authentication
import com.cashierserviceapp.domain.models.AuthenticationPayload
import com.cashierserviceapp.domain.models.HttpResponse

interface AuthenticationApi {
    suspend fun login(payload: AuthenticationPayload): HttpResponse<Authentication>?

    /**
     * Revokes this session's refresh token server-side. Token rotation itself lives in the Ktor
     * `Auth` plugin (see AppBindings) rather than here, because it has to run inside the client
     * that is being refreshed.
     */
    suspend fun logout(refreshToken: String): HttpResponse<String>?
}
