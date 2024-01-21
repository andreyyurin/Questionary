package ru.sad.onboarding

import android.hardware.TriggerEvent
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.prefs.AuthPref
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor() : BaseFragmentViewModel() {

    val transitionLive = MutableLiveData<State<Boolean>>()

    val onButtonClickLive = MutableLiveData<Boolean>()

    fun openLogin() {
        AuthPref.isOnBoardingShown = true

        if (!AuthPref.isAuthorized) {
            navigate(NavigationKey.LOGIN_SCREEN)
        } else {
            navigate(NavigationKey.TOP_QUIZ_SCREEN)
        }
    }
}