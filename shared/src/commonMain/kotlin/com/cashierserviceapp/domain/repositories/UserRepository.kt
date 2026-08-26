package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.Profile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /** The signed-in user as cached — null until `GET /me` has answered at least once. */
    fun observeProfile(): Flow<Profile?>

    /** `GET /me`, written through to the cache on the way past. */
    suspend fun getProfile(): Result<Profile>
}
