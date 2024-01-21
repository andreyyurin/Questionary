package ru.sad.data.repository.quiz

import android.app.Application
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.sad.data.R
import ru.sad.data.api.QuestionaryApi
import ru.sad.data.exceptions.QuestionaryException
import ru.sad.data.extensions.withContext
import ru.sad.data.extensions.withContextSingle
import ru.sad.domain.model.quiz.CheckResultsResponse
import ru.sad.domain.model.quiz.CreateQuizResponse
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.domain.model.quiz.QuizCountry
import ru.sad.domain.model.quiz.QuizResponse
import ru.sad.domain.model.quiz.QuizShortResponse
import ru.sad.domain.model.quiz.QuizSort
import ru.sad.domain.model.quiz.RateQuizRequest
import ru.sad.domain.model.quiz.RateQuizResponse


class QuizRepository(
    private val application: Application,
    private val api: QuestionaryApi
) : QuizRepositoryImpl {

    private val categoriesLive = MutableLiveData<List<QuizCategory>>()
    private val sortsLive = MutableLiveData<List<QuizSort>>()
    private val countriesLive = MutableLiveData<List<QuizCountry>>()

    init {
        suspendLoad()
    }

    override suspend fun checkResults(quizResponse: QuizResponse): CheckResultsResponse =
        withContextSingle {
            api.checkResults(quizResponse)
        }

    override suspend fun createQuiz(quizResponse: QuizResponse, uri: Uri?): CreateQuizResponse =
        withContextSingle {
            if (uri == null) throw QuestionaryException(application.getString(R.string.error_image))

            val file = uri.toFile()

            val gson = GsonBuilder().setLenient().create()

            val data =
                RequestBody.create("application/json".toMediaType(), gson.toJson(quizResponse))

            api.createQuiz(
                data,
                MultipartBody.Part.createFormData(
                    name = "image",
                    filename = file.name,
                    body = file.asRequestBody()
                ),
            )
        }


    override suspend fun getQuiz(id: Int): QuizResponse = withContextSingle {
        api.getQuiz(id)
    }

    override suspend fun getTopQuizes(
        page: Int,
        pageSize: Int,
        category: Int,
        sort: QuizSort,
        country: String
    ): List<QuizShortResponse> = withContext {
        api.getTopQuizes(page, pageSize, category, sort.type, country)
    }

    override suspend fun getFilteredQuizes(
        category: Int,
        sort: QuizSort
    ): List<QuizShortResponse> = withContext {
        api.getFiltered(category, sort.type)
    }

    override suspend fun getQuizesByUser(
        id: Int,
        page: Int,
        pageSize: Int
    ): List<QuizShortResponse> = withContext {
        api.getQuizByUser(id, page, pageSize)
    }

    override suspend fun getCategories(): List<QuizCategory> =
        categoriesLive.value ?: loadCategories()

    override suspend fun getCountries(): List<QuizCountry> =
        countriesLive.value ?: loadCountries()

    override suspend fun getSorts(): List<QuizSort> =
        sortsLive.value ?: loadSorts()

    override suspend fun removeQuiz(id: Int) = withContextSingle { api.removeQuiz(id) }

    override suspend fun sendRate(quizId: Long, stars: Int): RateQuizResponse = withContextSingle {
        api.rateQuiz(RateQuizRequest(quizId, stars))
    }

    private suspend fun loadCategories(): List<QuizCategory> {
        val result = withContext {
            api.getCategories()
        }

        categoriesLive.postValue(result)
        return result
    }

    private suspend fun loadCountries(): List<QuizCountry> {
        val result = withContext {
            api.getCountries()
        }.sortedBy { it.name }

        countriesLive.postValue(result)
        return result
    }

    private fun suspendLoad() {
        CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->

        }).launch {
            loadCategories()
            loadSorts()
            loadCountries()
        }
    }

    private suspend fun loadSorts(): List<QuizSort> {
        val result = withContext {
            api.getSorts()
        }

        sortsLive.postValue(result)
        return result
    }
}