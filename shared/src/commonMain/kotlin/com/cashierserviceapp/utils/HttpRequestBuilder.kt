package com.cashierserviceapp.utils

import com.cashierserviceapp.domain.models.HttpResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.utils.io.CancellationException

//suspend fun <T> safeApiCall(
//    logger: TaggedLogger,
//    call: suspend () -> T,
//): T? {
//    return try {
//        call()
//    } catch (e: CancellationException) {
//        throw e
//    } catch (e: Exception) {
//        logger.log { "API call failed: ${e.message}" }
//        null
//    }
//}

/**
 * Like [safeApiCall], but keeps the server's message on a failed status instead of collapsing it
 * to null. `ErrorEnvelopeMiddleware` renders every `Abort` as the same `{status, message}` shape as
 * a success, so validation reasons ("Nama customer wajib diisi…") survive the round trip and can be
 * shown on the form that caused them.
 */
suspend inline fun <reified T : Any> safeApiCall(
    logger: TaggedLogger,
    crossinline call: suspend () -> HttpResponse<T>,
): HttpResponse<T>? {
    return try {
        call()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        logger.log { "API call failed: ${e.message}" }
        runCatching { e.response.body<HttpResponse<T>>() }.getOrNull()
    } catch (e: Exception) {
        logger.log { "API call failed: ${e.message}" }
        null
    }
}

fun HttpRequestBuilder.apiUrl(path: String) {
    header(HttpHeaders.CacheControl, "no-cache")
    url {
        encodedPath = path
    }
}

/**
 * Same as [apiUrl], but targets [baseUrl] instead of the client-wide default. Setting the host here
 * makes `DefaultRequest` leave the URL alone, so the call bypasses the configured base URL.
 */
fun HttpRequestBuilder.apiUrl(baseUrl: String, path: String) {
    header(HttpHeaders.CacheControl, "no-cache")
    url {
        takeFrom(baseUrl)
        encodedPath = path
    }
}