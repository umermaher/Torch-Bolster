package com.torchbolster

import android.app.Application
import com.torchbolster.utils.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(
            getString(R.string.light_notification_channel_id),
            getString(R.string.light_notification_channel_name),
            getString(R.string.light_notification_channel_desc)
        )
    }
}