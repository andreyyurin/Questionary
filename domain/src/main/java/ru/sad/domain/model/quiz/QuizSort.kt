package ru.sad.domain.model.quiz

import java.io.Serializable

data class QuizSort(
    val name: String,
    val type: Int
): Serializable