package ru.sad.base.ext

import android.content.SharedPreferences

fun SharedPreferences.putString(key: String, param: String) {
    with(edit()) {
        putString(key, param)
        apply()
    }
}

fun SharedPreferences.putBoolean(key: String, param: Boolean) {
    with(edit()) {
        putBoolean(key, param)
        apply()
    }
}

fun SharedPreferences.getString(key: String): String? {
    return getString(key, "")
}