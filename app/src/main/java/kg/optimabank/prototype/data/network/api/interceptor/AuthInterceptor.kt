package kg.optimabank.prototype.data.network.api.interceptor

import kg.optimabank.prototype.data.network.api.AuthorizationApi
import kg.optimabank.prototype.data.network.api.AuthorizationApi.Factory.CUSTOM_HEADER
import kg.optimabank.prototype.data.network.api.AuthorizationApi.Factory.NO_AUTH
import kg.optimabank.prototype.data.preferences.SessionPreferences
import kg.optimabank.prototype.dev.UserSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject
import javax.inject.Provider

class AuthInterceptor @Inject constructor(
    private val sessionPreferences: SessionPreferences,
    private val authorizationApi: Provider<AuthorizationApi>
) : Interceptor {

    private val mutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().also { Timber.d("mutex [1] $it") }

        if (NO_AUTH in request.headers.values(CUSTOM_HEADER)) {
            return chain.proceedWithToken(request, null)
        }

        val token = runBlocking {
            sessionPreferences.getUserPreferences().first()?.accessToken
        }.also { Timber.d("mutex [2] $request $this") }
        val response = chain.proceedWithToken(request, token)

        if (response.code != HTTP_UNAUTHORIZED || token == null) {
            return response
        }

        Timber.d("mutex [3] $request")

        val newToken: String? = runBlocking {
            mutex.withLock {
                val userSettings = sessionPreferences.getUserPreferences().first()
                val maybeUpdatedToken = userSettings?.accessToken

                when (maybeUpdatedToken) {
                    null -> null.also { Timber.d("mutex [5-1] $request") }
                    else -> {
                        Timber.d("mutex [5-2] $request")

                        val refreshTokenResponse = authorizationApi.get().refreshToken(
                            clientId = "crm-app",
                            grantType = "refresh_token",
                            refreshToken = userSettings.refreshToken
                        )
                            .also { Timber.d("[6] $request $it") }

                        val statusCode = refreshTokenResponse.code()

                        when (statusCode) {
                            HTTP_OK -> {
                                refreshTokenResponse.body()?.accessToken.also { newAccessToken ->
                                    Timber.d("mutex [7-1] $request")

                                    sessionPreferences.setUserPreferences(
                                        UserSettings.newBuilder()
                                            .setClientId(userSettings.clientId)
                                            .setUsername(userSettings.username)
                                            .setPassword(userSettings.password)
                                            .setRefreshToken(userSettings.refreshToken)
                                            .setAccessToken(newAccessToken)
                                            .build()
                                    )

                                    sessionPreferences.setUserPreferences(
                                        userSettings.toBuilder()
                                            .setAccessToken(newAccessToken)
                                            .build()
                                    )
                                }
                            }
                            HTTP_UNAUTHORIZED -> {
                                Timber.d("mutex [7-2] $request")
                                sessionPreferences.clearSession()
                                null
                            }
                            else -> {
                                Timber.d("mutex [7-3] $request")
                                null
                            }
                        }
                    }
                }
            }
        }

        return if (newToken !== null) chain.proceedWithToken(request, newToken) else response
    }

    private fun Interceptor.Chain.proceedWithToken(request: Request, token: String?): Response =
        request.newBuilder()
            .apply {
                if (token !== null) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .removeHeader(CUSTOM_HEADER)
            .build()
            .let(::proceed)
}