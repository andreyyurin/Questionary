package ru.sad.domain.base

import android.telecom.Call


data class BaseModel<T>(val data: List<T>, val success: Boolean, val message: String? = null)

data class BaseModelSingle<T>(val data: T, val success: Boolean, val message: String? = null)