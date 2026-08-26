package com.cashierserviceapp.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.Theme
import com.cashierserviceapp.domain.models.Profile
import com.cashierserviceapp.domain.models.UserRole
import com.cashierserviceapp.domain.repositories.AuthRepository
import com.cashierserviceapp.domain.repositories.UserRepository
import com.cashierserviceapp.localization.AppLanguage
import com.cashierserviceapp.storage.ApplicationStorage
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class SettingsViewModel(
    private val storage: ApplicationStorage,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val theme: StateFlow<Theme> =
        storage.getTheme().stateIn(viewModelScope, SharingStarted.Eagerly, storage.getThemeBlocking())

    val language: StateFlow<AppLanguage> =
        storage.getLanguage()
            .stateIn(viewModelScope, SharingStarted.Eagerly, storage.getLanguageBlocking())

    /**
     * Seeded from the session so the card has a name and email on the first frame; the cached
     * `/me` and then the network fill in the rest. Success rather than Loading because there is
     * real data to read.
     */
    val profileState: StateFlow<Resource<Profile>>
        field = MutableStateFlow<Resource<Profile>>(
            storage.getSession()?.user
                ?.let { Resource.Success(Profile(name = it.name, email = it.email, role = it.role.toRole())) }
                ?: Resource.Loading()
        )

    val isSigningOut: StateFlow<Boolean>
        field = MutableStateFlow(false)

    private var profileJob: Job? = null
    private var signOutJob: Job? = null

    init {
        loadProfile()
    }

    fun loadProfile() {
        profileJob?.cancel()

        profileJob = viewModelScope.launch {
            userRepository.getProfile().collect { result ->
                result.fold(
                    onSuccess = { profile -> profileState.value = Resource.Success(profile) },
                    onFailure = { exception ->
                        // Keeps whichever card is showing — session seed or cache; the error only
                        // surfaces when there was nothing to show in the first place.
                        profileState.value = Resource.Error(
                            message = exception.message,
                            data = profileState.value.data
                        )
                    }
                )
            }
        }
    }

    fun setTheme(value: Theme) {
        viewModelScope.launch { storage.setTheme(value) }
    }

    fun setLanguage(value: AppLanguage) {
        viewModelScope.launch { storage.setLanguage(value) }
    }

    /**
     * Signs out, then reports back so the caller can navigate. [AuthRepository.logout] clears the
     * local session even if revoking server-side fails, so this always completes.
     */
    fun signOut(onSignedOut: () -> Unit) {
        if (signOutJob?.isActive == true) return

        signOutJob = viewModelScope.launch {
            isSigningOut.value = true
            authRepository.logout()
            isSigningOut.value = false
            onSignedOut()
        }
    }

    override fun onCleared() {
        super.onCleared()
        profileJob?.cancel()
        signOutJob?.cancel()
    }
}

/** The login response types role as a bare string; `GET /me` returns the enum. */
private fun String.toRole(): UserRole =
    if (equals(UserRole.ADMIN.name, ignoreCase = true)) UserRole.ADMIN else UserRole.CASHIER
