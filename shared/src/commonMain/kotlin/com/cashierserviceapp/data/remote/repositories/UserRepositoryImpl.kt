package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.domain.models.Profile
import com.cashierserviceapp.domain.network.UserApi
import com.cashierserviceapp.domain.repositories.UserRepository
import com.cashierserviceapp.utils.unreachable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository {
    override suspend fun getProfile(): Result<Profile> = runCatching {
        val response = api.getProfile()
            ?: unreachable()

        response.data ?: throw Exception(response.message)
    }
}
