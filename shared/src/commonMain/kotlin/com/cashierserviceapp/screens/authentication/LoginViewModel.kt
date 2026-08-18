package com.cashierserviceapp.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.User
import com.cashierserviceapp.domain.repositories.AuthRepository
import com.cashierserviceapp.domain.usecases.login.LoginValidators
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class LoginViewModel(
    private val authRepository: AuthRepository,
    private val validators: LoginValidators
) : ViewModel() {
    val loginState: StateFlow<Resource<User>?>
        field = MutableStateFlow<Resource<User>?>(null)

    /* Form State */
    val formState: StateFlow<LoginFormState>
        field = MutableStateFlow(LoginFormState())

    private var loginJob: Job? = null


    fun onLoginEvent(event: LoginFormEvent) {
        when (event) {
            is LoginFormEvent.EmailChanged -> formState.update { it.copy(email = event.email, emailError = null) }
            is LoginFormEvent.PasswordChanged -> formState.update {
                it.copy(
                    password = event.password,
                    passwordError = null
                )
            }
        }
    }

    fun onLogin() {
        if (loginJob?.isActive == true) return

        val emailValidation = validators.validateEmail.execute(formState.value.email)
        val passwordValidation = validators.validatePassword.execute(formState.value.password)
        val hasError = listOf(
            emailValidation,
            passwordValidation
        ).any { !it.success }

        if (hasError) {
            formState.update {
                it.copy(
                    emailError = emailValidation.message,
                    passwordError = passwordValidation.message
                )
            }
            return
        }

        loginJob = viewModelScope.launch {
            loginState.value = Resource.Loading()

            authRepository.login(
                formState.value.email,
                formState.value.password
            ).fold(
                onSuccess = { user -> loginState.value = Resource.Success(user) },
                onFailure = { exception -> loginState.value = Resource.Error(exception.message) }
            )
        }
    }

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
