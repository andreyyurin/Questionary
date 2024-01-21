package ru.sad.data.repository.push

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sad.data.R
import ru.sad.data.api.QuestionaryApi
import ru.sad.data.extensions.withContextSingle
import ru.sad.domain.model.users.FirebaseRequest
import ru.sad.domain.model.users.FirebaseTokenResponse
import javax.inject.Inject

class PushRepository @Inject constructor(
    private val application: Application,
    private val api: QuestionaryApi
) : PushRepositoryImpl {

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    private fun handleError(msg: Throwable) {
        Log.e(PUSH_SERVICE, msg.stackTraceToString())
    }

    override fun updateToken(token: String) {
        CoroutineScope(Dispatchers.IO + errorHandler).launch {
            val result: FirebaseTokenResponse = withContextSingle {
                api.sendFirebaseToken(FirebaseRequest(token))
            }
        }
    }

    override fun buildNotification(
        title: String,
        message: String,
        data: Map<String, Any>,
        channelId: String,
        icon: Int,
        appName: String,
        activity: Class<out AppCompatActivity>
    ) {
        if (title.isEmpty() || message.isEmpty()) return

        val builder = NotificationCompat.Builder(application, channelId)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText(message)
            )
            .setSmallIcon(icon)
            .setColor(Color.parseColor("#FFB74D"))
            .setContentText(message)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)

        if (data["image"].toString().isEmpty().not()) {
            val bigImage = Glide.with(application)
                .asBitmap()
                .load(data["image"])
                .submit()

            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(
                        bigImage.get()
                    )
            )
        }

        sendNotification(data, builder, channelId, appName, activity)
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(
        data: Map<String, Any?> = mapOf(),
        builder: NotificationCompat.Builder,
        channelId: String,
        appName: String,
        activity: Class<out AppCompatActivity>
    ) {
        val intent = Intent(application, activity)
        val keys = ArrayList<String>()
        val values = ArrayList<String>()

        for ((key, value) in data) {
            keys.add(key)
            values.add(value.toString())
        }

        intent.putStringArrayListExtra("keys", keys)
        intent.putStringArrayListExtra("values", values)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val requestId = data["messageId"]?.hashCode() ?: 0

        val pendingIntent = PendingIntent.getActivity(
            application, requestId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )

        builder.setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(application)

        val channel = NotificationChannel(
            channelId,
            appName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.setShowBadge(true)
        if (notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(requestId, builder.build())
    }

    companion object {
        private const val PUSH_SERVICE = "LIFESTORY_PUSH_SERVICE"
    }
}