package com.cashierserviceapp.storage

import com.cashierserviceapp.Theme
import com.cashierserviceapp.domain.models.Authentication
import com.cashierserviceapp.flags.Flags
import com.cashierserviceapp.getPlatformId
import com.cashierserviceapp.utils.Logger
import com.cashierserviceapp.utils.tagged
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@OptIn(ExperimentalSettingsApi::class)
class ApplicationStorageImpl(
    private val settings: ObservableSettings,
    appScope: CoroutineScope,
    logger: Logger
) : ApplicationStorage {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val taggedLogger = logger.tagged("ApplicationStorageImpl")

    private inline fun <reified T> String?.decodeOrNull(): T? {
        if (this == null) return null
        return try {
            json.decodeFromString<T>(this)
        } catch (_: SerializationException) {
            null
        }
    }

    override val userId = settings
        .getStringFlow(Keys.USER_ID, "")
        .stateIn(appScope, SharingStarted.Eagerly, "")

    override val session = settings
        .getStringOrNullFlow(Keys.SESSION)
        .map { it.decodeOrNull<Authentication>() }
        .stateIn(appScope, SharingStarted.Eagerly, readSession())

    override fun getSession(): Authentication? = readSession()

    override suspend fun setSession(value: Authentication) {
        settings[Keys.SESSION] = json.encodeToString(value)
    }

    override suspend fun clearSession() {
        settings.remove(Keys.SESSION)
    }

    private fun readSession(): Authentication? =
        settings.getStringOrNull(Keys.SESSION).decodeOrNull<Authentication>()

    override fun isOnboardingComplete(): Flow<Boolean> = settings.getBooleanFlow(Keys.ONBOARDING_COMPLETE, false)
    override suspend fun setOnboardingComplete(value: Boolean) = settings.set(Keys.ONBOARDING_COMPLETE, value)

    override fun getTheme(): Flow<Theme> = settings.getStringOrNullFlow(Keys.THEME).map { Theme.entries.firstOrNull { entry -> entry.name == it } ?: Theme.SYSTEM }
    override suspend fun setTheme(value: Theme) = settings.set(Keys.THEME, value.name)

    override fun getFlagsBlocking(): Flags? = settings.getStringOrNull(Keys.FLAGS)?.decodeOrNull<Flags>()

    override fun getFlags(): Flow<Flags?> = settings.getStringOrNullFlow(Keys.FLAGS).map { it.decodeOrNull<Flags>() }
    override suspend fun setFlags(value: Flags) = settings.set(Keys.FLAGS, json.encodeToString(value))

    override fun initialize() {
       ensureUserId()
    }

    private fun ensureUserId() {
        val existingUserId = settings.getString(Keys.USER_ID, "")
        if (existingUserId.isBlank()) {
            val generatedUserId = "${getPlatformId()}-${Uuid.random()}"
            settings.set(Keys.USER_ID, generatedUserId)
        }
    }
    private object Keys {
        const val STORAGE_VERSION = "storageVersion"
        const val USER_ID = "userId"
        const val ONBOARDING_COMPLETE = "onboardingComplete"
        const val THEME = "theme"
        const val FLAGS = "flags"
        const val CONFIG = "config"
        const val SESSION = "session"
    }
}