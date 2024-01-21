package ru.sad.base.error


import android.content.res.Resources
import android.util.Log
import ru.sad.base.R
import ru.sad.data.exceptions.QuestionaryException
import java.lang.IllegalStateException
import java.lang.NullPointerException
import javax.inject.Inject

class HandlerError @Inject constructor(private val resources: Resources) : ErrorImpl {
    companion object {
        private const val ERROR_TAG = "QUESTIONARY-EXCEPTION"
    }

    override fun getError(throwable: Throwable): String = when (throwable) {
        is NullPointerException -> {
            resources.getString(R.string.error_null_pointer)
        }

        is IllegalStateException -> {
            resources.getString(R.string.error_illegal)
        }

        is QuestionaryException -> {
            if (throwable.message.contains("connect")) resources.getString(R.string.error_unknown)
            else throwable.message
        }

        else -> {
            resources.getString(R.string.error_unknown)
        }
    }
}