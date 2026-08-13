package com.cashierserviceapp.storage

import com.cashierserviceapp.Theme
import com.cashierserviceapp.domain.models.Authentication
import com.cashierserviceapp.flags.Flags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ApplicationStorage {
    val userId: StateFlow<String>

    /** The persisted login session, or `null` when signed out. */
    val session: StateFlow<Authentication?>

    /**
     * Synchronous session read for Ktor's `loadTokens`/`refreshTokens`.
     *
     * Reads straight through to settings rather than using [session], whose value is republished
     * asynchronously by `stateIn` — a token rotation followed immediately by a read would
     * otherwise see the old, now-revoked refresh token.
     */
    fun getSession(): Authentication?
    suspend fun setSession(value: Authentication)
    suspend fun clearSession()

    fun isOnboardingComplete(): Flow<Boolean>
    suspend fun setOnboardingComplete(value: Boolean)

    fun getTheme(): Flow<Theme>
    suspend fun setTheme(value: Theme)

    fun getFlagsBlocking(): Flags?
    fun getFlags(): Flow<Flags?>
    suspend fun setFlags(value: Flags)

    fun initialize()
}