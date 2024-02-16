package com.torchbolster.domain.sensor

abstract class MeasurableSensor (
    protected val sensorType: Int
) {

    protected var onSensorValueChange: ((List<Float>) -> Unit ) ?= null

    abstract val doesSensorExist: Boolean

    abstract fun startListening()
    abstract fun stopListening()

    fun onSensorValueChangedListener(listener: (List<Float>) -> Unit) {
        onSensorValueChange = listener
    }
}