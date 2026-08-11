package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.Post

interface PostRepository {
    suspend fun getPosts(): Result<List<Post>>
}
