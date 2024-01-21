package ru.sad.domain.model.quiz

import java.io.Serializable

data class CheckResultsResponse(
    val totalQuestions: Int? = null,
    val trueQuestions: Int? = null
): Serializable