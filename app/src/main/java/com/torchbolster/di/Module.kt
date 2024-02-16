package com.torchbolster.di

import android.app.NotificationManager
import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.core.content.ContextCompat
import com.torchbolster.data.notification.NotificationHelperImpl
import com.torchbolster.data.sensor.LightSensor
import com.torchbolster.data.torch.AndroidFlashLightManager
import com.torchbolster.domain.notification.NotificationHelper
import com.torchbolster.domain.sensor.MeasurableSensor
import com.torchbolster.domain.torch.FlashLightManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideCameraManager(
        @ApplicationContext context: Context,
    ): CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    @Provides
    @Singleton
    fun notificationManager(@ApplicationContext context: Context): NotificationManager =
        ContextCompat.getSystemService(
            context, NotificationManager::class.java
        ) as NotificationManager

    @Provides
    @Singleton
    fun notificationHelper(
        @ApplicationContext context: Context,
        notificationManager: NotificationManager
    ): NotificationHelper = NotificationHelperImpl(context, notificationManager)

    @Provides
    @Singleton
    fun provideLightSensor(@ApplicationContext context: Context): MeasurableSensor = LightSensor(context)


}