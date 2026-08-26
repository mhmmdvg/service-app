package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.Profile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /**
     * The signed-in user: whatever was last cached, then `GET /me`, which is cached on the way past.
     *
     * Emits once or twice — the cached read is skipped when nothing has been stored yet, so a first
     * run straight after signing in emits only the network's answer.
     */
    fun getProfile(): Flow<Result<Profile>>
}
