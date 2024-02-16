package com.torchbolster.domain.torch

interface FlashLightManager {
    var isFlashLightOn: Boolean
    fun registerTorchCallback()
    fun unRegisterTorchCallback()
    fun enableFlashLight()
    fun disableFlashLight()
}