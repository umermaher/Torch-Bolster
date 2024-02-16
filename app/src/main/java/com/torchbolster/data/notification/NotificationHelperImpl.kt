package com.torchbolster.data.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.torchbolster.R
import com.torchbolster.domain.notification.NotificationHelper
import com.torchbolster.utils.sendNotification

/**
 * Helper class used to send app wide notifications.
 */
class NotificationHelperImpl(
    private val context: Context,
    private val notificationManager: NotificationManager,
): NotificationHelper {

    override fun sendNotification(
        channelId:String,
        notificationId:Int,
        title:String,
        msgBody:String,
        description:String,
        contentIntent: Intent
    ){
        notificationManager.sendNotification(
            context,
            channelId,
            notificationId,
            title,
            msgBody,
            description,
            contentIntent
        )
    }

    override fun getNotificationBuilder(
        channelId: String,
        notificationId: Int,
        title: String,
        msgBody: String,
        contentIntent: Intent?,
        alertOnlyOnce: Boolean,
        autoCancel: Boolean,
        onGoing: Boolean
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(msgBody)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(autoCancel)
            .setOnlyAlertOnce(alertOnlyOnce)
            .setOngoing(onGoing)

        contentIntent?.let {
            val activityPendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            builder.setContentIntent(activityPendingIntent)
        }
        return builder
    }

    override fun updateNotificationContent(
        notificationBuilder: NotificationCompat.Builder?,
        notificationId: Int,
        content: String?
    ) {
        notificationBuilder?.apply {
            content?.let {
                setContentText(content)
            }
            setSmallIcon(R.drawable.ic_notification)
            notificationManager.notify(notificationId, build())
        }
    }

    override fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }
}