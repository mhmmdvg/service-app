package com.cashierserviceapp.data.remote.api

import com.cashierserviceapp.URLs
import com.cashierserviceapp.domain.models.Post
import com.cashierserviceapp.domain.network.PostApi
import com.cashierserviceapp.utils.Logger
import com.cashierserviceapp.utils.apiUrl
import com.cashierserviceapp.utils.safeApiCall
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class PostApiImpl(
    private val client: HttpClient,
    private val logger: Logger
) : PostApi {
    override suspend fun getPosts(): List<Post>? = safeApiCall(logger) {
        client.get { apiUrl(URLs.JSON_PLACEHOLDER_URL, "posts") }.body()
    }
}
