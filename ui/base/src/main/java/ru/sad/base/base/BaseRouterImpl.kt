package ru.sad.base.base

import android.os.Bundle
import ru.sad.base.navigation.NavigationKey

interface BaseRouterImpl {

    val enterAnimation: Int

    val exitAnimation: Int

    fun navigate(key: NavigationKey, bundles: Bundle)
}