package kg.optimabank.prototype.di.modules

import android.annotation.SuppressLint
import kg.optimabank.prototype.data.network.api.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

const val CUSTOM_TRUST_MANAGER = "CustomX509TrustManager"
const val TLS = "TLSv1.2"

fun OkHttpClient.Builder.ignoreAllSSLErrors(
    authInterceptor: AuthInterceptor,
    loggerInterceptor: HttpLoggingInterceptor
): OkHttpClient.Builder {
    val naiveTrustManager = @SuppressLint(CUSTOM_TRUST_MANAGER)
    object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> = arrayOf()
        override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) = Unit
        override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) = Unit
    }

    val insecureSocketFactory = SSLContext.getInstance(TLS).apply {
        val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
        init(null, trustAllCerts, SecureRandom())
    }.socketFactory

    this.addInterceptor(authInterceptor)
    this.addInterceptor(loggerInterceptor)
    sslSocketFactory(insecureSocketFactory, naiveTrustManager)
    hostnameVerifier { _, _ -> true }
    return this
}

@SuppressLint("TrustAllX509TrustManager")
fun getUnsafeOkHttpClient(
    authInterceptor: AuthInterceptor,
    loggerInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    return try {
        val trustAllCerts = arrayOf<TrustManager>(

            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {

                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOf()
                }
            }
        )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        val trustManagerFactory: TrustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers: Array<TrustManager> =
            trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + trustManagers.contentToString()
        }

        val trustManager =
            trustManagers[0] as X509TrustManager


        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustManager)
        builder.addInterceptor(authInterceptor)
        builder.addInterceptor(loggerInterceptor)
        builder.connectTimeout(1L, TimeUnit.MINUTES)
        builder.readTimeout(1L, TimeUnit.MINUTES)
        builder.writeTimeout(1L, TimeUnit.MINUTES)
        builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
        builder.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}