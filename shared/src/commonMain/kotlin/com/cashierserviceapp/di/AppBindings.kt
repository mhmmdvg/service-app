package com.cashierserviceapp.di

import androidx.lifecycle.ViewModel
import com.cashierserviceapp.URLs
import com.cashierserviceapp.getPlatformId
import com.cashierserviceapp.storage.ApplicationStorage
import com.cashierserviceapp.utils.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass
import io.ktor.client.plugins.logging.Logger as KtorLogger

private const val LOGIN_PATH = "login"

@BindingContainer
@ContributesTo(AppScope::class)
object AppBindings {
    @Provides
    @SingleIn(AppScope::class)
    fun provideMetroViewModelFactory(
        viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel>,
        manualAssistedFactoryProviders: Map<KClass<out ManualViewModelAssistedFactory>, () -> ManualViewModelAssistedFactory>
    ) : MetroViewModelFactory = object : MetroViewModelFactory() {
        override val viewModelProviders get() = viewModelProviders
        override val manualAssistedFactoryProviders get() = manualAssistedFactoryProviders
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideAppScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @BaseUrl
    @SingleIn(AppScope::class)
    fun provideBaseUrl(): String = URLs.ANDROID_LOCAL_URL

    @Provides
    @SingleIn(AppScope::class)
    fun provideHttpClient(
        @BaseUrl baseUrl: String,
        logger: Logger,
        storage: ApplicationStorage,
    ): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }

            install(Logging) {
                level = LogLevel.HEADERS
                this.logger = object : KtorLogger {
                    override fun log(message: String) {
                        logger.log("HttpClient") { message }
                    }
                }
            }

            expectSuccess = true
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        storage.getAccessToken()?.let { BearerTokens(it, null) }
                    }

                    // Ktor only attaches the token after a 401 challenge by default, which costs
                    // an extra round trip on every call. Attach it up front instead — except on
                    // the login endpoint, which is what issues the token in the first place.
                    sendWithoutRequest { request ->
                        !request.url.encodedPath.endsWith(LOGIN_PATH)
                    }
                }
            }

            install(DefaultRequest) {
                url.takeFrom(baseUrl)
            }
        }
    }
}