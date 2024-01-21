package ru.sad.base.ext

import android.graphics.Color

fun String.getSpanned(color: String) = "<font color=$color>$this</font>";
