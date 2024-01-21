package ru.sad.profile

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.ext.copyToFile
import ru.sad.base.ext.error
import ru.sad.base.ext.getCameraPhotoOrientation
import ru.sad.base.ext.loading
import ru.sad.base.ext.postSuccess
import ru.sad.base.ext.rotateImage
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.prefs.AuthPref
import ru.sad.data.repository.quiz.QuizRepositoryImpl
import ru.sad.data.repository.user.UserRepository
import ru.sad.data.repository.user.UserRepositoryImpl
import ru.sad.domain.model.quiz.QuizShortResponse
import ru.sad.domain.model.quiz.RemoveQuizResponse
import ru.sad.domain.model.users.LogoutResponse
import ru.sad.domain.model.users.User
import ru.sad.profile.items.ItemProfilePhoto
import ru.sad.profile.items.ItemProfileQuiz
import ru.sad.profile.items.ItemProfileVideo
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepositoryImpl,
    private val firebaseMessaging: FirebaseMessaging,
    private val quizRepository: QuizRepositoryImpl
) : BaseFragmentViewModel() {

    companion object {
        const val URI = "URI"

        private const val QUIZ_ID = "QUIZ_ID"
    }

    val photoLive = MutableLiveData<State<Boolean>>()
    val userUpdatingLive = MutableLiveData<State<Boolean>>()
    val userLive = MutableLiveData<State<User>>()
    val logoutLive = MutableLiveData<State<LogoutResponse>>()
    val videosLive = MutableLiveData<State<List<Uri>>>()
    val quizesLive = MutableLiveData<State<List<QuizShortResponse>>>()
    val quizRemoveLive = MutableLiveData<State<Pair<RemoveQuizResponse, Int>>>()
    val imagesLive = MutableLiveData<State<List<Pair<Uri, Bitmap>>>>()
    val videoRemoveLive = MutableLiveData<State<ItemProfileVideo>>()
    val photoRemoveLive = MutableLiveData<State<ItemProfilePhoto>>()

    var takePhotoUri: Uri? = null

    private var selectedPhotoUri: Uri? = null

    init {
        sendFirebaseToken()
    }

    fun openTopQuizScreen() {
        navigate(NavigationKey.TOP_QUIZ_SCREEN)
    }

    fun openUploadScreenWithImage(data: String, uri: Uri) {
        navigate(NavigationKey.PHOTO_STICKER_SCREEN, bundleOf(data to uri.toString()))
    }

    fun logout() {
        launchIO({
            logoutLive.error(it)
        }) {
            logoutLive.loading()
            logoutLive.postSuccess(userRepository.logout())
        }
    }

    fun openCamera() {
        navigate(NavigationKey.CAMERA_SCREEN)
    }

    fun openStartScreen() {
        userRepository.clearAfterLogout()
        navigate(NavigationKey.LOGIN_SCREEN)
    }

    fun openMainScreen(uri: Uri) {
        navigate(
            NavigationKey.MAIN_SCREEN, bundles = bundleOf(
                URI to uri.toString()
            )
        )
    }

    fun removeVideo(itemProfileVideo: ItemProfileVideo, uri: Uri) {
    }

    fun removeQuiz(id: Int, itemId: Int) {
        launchIO({
            quizRemoveLive.error(it)
        }) {
            quizRemoveLive.loading()
            quizRemoveLive.postSuccess(Pair(quizRepository.removeQuiz(id), itemId))
        }
    }

    fun removePhoto(itemProfilePhoto: ItemProfilePhoto, uri: Uri) {
//        launchIO({
//            photoRemoveLive.error(it)
//        }) {
//            photoRemoveLive.loading()
//            val result = videoHelper.removePhoto(uri)
//            if (result) {
//                photoRemoveLive.postSuccess(itemProfilePhoto)
//            } else {
//                photoRemoveLive.error("")
//            }
//        }
    }

    fun savePhoto(uri: Uri) {
        launchIO({
            photoLive.error(it)
        }, {
            photoLive.loading()

            val finalUri = uri.copyToFile(application).toUri()

            finalUri
                .getCameraPhotoOrientation(application)
                .rotateImage(application)

            selectedPhotoUri = finalUri

            photoLive.postSuccess(true)
            saveInProfile()
        })
    }

    fun openMainScreen() {
        navigate(NavigationKey.MAIN_SCREEN)
    }

    fun loadUser(userId: Int) {
        launchIO({
            userLive.error(it)
        }) {
            userLive.loading()
            userLive.postSuccess(userRepository.getInfoUser(userId))
        }
    }


    fun getUserQuizes(userId: Int, page: Int = 0, pageSize: Int = 20) {
        launchIO({
            quizesLive.error(it)
        }) {
            if (page == 0) {
                quizesLive.loading(emptyList())
            } else {
                quizesLive.loading(null)
            }
            delay(100) // Костыль, иначе было, что данные быстрей приходят, чем отрабатывает loading
            quizesLive.postSuccess(quizRepository.getQuizesByUser(userId, page, pageSize))
        }
    }


    fun getImages(list: List<Uri>) {
//        launchIO({
//            imagesLive.error(it)
//        }) {
//            imagesLive.loading()
//
//            val result = ArrayList<Pair<Uri, Bitmap>>()
//
//            list.forEach { uri ->
//                result.add(Pair(uri, videoHelper.getVideoFrame(uri, 1000L)))
//            }
//
//            imagesLive.postSuccess(result)
//        }
    }

    fun openQuizScreen(id: Int) {
        navigate(NavigationKey.QUIZ_SCREEN, bundles = bundleOf(QUIZ_ID to id))
    }

    private fun saveInProfile() {
        launchIO({
            userUpdatingLive.error(it)
        }, {
            userRepository.uploadPhoto(selectedPhotoUri)
        })
    }

    private fun sendFirebaseToken() {
        firebaseMessaging.token.addOnSuccessListener {
            launchIO {
                userRepository.updateFirebaseToken(it)
            }
        }
    }

}