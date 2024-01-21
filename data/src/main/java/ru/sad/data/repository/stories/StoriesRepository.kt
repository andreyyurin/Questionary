package ru.sad.data.repository.stories

import android.app.Application
import android.net.Uri
import androidx.core.net.toFile
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.sad.data.R
import ru.sad.data.api.QuestionaryApi
import ru.sad.data.exceptions.QuestionaryException
import ru.sad.data.extensions.withContext
import ru.sad.data.extensions.withContextSingle
import ru.sad.domain.model.stories.CreateStoryResponse
import ru.sad.domain.model.stories.StoriesResponse

class StoriesRepository(
    private val application: Application,
    private val api: QuestionaryApi
) : StoriesRepositoryImpl {

    override suspend fun getStories(): List<StoriesResponse> = withContext {
        api.getStories()
    }

    override suspend fun createStory(uri: Uri?, id: Int): CreateStoryResponse = withContextSingle {
        if (uri == null) throw QuestionaryException(application.getString(R.string.error_image))

        val file = uri.toFile()

        api.createStory(
            MultipartBody.Part.createFormData(
                name = "image",
                filename = file.name,
                body = file.asRequestBody()
            ),
            file.name,
            id.toString(),
        )
    }
}