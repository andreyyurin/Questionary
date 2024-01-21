package ru.sad.questionary.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sad.base.base.BaseActivity
import ru.sad.base.error.HandlerError
import ru.sad.data.repository.push.PushAction
import ru.sad.data.repository.push.PushRepository
import ru.sad.data.repository.push.PushRepositoryImpl
import ru.sad.data.repository.user.UserRepository
import ru.sad.data.repository.user.UserRepositoryImpl
import ru.sad.questionary.MainActivity
import ru.sad.questionary.R
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var pushRepository: PushRepositoryImpl

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        pushRepository.updateToken(token)
    }

    override fun handleIntent(intent: Intent?) {
        if (intent?.extras == null) super.handleIntent(intent)
        else {
            val remoteMessage = intent.extras?.let { RemoteMessage(it) }
            onMessageReceived(remoteMessage!!)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val imageUrlUri = remoteMessage.notification?.imageUrl

        data["click_action"] = remoteMessage.notification?.clickAction
        data["image"] = imageUrlUri?.toString() ?: ""
        data["messageId"] = remoteMessage.messageId

        pushRepository.buildNotification(
            remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "",
            remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "",
            data,
            application.getString(ru.sad.base.R.string.default_notification_channel_id),
            R.drawable.ic_push_notification,
            application.getString(ru.sad.base.R.string.app_name),
            MainActivity::class.java
        )
    }
}