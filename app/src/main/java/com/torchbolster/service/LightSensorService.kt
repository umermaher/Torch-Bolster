package com.torchbolster.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.torchbolster.R
import com.torchbolster.domain.notification.NotificationHelper
import com.torchbolster.domain.sensor.MeasurableSensor
import com.torchbolster.domain.torch.FlashLightManager
import com.torchbolster.utils.LIGHT_SENSOR_NOTIFICATION_ID
import com.torchbolster.utils.TORCH_UPDATE_INTERVAL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LightSensorService: Service() {

    @Inject
    lateinit var lightSensor: MeasurableSensor

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var flashLightManager: FlashLightManager

    private var isPreviousLightStateDark: Boolean? = null
    private var lux: Float? = null

    private var monitorJob: Job? = null


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ServiceActions.START.toString() -> start()
            ServiceActions.STOP.toString() -> {
                lightSensor.stopListening()
                monitorJob?.cancel()
                flashLightManager.disableFlashLight()
                flashLightManager.unRegisterTorchCallback()
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        lightSensor.startListening()
        flashLightManager.registerTorchCallback()

        lightSensor.onSensorValueChangedListener { values ->
            lux = values[0]
            val isDarkOutside = lux!! < 5f

            if(isPreviousLightStateDark == null) {
                updateNotification(isDarkOutside)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(
                        LIGHT_SENSOR_NOTIFICATION_ID,
                        notificationBuilder.build(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                    )
                } else {
                    startForeground(
                        LIGHT_SENSOR_NOTIFICATION_ID,
                        notificationBuilder.build()
                    )
                }
            } else updateNotification(isDarkOutside)
        }

        monitorJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val startTime = System.currentTimeMillis()
                while ((lux ?: 0f) <= 2f &&
                    System.currentTimeMillis() - startTime < TORCH_UPDATE_INTERVAL
                ) {
                    // Value is 0, waiting...
                }
                if ((lux ?: 0f) <= 2f) {
                    // Perform further action when the value is 0 for 10 seconds
                    flashLightManager.enableFlashLight()
                } else
                    flashLightManager.disableFlashLight()
                delay(1000L)
                Log.i("Loop", "Looping")
            }
        }
    }

    private fun updateNotification(isDarkOutside: Boolean) {
        if(isPreviousLightStateDark != isDarkOutside) {
            isPreviousLightStateDark = isDarkOutside
            notificationHelper.updateNotificationContent(
                notificationBuilder,
                LIGHT_SENSOR_NOTIFICATION_ID,
                if (isDarkOutside) {
                    getString(R.string.its_dark_outside)
                } else getString(R.string.its_bright_outside)
            )
        }
    }
}