package com.torchbolster.ui.home

import android.Manifest
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.torchbolster.domain.sensor.MeasurableSensor
import com.torchbolster.service.ServiceActions
import com.torchbolster.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.torchbolster.R

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val lightSensor: MeasurableSensor
): ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state get() = _state.asStateFlow()

//    val permissionDialogQueue = mutableStateListOf<String>()

    private val resultChannel = Channel<HomeResult>()
    val homeResults = resultChannel.receiveAsFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnPermissionResult -> onPermissionResult(
                event.permission, event.isGranted
            )

            HomeEvent.DismissPermissionDialog -> {
                _state.update {
                    val list = it.permissionDialogQueue.toMutableList()
                    list.removeFirstOrNull()
                    it.copy(permissionDialogQueue = list)
                }
            }

            is HomeEvent.OnToggleService -> viewModelScope.launch {
                if(!lightSensor.doesSensorExist) {
                    resultChannel.send( HomeResult.Message(
                        msg = UiText.StringResource(
                            R.string.sensor_does_not_exist,
                            emptyList()
                        )
                    ) )
                    return@launch
                }
                _state.update {
                    it.copy(isServiceRunning = event.shouldStartService)
                }
                resultChannel.send(HomeResult.ToggleLightSensorService(
                    action = if(event.shouldStartService) {
                        ServiceActions.START
                    } else ServiceActions.STOP
                ))
            }

            is HomeEvent.InitServiceState -> {
                _state.update {
                    it.copy(isServiceRunning = event.isRunning)
                }
            }
        }
    }

    private fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !state.value.permissionDialogQueue.contains(permission)) {
            _state.update {
                it.copy(permissionDialogQueue = it.permissionDialogQueue + permission)
            }
        } else if(isGranted && permission == Manifest.permission.POST_NOTIFICATIONS)
            viewModelScope.launch {
                resultChannel.send(
                    HomeResult.ToggleLightSensorService(action = ServiceActions.START)
                )
            }
    }
}