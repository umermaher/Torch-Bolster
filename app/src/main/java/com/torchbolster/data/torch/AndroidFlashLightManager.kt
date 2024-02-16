package com.torchbolster.data.torch

import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.util.Log
import com.torchbolster.domain.torch.FlashLightManager

class AndroidFlashLightManager(
    private val cameraManager: CameraManager
): FlashLightManager {

    private var cameraId: String ?= null
    override var isFlashLightOn = false

    private val torchCallback = object: CameraManager.TorchCallback(){
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            super.onTorchModeChanged(cameraId, enabled)
            isFlashLightOn = enabled
            Log.i("isFlashLightOn", isFlashLightOn.toString())
        }
    }

    init {
        try{
            // O means back camera unit,
            // 1 means front camera unit
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            Log.e("Camera Id error", e.message.toString())
        }
    }

    override fun registerTorchCallback() = cameraManager.registerTorchCallback(torchCallback, null)

    override fun unRegisterTorchCallback() {
        cameraManager.unregisterTorchCallback(torchCallback)
    }

    override fun enableFlashLight() {
        try {
            if (!isFlashLightOn) {
                cameraManager.setTorchMode(cameraId!!, true);
            }
        } catch (e: Exception) {
            Log.e("Camera error", e.message.toString())
        }
    }

    override fun disableFlashLight() {
        try {
            if (isFlashLightOn) {
                cameraManager.setTorchMode(cameraId!!, false);
            }
        } catch (e: Exception) {
            Log.e("Camera error", e.message.toString())
        }
    }
}