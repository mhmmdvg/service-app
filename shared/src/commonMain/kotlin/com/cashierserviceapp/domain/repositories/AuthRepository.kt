package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    /** Emits the signed-in user, or `null` when there is no session. */
    val currentUser: StateFlow<User?>
    val isLoggedIn: Flow<Boolean>

    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout()
}
