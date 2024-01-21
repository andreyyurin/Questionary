package ru.sad.data.api

import okhttp3.OkHttpClient

interface OkHttpBuildersImpl {
    val mainHttpBuilder: OkHttpClient.Builder
}