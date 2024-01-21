package ru.sad.domain.model.stories

import ru.sad.domain.model.users.User
import java.io.Serializable

data class StoriesResponse(
    val author: User,
    val imageUrl: String,
    val date: String
) : Serializable