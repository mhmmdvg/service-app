package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.Post

/**
 * Test-only API against jsonplaceholder.typicode.com.
 *
 * Unlike [OrderApi] the responses are not wrapped in a
 * [com.cashierserviceapp.domain.models.HttpResponse] envelope, so the payload is returned as-is.
 */
interface PostApi {
    suspend fun getPosts(): List<Post>?
}
