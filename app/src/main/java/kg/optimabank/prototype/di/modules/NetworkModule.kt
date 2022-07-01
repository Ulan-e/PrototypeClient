package kg.optimabank.prototype.di.modules

import dagger.Module
import dagger.Provides
import kg.optimabank.prototype.BuildConfig
import kg.optimabank.prototype.data.network.api.AuthorizationApi
import kg.optimabank.prototype.data.network.api.ServerApi
import kg.optimabank.prototype.data.network.api.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class NetworkModule {

    companion object {
        const val AUTH_BASE_URL = "https://keycloak.optimabank.kg/"
        const val SERVER_BASE_URL = "https://test-creditclaim:8443/"
    }

    @Provides
    @Auth
    fun provideAuthRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Server
    fun provideServerRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthApi(@Auth retrofit: Retrofit): AuthorizationApi = AuthorizationApi(retrofit)

    @Singleton
    @Provides
    fun provideServerApi(@Server retrofit: Retrofit): ServerApi = ServerApi(retrofit)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggerInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = getUnsafeOkHttpClient(authInterceptor, loggerInterceptor)

    @Singleton
    @Provides
    fun provideLoggerInterceptor() = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}