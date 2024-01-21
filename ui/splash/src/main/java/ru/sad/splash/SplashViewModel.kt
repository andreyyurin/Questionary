package ru.sad.splash

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.prefs.AuthPref
import ru.sad.data.prefs.AuthPref.isAuthorized
import ru.sad.data.prefs.AuthPref.userId
import ru.sad.data.repository.user.UserRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseMessaging: FirebaseMessaging
) : BaseFragmentViewModel() {

    private var timer: CountDownTimer? = null

    init {
        firebaseMessaging.token.addOnSuccessListener {
            Log.d("FB-TOKEN", it.toString())
        }
    }

    fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(1000, 200) {
            override fun onTick(millisUntilFinished: Long) {
                /* */
            }

            override fun onFinish() {
                openStartScreen()
            }
        }.start()
    }

    fun openStartScreen() {
        if (!AuthPref.isOnBoardingShown) {
            navigate(NavigationKey.ONBOARDING_SCREEN)
        } else {
            if (!isAuthorized) {
                navigate(NavigationKey.LOGIN_SCREEN)
            } else {
                navigate(NavigationKey.TOP_QUIZ_SCREEN)
            }
        }
    }
}