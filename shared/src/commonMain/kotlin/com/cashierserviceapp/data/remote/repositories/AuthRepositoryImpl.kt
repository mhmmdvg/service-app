package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.domain.models.AuthenticationPayload
import com.cashierserviceapp.domain.models.User
import com.cashierserviceapp.domain.network.AuthenticationApi
import com.cashierserviceapp.domain.repositories.AuthRepository
import com.cashierserviceapp.storage.ApplicationStorage
import com.cashierserviceapp.utils.Logger
import com.cashierserviceapp.utils.tagged
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AuthRepositoryImpl(
    private val api: AuthenticationApi,
    private val storage: ApplicationStorage,
    private val client: HttpClient,
    appScope: CoroutineScope,
    logger: Logger,
) : AuthRepository {
    private val taggedLogger = logger.tagged("AuthRepository")

    override val currentUser = storage.session
        .map { it?.user }
        .stateIn(appScope, SharingStarted.Eagerly, storage.session.value?.user)

    override val isLoggedIn = storage.session.map { it != null }

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val response = api.login(AuthenticationPayload(email = email, password = password))
        val authentication = when {
            // safeApiCall collapses *every* failure to null — a 401 from bad credentials and a
            // dead network are indistinguishable here, so this message has to stay neutral.
            // See AuthenticationApi if you want them told apart.
            response == null -> error("Could not sign in. Check your details and try again.")
            !response.status -> error(response.message)
            response.data == null -> error(response.message)
            else -> response.data
        }

        storage.setSession(authentication)
        // The bearer provider caches whatever loadTokens returned the first time it ran — which,
        // on a cold start, is null. Without clearing it here every request after login would
        // still go out unauthenticated until the process restarts.
        client.authProvider<BearerAuthProvider>()?.clearToken()
        taggedLogger.log { "Signed in as ${authentication.user.email}" }

        authentication.user
    }

    override suspend fun logout() {
        storage.clearSession()
        client.authProvider<BearerAuthProvider>()?.clearToken()
        taggedLogger.log { "Session cleared" }
    }
}
