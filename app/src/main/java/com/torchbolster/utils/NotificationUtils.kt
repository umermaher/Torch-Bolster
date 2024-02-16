package com.torchbolster.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.torchbolster.R

fun Application.createNotificationChannel(
    channelId:String,
    channelName:String,
    channelDescription:String,
    importance: Int = NotificationManager.IMPORTANCE_HIGH
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            importance
        )

        channel.description = channelDescription
        channel.enableLights(true)
        channel.lightColor = Color.RED



        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

/**
 * notification utils
 * */
fun NotificationManager.sendNotification(
    applicationContext: Context,
    channelId: String,
    notificationId: Int,
    title: String,
    messageBody: String,
    description: String = "",
    contentIntent: Intent
){

    val activityPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationId,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )

    val notificationBuilder = NotificationCompat.Builder(applicationContext,channelId)
    notificationBuilder
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setContentIntent(activityPendingIntent)
        .setAutoCancel(true)
        .priority = NotificationCompat.PRIORITY_HIGH

//    if (description.isNotEmpty()) {
//        val notificationStyle = NotificationCompat.BigTextStyle()
//        notificationStyle.bigText(description)
//        notificationStyle.setSummaryText(messageBody)
//
//        notificationBuilder.setStyle(notificationStyle)
//    }

    notify(notificationId, notificationBuilder.build())
}