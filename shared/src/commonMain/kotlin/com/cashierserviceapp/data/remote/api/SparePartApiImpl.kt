package com.cashierserviceapp.data.remote.api

import com.cashierserviceapp.domain.models.CreateSparePartRequest
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.SparePart
import com.cashierserviceapp.domain.network.SparePartApi
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
class SparePartApiImpl(
    private val client: HttpClient,
    private val logger: Logger
) : SparePartApi {
    private val taggedLogger = logger.tagged("SparePartApi")

    override suspend fun getSpareParts(): HttpResponse<List<SparePart>>? = safeApiCall(taggedLogger) {
        client.get { apiUrl("spare-parts") }.body()
    }

    override suspend fun createSparePart(
        request: CreateSparePartRequest
    ): HttpResponse<SparePart>? = safeApiCall(taggedLogger) {
        client.post {
            apiUrl("spare-parts")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
