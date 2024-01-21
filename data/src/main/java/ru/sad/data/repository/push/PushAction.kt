package ru.sad.data.repository.push

import java.io.Serializable

enum class PushAction(val action: String): Serializable {
    EMPTY("empty"),
    STORY_IMAGE("story.image")
}