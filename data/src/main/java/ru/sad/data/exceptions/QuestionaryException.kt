package ru.sad.data.exceptions

class QuestionaryException(private val messageForUser: String) : Exception() {
    override val message: String = messageForUser

    override fun getLocalizedMessage(): String {
        return messageForUser
    }
}