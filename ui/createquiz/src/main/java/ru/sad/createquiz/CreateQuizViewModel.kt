package ru.sad.createquiz

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.checkerframework.checker.units.qual.s
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.ext.copyToFile
import ru.sad.base.ext.error
import ru.sad.base.ext.getCameraPhotoOrientation
import ru.sad.base.ext.loading
import ru.sad.base.ext.mapWithScreenType
import ru.sad.base.ext.postSuccess
import ru.sad.base.ext.rotateImage
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.exceptions.QuestionaryException
import ru.sad.data.repository.quiz.QuizRepositoryImpl
import ru.sad.data.repository.simple.SimpleRepositoryImpl
import ru.sad.domain.model.quiz.CreateQuizResponse
import ru.sad.domain.model.quiz.QuizAnswer
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.domain.model.quiz.QuizCountry
import ru.sad.domain.model.quiz.QuizResponse
import ru.sad.domain.model.simple.SimpleTypeScreenEnum
import javax.inject.Inject

@HiltViewModel
class CreateQuizViewModel @Inject constructor(
    private val application: Application,
    private val quizRepository: QuizRepositoryImpl,
    private val simpleRepository: SimpleRepositoryImpl
) : BaseFragmentViewModel() {

    val quizUploadLive = MutableLiveData<State<CreateQuizResponse?>>()

    val photoLive = MutableLiveData<State<Boolean>>()

    val categoriesLive = MutableLiveData<State<List<QuizCategory>>>()

    val countriesLive = MutableLiveData<State<List<QuizCountry>>>()

    val createQuizLive = MutableLiveData<State<Boolean>>()

    val dialogSelectCategoryLive: MutableLiveData<QuizCategory> =
        simpleRepository.simpleSelectedData.mapWithScreenType(SimpleTypeScreenEnum.CREATE_QUIZ_TYPE)

    val dialogSelectCountryLive: MutableLiveData<QuizCountry> =
        simpleRepository.simpleSelectedData.mapWithScreenType(SimpleTypeScreenEnum.CREATE_QUIZ_TYPE)

//    val dialogCategoryDataLive = simpleRepository.simpleWaitingData
//    val dialogCountryDataLive = simpleRepository.simpleWaitingData

    var currentQuiz: QuizResponse? = null

    private var selectedPhotoUri: Uri? = null

    fun createQuiz() {
        launchIO({
            quizUploadLive.error(it)
        }) {
            quizUploadLive.loading()
            checkParameters()
            currentQuiz?.questions?.removeLast()

            quizUploadLive.postSuccess(quizRepository.createQuiz(currentQuiz!!, selectedPhotoUri))
        }
    }

    fun openFinalScreen() {
        navigate(
            NavigationKey.CREATE_QUIZ_FINAL_SCREEN,
            bundles = bundleOf()
        )
    }

    fun openMainScreen() {
        navigate(NavigationKey.TOP_QUIZ_SCREEN)
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
        })
    }

    fun openCamera() {
        navigate(NavigationKey.CAMERA_SCREEN)
    }

    fun loadCategories() {
        launchIO({
            categoriesLive.error(it)
        }) {
            categoriesLive.loading()
            categoriesLive.postSuccess(quizRepository.getCategories())
        }
    }

    fun loadCountries() {
        launchIO({
            countriesLive.error(it)
        }) {
            countriesLive.loading()
            countriesLive.postSuccess(quizRepository.getCountries())
        }
    }

    fun setupQuestionTitle(title: String, posQuestion: Int) {
        currentQuiz?.questions?.get(posQuestion)?.question = title
    }

    fun setupQuestionAnswer(title: String, posQuestion: Int, posAnswer: Int) {
        if (posQuestion >= (currentQuiz?.questions?.size ?: 0)
            || posAnswer >= (currentQuiz?.questions?.get(posQuestion)?.answers?.size ?: 0)
        ) {
            return
        }
        currentQuiz?.questions?.get(posQuestion)?.answers?.get(posAnswer)?.answer = title
    }

    fun removeAnswer(posQuestion: Int, posAnswer: Int) {
        currentQuiz?.questions?.get(posQuestion)?.answers?.removeAt(posAnswer)
    }

    fun selectAnswer(isTrue: Boolean, posQuestion: Int, posAnswer: Int) {
        currentQuiz?.questions?.get(posQuestion)?.answers?.get(posAnswer)?.isTrue = isTrue
    }

    fun checkQuiz() {
        launchIO({
            createQuizLive.error(it)
        }) {
            createQuizLive.loading()

            if (currentQuiz == null) throw QuestionaryException(application.getString(R.string.create_quiz_error_app))

            if (currentQuiz?.title.isNullOrEmpty()) throw QuestionaryException(
                application.getString(
                    R.string.create_quiz_error_title_miss
                )
            )

            val questionsSize = (currentQuiz?.questions?.size ?: 2) - 2

            for (i in 0..questionsSize) {
                val currentQuestion = currentQuiz?.questions?.get(i)

                if (currentQuestion?.question.isNullOrEmpty()) throw QuestionaryException(
                    application.getString(R.string.create_quiz_error_title_question_miss)
                )

                var haveTrueAnswer = false
                val answersSize = (currentQuestion?.answers?.size ?: 1) - 1

                for (j in 0..answersSize) {
                    if (currentQuestion?.answers?.get(j)?.answer.isNullOrEmpty()) throw QuestionaryException(
                        application.getString(R.string.create_quiz_error_answer_blank)
                    )
                    if (currentQuestion?.answers?.get(j)?.isTrue == true) haveTrueAnswer = true
                }

                if (!haveTrueAnswer) throw QuestionaryException(application.getString(R.string.create_quiz_error_answer_no_correct))
            }

            createQuizLive.postSuccess(true)
        }
    }

    private fun checkParameters() {
        if (selectedPhotoUri == null) throw QuestionaryException(application.getString(R.string.create_quiz_final_error_photo))
        if (currentQuiz?.category == null || currentQuiz?.category == -1) throw QuestionaryException(
            application.getString(R.string.create_quiz_final_error_category)
        )
        if (currentQuiz?.country.isNullOrEmpty()) throw QuestionaryException(
            application.getString(R.string.create_quiz_final_error_category)
        )
    }

    fun clear() {
        onCleared()
        currentQuiz = null
    }
}