package ru.sad.data.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sad.data.exceptions.QuestionaryException
import ru.sad.domain.base.BaseModel
import ru.sad.domain.base.BaseModelSingle

suspend fun <T> withContextSingle(
    block: suspend CoroutineScope.() -> BaseModelSingle<T>
): T {
    val result = withContext(Dispatchers.IO, block)

    if ((result as? BaseModelSingle<*>)?.success != true) {
        throw QuestionaryException(result.message ?: "Failed to retrieve data")
    }

    return result.data
}

suspend fun <T> withContext(
    block: suspend CoroutineScope.() -> BaseModel<T>
): List<T> {
    val result = withContext(Dispatchers.IO, block)

    if ((result as? BaseModel<*>)?.success != true) {
        throw QuestionaryException(result.message ?: "Failed to retrieve data")
    }

    return result.data
}