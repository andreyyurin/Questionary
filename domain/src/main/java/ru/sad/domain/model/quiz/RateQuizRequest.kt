package ru.sad.domain.model.quiz

import java.io.Serializable

data class RateQuizRequest(val quizId: Long, val stars: Int): Serializable