package ru.sad.domain.model.quiz

import java.io.Serializable

data class QuizShortResponse(
    val rating: Float,
    val category: String,
    val title: String,
    val image: String,
    val id: Int,
    val authorName: String? = null
): Serializable