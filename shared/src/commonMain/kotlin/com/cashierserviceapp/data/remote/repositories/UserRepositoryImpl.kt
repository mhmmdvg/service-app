package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.data.local.dao.ProfileDao
import com.cashierserviceapp.data.local.mappers.toDomain
import com.cashierserviceapp.data.local.mappers.toEntity
import com.cashierserviceapp.domain.models.Profile
import com.cashierserviceapp.domain.network.UserApi
import com.cashierserviceapp.domain.repositories.UserRepository
import com.cashierserviceapp.utils.unreachable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class UserRepositoryImpl(
    private val api: UserApi,
    private val profileDao: ProfileDao,
) : UserRepository {
    override fun observeProfile(): Flow<Profile?> =
        profileDao.getProfile().map { it?.toDomain() }

    override suspend fun getProfile(): Result<Profile> = runCatching {
        val response = api.getProfile()
            ?: unreachable()

        val profile = response.data ?: throw Exception(response.message)

        profileDao.upsertProfile(profile.toEntity())

        profile
    }
}
