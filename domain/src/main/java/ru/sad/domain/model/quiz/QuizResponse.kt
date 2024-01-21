package ru.sad.domain.model.quiz

import java.io.Serializable

data class QuizResponse(
    var title: String,
    val id: Int = -1,
    val questions: ArrayList<QuizQuestion>,
    var category: Int,
    var country: String
) : Serializable

data class QuizQuestion(
    var question: String,
    val answers: ArrayList<QuizAnswer>
) : Serializable

data class QuizAnswer(
    var isTrue: Boolean,
    var answer: String
): Serializable