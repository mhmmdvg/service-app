package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.domain.models.Post
import com.cashierserviceapp.domain.network.PostApi
import com.cashierserviceapp.domain.repositories.PostRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class PostRepositoryImpl(
    private val api: PostApi
) : PostRepository {
    override suspend fun getPosts(): Result<List<Post>> = runCatching {
        api.getPosts() ?: throw Exception("Failed to fetch posts")
    }
}
