package com.cashierserviceapp.flags

import com.cashierserviceapp.storage.ApplicationStorage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Inject
@SingleIn(AppScope::class)
class FlagsManager(
    val platformFlags: Flags,
    private val storage: ApplicationStorage,
    private val scope: CoroutineScope,
) {
    val flags: StateFlow<Flags> = storage.getFlags()
        .filterNotNull()
        .stateIn(scope, SharingStarted.Eagerly, platformFlags)


    suspend fun initAndGetFlags(): Flags {
        val storedFlags = storage.getFlags().first()
        if (storedFlags == null) {
            storage.setFlags(platformFlags)
        }
        return storedFlags ?: platformFlags
    }

    fun resetFlags() {
        scope.launch {
            storage.setFlags(platformFlags)
        }
    }

    fun updateFlags(newFlags: Flags) {
        scope.launch {
            storage.setFlags(newFlags)
        }
    }
}