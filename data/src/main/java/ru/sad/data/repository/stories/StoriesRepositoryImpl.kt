package ru.sad.data.repository.stories

import android.net.Uri
import ru.sad.domain.model.stories.CreateStoryResponse
import ru.sad.domain.model.stories.StoriesResponse
import ru.sad.domain.model.users.UploadPhotoResponse

interface StoriesRepositoryImpl {
    suspend fun getStories(): List<StoriesResponse>

    suspend fun createStory(uri: Uri?, id: Int): CreateStoryResponse
}