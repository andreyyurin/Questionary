package ru.sad.base.di

import android.annotation.SuppressLint
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.sad.base.analytics.AnalyticsLogger
import ru.sad.base.error.ErrorImpl
import ru.sad.base.error.HandlerError
import ru.sad.base.navigation.MainNavigation
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.BuildConfig
import ru.sad.data.api.OkHttpBuilders
import ru.sad.data.api.OkHttpBuildersImpl
import ru.sad.data.api.QuestionaryApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    companion object {
        private const val SHARED_PREFERENCES_NAME = "Lifestory"
        private const val APP_NAME = "Questionary"
        private const val APP_VERSION = "1.0.1"

        private val userAgent: String by lazy {
            APP_NAME + "/" + APP_VERSION + " " + System.getProperty("http.agent")
        }
    }

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }


    @Singleton
    @Provides
    fun provideResources(application: Application): Resources {
        return application.resources
    }

    @Singleton
    @Provides
    fun provideHandlerError(resources: Resources): ErrorImpl {
        return HandlerError(resources)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideNavigation(): MainNavigation {
        return MainNavigation
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Singleton
    @Provides
    fun provideOneTapClient(context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance("gs://familylocator-12856.appspot.com")
    }

    @Singleton
    @Provides
    fun provideFusedProviderClientLocation(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    @Singleton
    @Provides
    fun provideFirebaseLogger(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Singleton
    @Provides
    fun analyticsLogger(
        firebaseAuth: FirebaseAuth,
        firebaseLogger: FirebaseAnalytics
    ): AnalyticsLogger {
        return AnalyticsLogger(firebaseAuth, firebaseLogger)
    }

    @Singleton
    @Provides
    fun provideOkHttpBuilders(application: Application): OkHttpBuildersImpl =
        OkHttpBuilders(application, userAgent)

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        val level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        return interceptor.setLevel(level)
    }

    @Singleton
    @Provides
    fun provideLifestory(
        application: Application,
        okHttpClientBuilders: OkHttpBuildersImpl,
        interceptor: HttpLoggingInterceptor,
    ): QuestionaryApi {
        val chuckInterceptorWrapper = ChuckerInterceptor(application)

        val okHttpClient = okHttpClientBuilders
            .mainHttpBuilder
            .addInterceptor(chuckInterceptorWrapper)
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.QUESTIONARY_API_ENDPOINT)
            .client(okHttpClient)
            .build()
            .create(QuestionaryApi::class.java)
    }

    @Singleton
    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}
