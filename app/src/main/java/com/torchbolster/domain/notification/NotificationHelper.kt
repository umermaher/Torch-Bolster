package com.torchbolster.domain.notification

import android.content.Intent
import androidx.core.app.NotificationCompat

interface NotificationHelper {

    /**
     * Helper method to send default notification with some custom options
     * @param channelId         channel id
     * @param notificationId    notification id
     * @param title             notification title
     * @param msgBody       message for notification
     * @param description       short description for notification
     * @param contentIntent     [Intent] of the target [Activity]
     */
    fun sendNotification(
        channelId:String,
        notificationId:Int,
        title:String,
        msgBody:String,
        description:String,
        contentIntent: Intent,
//        onGoing: Boolean = false
    )

    /**
     *
     * Cancel a previously shown notification.  If it's transient, the view
     * will be hidden.  If it's persistent, it will be removed from the status
     * bar.
     *
     * @param id notification id
     */
    fun cancelNotification(id: Int)

    /**
     * Helper method to update notification with some custom options
     * @param notificationBuilder       notification Builder
     * @param notificationId            notification id
     * @param content                   message for notification
     */
    fun updateNotificationContent(
        notificationBuilder: NotificationCompat.Builder?,
        notificationId: Int,
        content: String? = null
    )

    /**
     * Helper method to get default notification with some custom options
     * @param notificationBuilder       notification Builder
     * @param notificationId    notification id
     * @param title             notification title
     * @param messageBody       message for notification
     * @param description       short description for notification
     * @param contentIntent     [Intent] of the target [Activity]
     */
    fun getNotificationBuilder(
        channelId:String,
        notificationId: Int,
        title:String,
        msgBody:String,
        contentIntent: Intent?= null,
        alertOnlyOnce: Boolean = false,
        autoCancel: Boolean = true,
        onGoing: Boolean = false
    ): NotificationCompat.Builder

}