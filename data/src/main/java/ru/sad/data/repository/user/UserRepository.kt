package ru.sad.data.repository.user

import android.app.Application
import android.net.Uri
import androidx.core.net.toFile
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.sad.data.R
import ru.sad.data.api.QuestionaryApi
import ru.sad.data.exceptions.QuestionaryException
import ru.sad.data.extensions.withContextSingle
import ru.sad.data.prefs.AuthPref
import ru.sad.data.repository.users.UsersRepositoryImpl
import ru.sad.domain.model.users.FirebaseRequest
import ru.sad.domain.model.users.FirebaseTokenResponse
import ru.sad.domain.model.users.LogoutResponse
import ru.sad.domain.model.users.UploadPhotoResponse
import ru.sad.domain.model.users.User
import ru.sad.domain.model.users.UserModel
import ru.sad.domain.model.users.UserRequest

class UserRepository(
    private val application: Application,
    private val firebaseAuth: FirebaseAuth,
    private val usersRepository: UsersRepositoryImpl,
    private val api: QuestionaryApi
) : UserRepositoryImpl {

    override suspend fun authUserByOneTap(credential: AuthCredential): FirebaseUser? {
        return withContext(Dispatchers.IO) {
            val task = firebaseAuth
                .signInWithCredential(credential)

            Tasks.await(task).user
        }
    }

    override suspend fun getCurrentUser(): User = withContextSingle {
        api.getUser(AuthPref.userId)
    }

    override suspend fun getInfoUser(userId: Int): User = withContextSingle {
        api.getUser(userId)
    }

    override suspend fun updateProfile(name: String, uri: Uri?) {
        withContext(Dispatchers.IO) {
            checkSelectedData(name, uri)

            val request = UserProfileChangeRequest
                .Builder()
                .setDisplayName(name)
                .setPhotoUri(uri)
                .build()

            firebaseAuth.currentUser?.let {
                it.updateProfile(request).await()
                usersRepository.putUser(it.toUserModel())
            }
        }
    }

    override suspend fun authByNickname(username: String, password: String): User = withContextSingle {
        api.auth(UserRequest(username, password))
    }

    override suspend fun logout(): LogoutResponse = withContextSingle {
        api.logout()
    }

    override fun clearAfterLogout() {
        AuthPref.userId = -1
        AuthPref.isAuthorized = false
        AuthPref.authToken = null
    }

    override suspend fun updateFirebaseToken(token: String): FirebaseTokenResponse =
        withContextSingle {
            api.sendFirebaseToken(FirebaseRequest(token))
        }

    override suspend fun uploadPhoto(uri: Uri?): UploadPhotoResponse = withContextSingle {
        if (uri == null) throw QuestionaryException(application.getString(R.string.error_image))

        val file = uri.toFile()

        api.uploadPhoto(
            MultipartBody.Part.createFormData(
                name = "image",
                filename = file.name,
                body = file.asRequestBody()
            )
        )
    }

    private fun checkSelectedData(name: String, uri: Uri?) {
        when {
            name.length < 2 -> throw QuestionaryException(application.getString(R.string.error_name_length))
            uri == null -> throw QuestionaryException(application.getString(R.string.error_image))
        }
    }

    private fun FirebaseUser.toUserModel() = UserModel(
        photoUrl = this.photoUrl.toString(),
        nickname = this.displayName.toString(),
        email = this.email.toString(),
        id = this.uid
    )

}