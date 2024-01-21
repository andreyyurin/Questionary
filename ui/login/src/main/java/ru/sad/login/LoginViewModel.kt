package ru.sad.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.ext.error
import ru.sad.base.ext.loading
import ru.sad.base.ext.postSuccess
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.prefs.AuthPref.authToken
import ru.sad.data.prefs.AuthPref.isAuthorized
import ru.sad.data.prefs.AuthPref.userId
import ru.sad.data.repository.user.UserRepositoryImpl
import ru.sad.domain.model.users.User
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepositoryImpl
) : BaseFragmentViewModel() {

    val loginProgress = MutableLiveData<State<User>>()

    fun login(username: String, password: String) {
        launchIO {
            loginProgress.loading()

            try {
                val result = userRepository.authByNickname(username, password)
                loginProgress.postSuccess(result)
                authToken = result.token
                userId = result.id
                isAuthorized = true
            } catch (e: Exception) {
                loginProgress.error(e.message.toString())
            }
        }
    }

    fun openMainScreen() {
        navigate(NavigationKey.TOP_QUIZ_SCREEN)
    }
}