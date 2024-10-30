package com.specure.manager.presentation.screens.manager_overview

import android.Manifest
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specure.core.domain.config.Config
import com.specure.core.domain.service.BluetoothService
import com.specure.core.presentation.ui.UiText
import com.specure.intercom.domain.client.BluetoothClientService
import com.specure.intercom.domain.client.DeviceType
import com.specure.intercom.domain.message.TrackerAction
import com.specure.manager.presentation.BuildConfig
import com.specure.manager.presentation.R
import com.specure.permissions.domain.PermissionHandler
import com.specure.permissions.presentation.appPermissions
import com.specure.updater.domain.Updater
import com.specure.updater.domain.UpdatingStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber


class ManagerOverviewViewModel(
    private val appContext: Context,
    private val appConfig: Config,
    private val applicationScope: CoroutineScope,
    private val bluetoothService: BluetoothClientService,
    private val androidBluetoothService: BluetoothService,
    private val permissionHandler: PermissionHandler,
    private val updater: Updater,
) : ViewModel() {

    var state by mutableStateOf(ManagerOverviewState())
        private set

    var latestReleasedVersionStatus = updater.latestReleasedVersionStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UpdatingStatus.Idle
    )

    val trackerRepoLink = BuildConfig.GITHUB_API_TRACKER_REPO_URL

    init {
        permissionHandler.setPermissionsNeeded(
            appPermissions
        )

        latestReleasedVersionStatus.onEach { updaterState ->
            if (updaterState is UpdatingStatus.NewVersionFound) {
                state = state.copy(
                    lastTrackerVersion = updaterState.version
                )
            }
        }.launchIn(viewModelScope)

        checkTrackerLatestVersion()

        manageBluetoothDevices()
    }

    private fun playAlertSound() {
        if (appConfig.isAlertSoundOnTestErrorEnabled()) {
            val alert: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone: Ringtone = RingtoneManager.getRingtone(appContext, alert)
            ringtone.play()
        }
    }

    private fun manageBluetoothDevices() {
        if (permissionHandler.isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT) && state.isBluetoothAdapterEnabled) {
            bluetoothService.observeConnectedDevices(DeviceType.TRACKER)
                .onEach { devices ->
                    state = state.copy(
                        managedDevices = devices.values.toList()
                    )
                }
                .launchIn(viewModelScope)

            bluetoothService.trackingDevices
                .onEach { devices ->

                    val previousState = state.copy()
                    val newErrorDevices = previousState.managedDevices.filter { prevDeviceState ->
                        devices.filter { currentDeviceState ->
                            currentDeviceState.value.isStateChangedOnTheSameDevice(prevDeviceState) && currentDeviceState.value.isErrorState()
                        }.isNotEmpty()
                    }
                    if (newErrorDevices.isNotEmpty()) {
                        playAlertSound()
                    }
                    val updateCheckDevices = devices.values.map { device ->
                        val latestVersion = updater.getLatestVersion(device.deviceAppVersion, state.lastTrackerVersion)
                        if (latestVersion != null && device.deviceAppVersion != latestVersion) {
                            device.copy(
                                deviceAppVersion = "${device.deviceAppVersion} (${UiText.StringResource(
                                    R.string.udpateAvailable).asString(appContext)})"
                            )
                        } else {
                            device
                        }

                    }
                    state = state.copy(
                        managedDevices = updateCheckDevices.toList()
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    fun onAction(action: ManagerOverviewAction) {
        when (action) {
            is ManagerOverviewAction.DeleteManager -> {
                TODO()
            }

            is ManagerOverviewAction.OnConnectClick -> {
                viewModelScope.launch {
                    val result = bluetoothService.connectToDevice(action.address)
                    Timber.d("Connect result: $result")
                }
            }

            is ManagerOverviewAction.OnStartClick -> {
                viewModelScope.launch {
                    bluetoothService.sendActionFlow.emit(TrackerAction.StartTest(action.address))
                }
            }

            is ManagerOverviewAction.OnStopClick -> {
                viewModelScope.launch {
                    bluetoothService.sendActionFlow.emit(TrackerAction.StopTest(action.address))
                }
            }

            ManagerOverviewAction.OnResolvePermissionClick -> {

            }
            ManagerOverviewAction.OnOpenBluetoothSettingsClick -> {
                androidBluetoothService.openBluetoothSettings()
            }
            ManagerOverviewAction.OnCheckTrackerLatestVersionClick -> {
                checkTrackerLatestVersion()
            }
            else -> Unit
        }
    }

    private fun checkTrackerLatestVersion() {
        viewModelScope.launch {
            updater.getLatestReleasedVersion(trackerRepoLink)
        }
    }

    fun onEvent(event: ManagerOverviewEvent) {
        when (event) {
            is ManagerOverviewEvent.OnResumed -> {
                updateBluetoothAdapterState()
                updatePermissionsState()
                manageBluetoothDevices()
            }
            is ManagerOverviewEvent.OnMeasurementError -> {
                playAlertSound()
            }
        }
    }

    private fun updatePermissionsState() {
        state = state.copy(
            isPermissionRequired = permissionHandler.getNotGrantedPermissionList().isNotEmpty(),
        )
    }

    private fun updateBluetoothAdapterState() {
        state = state.copy(
            isBluetoothAdapterEnabled = androidBluetoothService.isBluetoothEnabled(),
        )
    }
}