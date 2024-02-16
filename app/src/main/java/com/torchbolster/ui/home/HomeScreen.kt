package com.torchbolster.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import com.torchbolster.R
import com.torchbolster.ui.MainActivity
import com.torchbolster.utils.UiText
import com.torchbolster.utils.compose.NotificationPermissionTextProvider
import com.torchbolster.utils.compose.ObserveAsEvents
import com.torchbolster.utils.compose.PermissionDialog
import com.torchbolster.utils.hasPermission
import com.torchbolster.utils.isLightSensorServiceRunning
import com.torchbolster.utils.openAppSettings
import com.torchbolster.utils.showToast
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    homeResults: Flow<HomeResult>,
) {

    val activity = LocalContext.current as MainActivity
    val lifecycleOwner = LocalLifecycleOwner.current

    val notificationPermissionToRequest = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else null
    }

    // notification
    val notificationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            notificationPermissionToRequest?.let {
                onEvent(
                    HomeEvent.OnPermissionResult(
                        notificationPermissionToRequest, isGranted
                    )
                )
            }
        }
    )

    LaunchedEffect(key1 = true) {
        onEvent( HomeEvent.InitServiceState(
            isRunning = activity.isLightSensorServiceRunning()
        ) )
    }

    ObserveAsEvents(flow = homeResults) { res ->
        when (res) {
            is HomeResult.ToggleLightSensorService -> {
                activity.toggleLightSensorService(res.action)
            }
            is HomeResult.Message -> when (res.msg) {
                is UiText.DynamicString -> activity.showToast(res.msg.value)
                is UiText.StringResource -> activity.showToast(
                    activity.getString(res.msg.value)
                )
            }
        }
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                    )
                },
            )
        }
    ) { values ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(values),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnBoardMessage(
                imgRes = R.drawable.ic_torch,
                titleRes = R.string.torch_support_title_msg,
                msgRes = R.string.torch_support_msg
            )

            Switch(
                checked = state.isServiceRunning,
                onCheckedChange = {
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                                && !activity.hasPermission(Manifest.permission.POST_NOTIFICATIONS))
                    ) {
                        notificationPermissionResultLauncher.launch(notificationPermissionToRequest)
                    } else {

                        onEvent(HomeEvent.OnToggleService(it))
                    }
                },
            )
        }
    }

    state.permissionDialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {

                    Manifest.permission.POST_NOTIFICATIONS ->
                        NotificationPermissionTextProvider()

                    else -> return@forEach
                },
                isPermanentlyDeclined = !activity.shouldShowRequestPermissionRationale(
                    permission
                ),
                onDismiss = {
                    onEvent(HomeEvent.DismissPermissionDialog)
                },
                onOkClick = {
                    onEvent(HomeEvent.DismissPermissionDialog)
                    when(permission) {
                        notificationPermissionToRequest ->
                            notificationPermissionResultLauncher.launch(notificationPermissionToRequest)
                    }
                },
                onGoToAppSettingsClick = activity::openAppSettings
            )
        }
}