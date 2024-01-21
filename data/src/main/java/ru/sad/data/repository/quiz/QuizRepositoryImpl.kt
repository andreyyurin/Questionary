package ru.sad.data.repository.quiz

import android.net.Uri
import ru.sad.domain.model.quiz.CheckResultsResponse
import ru.sad.domain.model.quiz.CreateQuizResponse
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.domain.model.quiz.QuizCountry
import ru.sad.domain.model.quiz.QuizResponse
import ru.sad.domain.model.quiz.QuizShortResponse
import ru.sad.domain.model.quiz.QuizSort
import ru.sad.domain.model.quiz.RateQuizResponse
import ru.sad.domain.model.quiz.RemoveQuizResponse

interface QuizRepositoryImpl {
    suspend fun createQuiz(quizResponse: QuizResponse, uri: Uri?): CreateQuizResponse

    suspend fun checkResults(quizResponse: QuizResponse): CheckResultsResponse

    suspend fun getQuiz(id: Int): QuizResponse

    suspend fun getQuizesByUser(
        id: Int,
        page: Int,
        pageSize: Int,
    ): List<QuizShortResponse>

    suspend fun getTopQuizes(
        page: Int,
        pageSize: Int,
        category: Int,
        sort: QuizSort,
        country: String
    ): List<QuizShortResponse>

    suspend fun getCategories(): List<QuizCategory>

    suspend fun getSorts(): List<QuizSort>

    suspend fun getCountries(): List<QuizCountry>

    suspend fun removeQuiz(id: Int): RemoveQuizResponse

    suspend fun getFilteredQuizes(category: Int, sort: QuizSort): List<QuizShortResponse>

    suspend fun sendRate(quizId: Long, stars: Int): RateQuizResponse
}