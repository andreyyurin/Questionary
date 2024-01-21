package ru.sad.base.navigation

import android.os.Bundle
import androidx.core.os.bundleOf

data class NavigationParams(
    val key: NavigationKey = NavigationKey.SPLASH_SCREEN,
    val bundles: Bundle = bundleOf()
)