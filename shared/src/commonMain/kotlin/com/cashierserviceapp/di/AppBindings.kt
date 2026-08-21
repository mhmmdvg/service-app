package com.cashierserviceapp.di

import androidx.lifecycle.ViewModel
import com.cashierserviceapp.URLs
import com.cashierserviceapp.data.local.dao.OrderDao
import com.cashierserviceapp.data.local.database.AppDatabase
import com.cashierserviceapp.data.local.database.DatabaseDriveFactory
import com.cashierserviceapp.data.local.database.getDatabaseBuilder
import com.cashierserviceapp.domain.models.RefreshTokenPayload
import com.cashierserviceapp.domain.models.TokenPair
import com.cashierserviceapp.storage.ApplicationStorage
import com.cashierserviceapp.utils.Logger
import com.cashierserviceapp.utils.apiUrl
import dev.zacsweers.metro.*
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass
import com.cashierserviceapp.domain.models.HttpResponse as AppHttpResponse
import io.ktor.client.plugins.logging.Logger as KtorLogger

private const val REFRESH_PATH = "refresh"

/** Endpoints that authenticate via a body payload, not a bearer header. */
private val UNAUTHENTICATED_PATHS = setOf("login", REFRESH_PATH, "logout")

@BindingContainer
@ContributesTo(AppScope::class)
object AppBindings {
    @Provides
    @SingleIn(AppScope::class)
    fun provideMetroViewModelFactory(
        viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel>,
        manualAssistedFactoryProviders: Map<KClass<out ManualViewModelAssistedFactory>, () -> ManualViewModelAssistedFactory>
    ): MetroViewModelFactory = object : MetroViewModelFactory() {
        override val viewModelProviders get() = viewModelProviders
        override val manualAssistedFactoryProviders get() = manualAssistedFactoryProviders
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideAppDatabase(factory: DatabaseDriveFactory): AppDatabase = getDatabaseBuilder(factory)

    @Provides
    @SingleIn(AppScope::class)
    fun provideOrderDao(database: AppDatabase): OrderDao = database.orderDao()

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
                        storage.getSession()?.let { BearerTokens(it.accessToken, it.refreshToken) }
                    }

                    // Runs when a request comes back 401. Ktor serialises this block behind a
                    // mutex, which matters here: the server rotates the refresh token on every
                    // call and treats a second use of an already-rotated token as a leak,
                    // revoking *every* session for the user. Concurrent refreshes would log the
                    // user out of all their devices.
                    refreshTokens {
                        val current = storage.getSession() ?: return@refreshTokens null

                        val pair = runCatching {
                            client.post {
                                apiUrl(REFRESH_PATH)
                                markAsRefreshTokenRequest()
                                contentType(ContentType.Application.Json)
                                setBody(RefreshTokenPayload(current.refreshToken))
                            }.body<AppHttpResponse<TokenPair>>().data
                        }.getOrNull()

                        if (pair == null) {
                            // Refresh token expired, revoked, or flagged as reused. The server has
                            // already dropped the session, so drop ours too — otherwise every
                            // later request retries a refresh that can never succeed.
                            logger.log("HttpClient") { "Token refresh failed, clearing session" }
                            storage.clearSession()
                            return@refreshTokens null
                        }

                        // Persist the rotated pair before handing it back: the old refresh token
                        // is dead the moment the server responded.
                        storage.setSession(
                            current.copy(
                                accessToken = pair.accessToken,
                                refreshToken = pair.refreshToken,
                                expiresIn = pair.expiresIn,
                            )
                        )
                        BearerTokens(pair.accessToken, pair.refreshToken)
                    }

                    // Ktor only attaches the token after a 401 challenge by default, which costs
                    // an extra round trip on every call. Attach it up front instead — except on
                    // the endpoints that take a refresh token in the body and need no bearer.
                    sendWithoutRequest { request ->
                        request.url.encodedPath.trim('/') !in UNAUTHENTICATED_PATHS
                    }
                }
            }

            install(DefaultRequest) {
                url.takeFrom(baseUrl)
            }
        }
    }
}