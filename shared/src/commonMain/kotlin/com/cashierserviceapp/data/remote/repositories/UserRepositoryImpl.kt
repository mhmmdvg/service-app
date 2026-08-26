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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class UserRepositoryImpl(
    private val api: UserApi,
    private val profileDao: ProfileDao,
) : UserRepository {
    override fun getProfile(): Flow<Result<Profile>> {
        val refresh = flow { emit(fetchProfile()) }

        // Cached first, so phone and joined date are on the card before the network answers — or
        // instead of it, offline. `onStart` rather than `merge`: merge collects both at once, and
        // a network that wins the race would be overwritten by the older cached row.
        return refresh.onStart {
            profileDao.getProfile()?.let { emit(Result.success(it.toDomain())) }
        }
    }

    private suspend fun fetchProfile(): Result<Profile> = runCatching {
        val response = api.getProfile() ?: unreachable()
        val profile = response.data ?: throw Exception(response.message)

        profileDao.upsertProfile(profile.toEntity())

        profile
    }
}
