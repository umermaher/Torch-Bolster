package com.torchbolster.di

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import androidx.core.app.NotificationCompat
import com.torchbolster.data.torch.AndroidFlashLightManager
import com.torchbolster.data.sensor.LightSensor
import com.torchbolster.domain.sensor.MeasurableSensor
import com.torchbolster.domain.torch.FlashLightManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import com.torchbolster.R
import com.torchbolster.domain.notification.NotificationHelper
import com.torchbolster.ui.MainActivity

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideTorchUseCase(
        cameraManager: CameraManager
    ): FlashLightManager = AndroidFlashLightManager(
        cameraManager = cameraManager
    )

    @Provides
    @ServiceScoped
    fun provideLightSensorNotificationBuilder(
        @ApplicationContext context: Context,
        notificationHelper: NotificationHelper
    ): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java)
        return notificationHelper.getNotificationBuilder(
            context.getString(R.string.light_notification_channel_id),
            com.torchbolster.utils.LIGHT_SENSOR_NOTIFICATION_ID,
            title = context.getString(R.string.app_name),
            msgBody = "-",
            contentIntent = intent,
            alertOnlyOnce = true,
            autoCancel = false,
            onGoing = true
        )
    }
}