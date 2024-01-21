package ru.sad.questionary

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.navigation.MainNavigation
import ru.sad.base.navigation.NavigationKey
import ru.sad.base.navigation.NavigationParams
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val navigation: MainNavigation
) : ViewModel() {

    val navigationListener = navigation

    fun navigate(key: NavigationKey, bundles: Bundle = bundleOf()) {
        navigation.postValue(NavigationParams(key, bundles))
    }
}