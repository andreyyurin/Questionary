package ru.sad.data.api

import android.annotation.SuppressLint
import android.app.Application
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.sad.data.BuildConfig
import ru.sad.data.prefs.AuthPref
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class OkHttpBuilders @Inject constructor(
    private val application: Application,
    private val userAgent: String
) : OkHttpBuildersImpl {

    override val mainHttpBuilder = this
        .createOkHttpClientBuilder()
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
            val locale = Locale.getDefault().language

            builder.addHeader("accept", "application/json")
            builder.addHeader("User-agent", userAgent)
            builder.addHeader("Platform", "Android")
            builder.addHeader("Language", locale)

            AuthPref.authToken?.let {
                builder.addHeader("token", it)
            }

            chain.proceed(builder.build())
        }


    private fun createOkHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(BuildConfig.CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.READ_TIMEOUT_SEC, TimeUnit.SECONDS)
            .followRedirects(false).apply {
                if (BuildConfig.BUILD_TYPE in listOf("debug")) {
                    addHttpLoggingInterceptor()
                    allowNotCertificateSsl()
                }
            }
    }


    private fun OkHttpClient.Builder.addHttpLoggingInterceptor(): OkHttpClient.Builder {
        return addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    }

    private fun OkHttpClient.Builder.allowNotCertificateSsl(): OkHttpClient.Builder {
        val x509TrustManager = object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(x509TrustManager), SecureRandom())
        val sslSocketFactory = sslContext.socketFactory
        return sslSocketFactory(sslSocketFactory, x509TrustManager)
    }
}