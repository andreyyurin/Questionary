package ru.sad.quiz

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.ext.error
import ru.sad.base.ext.loading
import ru.sad.base.ext.postSuccess
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.repository.quiz.QuizRepositoryImpl
import ru.sad.domain.model.quiz.CheckResultsResponse
import ru.sad.domain.model.quiz.QuizResponse
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepositoryImpl
) : BaseFragmentViewModel() {

    companion object {
        private const val QUIZ_RESULT = "QUIZ_RESULT"
        private const val QUIZ_ID = "QUIZ_ID"
    }

    val quizLive = MutableLiveData<State<QuizResponse>>()
    val checkResultsLive = MutableLiveData<State<CheckResultsResponse>>()

    var currentQuiz: QuizResponse? = null

    fun loadQuiz(quizId: Int) {
        launchIO({
            quizLive.error(it)
        }) {
            quizLive.loading()
            val result = quizRepository.getQuiz(quizId)
            currentQuiz = result
            quizLive.postSuccess(currentQuiz!!)
        }
    }

    fun checkResults() {
        launchIO({
            checkResultsLive.error(it)
        }) {
            checkResultsLive.loading()
            currentQuiz?.let {
                checkResultsLive.postSuccess(quizRepository.checkResults(it))
            }
        }
    }

    fun openResultScreen(result: CheckResultsResponse, quizId: Long) {
        navigate(
            NavigationKey.QUIZ_RESULT_SCREEN, bundles = bundleOf(
                QUIZ_RESULT to result,
                QUIZ_ID to quizId
            )
        )
    }

    fun clear() {
        onCleared()
        currentQuiz = null
    }
}