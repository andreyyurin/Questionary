package ru.sad.base.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

sealed class State<out T> {
    data class Success<out T>(val value: T) : State<T>()

    data class Error<out T>(val message: String?, val data: T? = null) : State<T>()

    data class Loading<out T>(val data: T? = null) : State<T>()
}

inline fun <T> State<T>.onSuccess(
    crossinline f: T.() -> Unit
) {
    runOnUiThread {
        if (this is State.Success) {
            f(this.value)
        }
        this
    }
}

inline fun <T> State<T>.onLoading(crossinline f: T?.() -> Unit = {}) =
    runOnUiThread {
        if (this is State.Loading)
            f(this.data)
        this
    }

inline fun <T> State<T>.onError(
    crossinline f: T?.() -> Unit = {},
    crossinline error: String?.() -> Unit
) =
    runOnUiThread {
        if (this is State.Error) {
            f(this.data)
            error(this.message)
        }
        this
    }

inline fun <T> State<T>.onError(crossinline f: String?.() -> Unit) =
    runOnUiThread {
        if (this is State.Error)
            f(this.message)
        this
    }

inline fun <T> MutableLiveData<State<T>>.loading(data: T? = null) {
    this.postValue(State.Loading(data))
}

inline fun <T> MutableLiveData<State<T>>.error(e: Exception) {
    this.postValue(State.Error(e.localizedMessage))
}

inline fun <T> MutableLiveData<State<T>>.error(e: String) {
    this.postValue(State.Error(e))
}

inline fun <T> MutableLiveData<State<T>>.postSuccess(value: T) {
    this.postValue(State.Success(value))
}

fun <T : Any?> MutableLiveData<T>.observeAndClear(owner: LifecycleOwner, observer: Observer<T>) {
    val o = Observer<T> { t ->
        if (t != null) {
            observer.onChanged(t)
            postValue(null)
        }
    }
    observe(owner, o)
}


inline fun runOnUiThread(crossinline f: () -> Unit) = MainScope().launch {
    f()
}
