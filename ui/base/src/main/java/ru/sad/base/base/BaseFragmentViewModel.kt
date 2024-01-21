package ru.sad.base.base

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.sad.base.error.ErrorResult
import ru.sad.base.error.HandlerError
import ru.sad.base.navigation.MainNavigation
import ru.sad.base.navigation.NavigationKey
import ru.sad.base.navigation.NavigationParams
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

open class BaseFragmentViewModel : ViewModel(), CoroutineScope {

    companion object {
        private const val EXCEPTION_FAILED_CONNECT = "failed to connect"
        private const val EXCEPTION_UNKNOWN = "An unknown error occurred. Please try again later."
    }

    @Inject
    lateinit var navigation: MainNavigation

    @Inject
    lateinit var handlerError: HandlerError

    private val scopeJob: Job = SupervisorJob()

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    override val coroutineContext: CoroutineContext = scopeJob + Dispatchers.Main + errorHandler

    private fun handleError(throwable: Throwable) {
        ErrorResult.postValue(handlerError.getError(throwable))
    }

    fun handleError(error: String) {
        ErrorResult.postValue(error)
    }

    open fun CoroutineScope.launchIO(
        exception: suspend (String) -> Unit = {},
        r: suspend () -> Unit
    ) {
        launch(Dispatchers.IO) {
            try {
                r.invoke()
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    if (!it.contains(EXCEPTION_FAILED_CONNECT)) {
                        exception.invoke(it)
                    } else {
                        exception.invoke(EXCEPTION_UNKNOWN)
                    }
                }
            }
        }
    }

    open fun CoroutineScope.launchMain(
        exception: suspend (String) -> Unit = {},
        r: suspend () -> Unit
    ) {
        launch(Dispatchers.Main) {
            try {
                r.invoke()
            } catch (e: Exception) {
                e.localizedMessage?.let { exception.invoke(it) }
            }
        }
    }


    open fun CoroutineScope.launchPeriodicAsync(repeatMillis: Long, action: suspend () -> Unit) {
        launch(Dispatchers.IO) {
            while (isActive) {
                action.invoke()
                delay(repeatMillis)
            }
        }
    }

    open fun navigate(key: NavigationKey, bundles: Bundle = bundleOf()) {
        navigation.postValue(NavigationParams(key, bundles))
    }

    open fun back(bundles: Bundle = bundleOf()) {
        navigation.postValue(NavigationParams(NavigationKey.EXIT, bundles))
    }

    open fun CoroutineScope.launchMain(r: suspend () -> Unit) {
        launch(Dispatchers.Main) {
            r.invoke()
        }
    }


}