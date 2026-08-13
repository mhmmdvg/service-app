package com.cashierserviceapp.data.remote.api

import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.Profile
import com.cashierserviceapp.domain.network.UserApi
import com.cashierserviceapp.utils.Logger
import com.cashierserviceapp.utils.apiUrl
import com.cashierserviceapp.utils.safeApiCall
import com.cashierserviceapp.utils.tagged
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class UserApiImpl(
    private val client: HttpClient,
    private val logger: Logger
) : UserApi {
    private val taggedLogger = logger.tagged("UserApi")

    override suspend fun getProfile(): HttpResponse<Profile>? = safeApiCall(taggedLogger) {
        client.get { apiUrl("me") }.body()
    }
}
