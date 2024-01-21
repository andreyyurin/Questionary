package ru.sad.data.repository.push

import androidx.appcompat.app.AppCompatActivity

interface PushRepositoryImpl {
    fun updateToken(token: String)

    fun buildNotification(
        title: String,
        message: String,
        data: Map<String, Any>,
        channelId: String,
        icon: Int,
        appName: String,
        activity: Class<out AppCompatActivity>
    )
}