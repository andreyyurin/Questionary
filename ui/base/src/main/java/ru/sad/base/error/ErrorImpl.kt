package ru.sad.base.error

interface ErrorImpl {
    fun getError(throwable: Throwable): String
}