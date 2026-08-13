package com.cashierserviceapp.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.User
import com.cashierserviceapp.domain.repositories.AuthRepository
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    val loginState: StateFlow<Resource<User>?>
        field = MutableStateFlow<Resource<User>?>(null)

    private var loginJob: Job? = null

    fun login(email: String, password: String) {
        if (loginJob?.isActive == true) return

        loginJob = viewModelScope.launch {
            loginState.value = Resource.Loading()
            authRepository.login(email.trim(), password)
                .fold(
                    onSuccess = { user -> loginState.value = Resource.Success(user) },
                    onFailure = { exception -> loginState.value = Resource.Error(exception.message) }
                )
        }
    }

    /** Drops the error banner once the user starts correcting their input. */
    fun clearError() {
        if (loginState.value is Resource.Error) loginState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        loginJob?.cancel()
    }
}
