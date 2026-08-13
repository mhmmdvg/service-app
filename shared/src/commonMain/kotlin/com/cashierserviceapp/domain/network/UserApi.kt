package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.Profile

interface UserApi {
    /** The signed-in user, resolved server-side from the bearer token. */
    suspend fun getProfile(): HttpResponse<Profile>?
}
