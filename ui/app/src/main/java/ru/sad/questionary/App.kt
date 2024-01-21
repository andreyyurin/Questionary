package ru.sad.questionary

import android.app.Application
import android.content.SharedPreferences
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.Kotpref.init
import com.chibatching.kotpref.gsonpref.gson
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.hilt.android.HiltAndroidApp
import ru.sad.base.navigation.MainNavigation
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var mainNavigation: MainNavigation

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        init(this)
        Kotpref.gson = Gson()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    companion object {
        lateinit var INSTANCE: App
            private set
    }
}