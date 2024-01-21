package ru.sad.domain.model.quiz

import java.io.Serializable

data class QuizCountry(
    val name: String,
    val image: String?,
    val id: String
): Serializable