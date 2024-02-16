package com.torchbolster.ui.home

import androidx.compose.runtime.mutableStateListOf
import com.torchbolster.service.ServiceActions
import com.torchbolster.utils.UiText

data class HomeState(
    val isServiceRunning: Boolean = false,
    val permissionDialogQueue: List<String> = emptyList()
)

sealed interface HomeEvent {
    data class OnPermissionResult(val permission: String, val isGranted: Boolean): HomeEvent
    data object DismissPermissionDialog: HomeEvent
    data class InitServiceState(val isRunning: Boolean): HomeEvent
    data class OnToggleService(val shouldStartService: Boolean): HomeEvent
}

sealed class HomeResult {
    data class ToggleLightSensorService(val action: ServiceActions): HomeResult()
    data class Message(val msg: UiText): HomeResult()
}