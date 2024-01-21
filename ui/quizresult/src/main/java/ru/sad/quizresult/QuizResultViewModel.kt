package ru.sad.quizresult

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.ext.error
import ru.sad.base.ext.loading
import ru.sad.base.ext.postSuccess
import ru.sad.data.repository.quiz.QuizRepository
import ru.sad.data.repository.quiz.QuizRepositoryImpl
import ru.sad.domain.model.quiz.RateQuizRequest
import ru.sad.domain.model.quiz.RateQuizResponse
import java.lang.System.exit
import javax.inject.Inject

@HiltViewModel
class QuizResultViewModel @Inject constructor(
    private val quizRepository: QuizRepositoryImpl
) : BaseFragmentViewModel() {

    val rateQuizLive = MutableLiveData<State<RateQuizResponse>>()

    fun sendRate(quizId: Long, stars: Int) {
        launchIO({
            rateQuizLive.error(it)
            back()
        }) {
            rateQuizLive.loading()
            rateQuizLive.postSuccess(quizRepository.sendRate(quizId, stars))
            back()
        }
    }
}