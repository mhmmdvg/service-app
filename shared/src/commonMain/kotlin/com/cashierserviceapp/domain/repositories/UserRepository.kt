package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.Profile

interface UserRepository {
    suspend fun getProfile(): Result<Profile>
}
