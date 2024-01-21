package ru.sad.data.repository.user

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import ru.sad.domain.model.users.FirebaseTokenResponse
import ru.sad.domain.model.users.LogoutResponse
import ru.sad.domain.model.users.UploadPhotoResponse
import ru.sad.domain.model.users.User

interface UserRepositoryImpl {

    suspend fun authUserByOneTap(credential: AuthCredential): FirebaseUser?

    suspend fun getCurrentUser(): User

    suspend fun updateProfile(name: String, uri: Uri?)

    suspend fun logout(): LogoutResponse

    suspend fun authByNickname(username: String, password: String): User

    suspend fun uploadPhoto(uri: Uri?): UploadPhotoResponse

    suspend fun updateFirebaseToken(token: String): FirebaseTokenResponse

    suspend fun getInfoUser(userId: Int): User

    fun clearAfterLogout()
}