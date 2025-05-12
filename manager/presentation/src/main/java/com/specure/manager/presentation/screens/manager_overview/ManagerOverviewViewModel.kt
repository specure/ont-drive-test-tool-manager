package com.specure.manager.presentation.screens.manager_overview

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cadrikmdev.intercom.data.message.toMessageActionDto
import com.cadrikmdev.intercom.domain.BluetoothDevicesProvider
import com.cadrikmdev.intercom.domain.client.BluetoothClientService
import com.cadrikmdev.intercom.domain.data.MessageContent
import com.cadrikmdev.intercom.domain.message.MessageWrapper
import com.cadrikmdev.intercom.domain.message.SerializableContent
import com.cadrikmdev.intercom.domain.service.BluetoothService
import com.specure.core.domain.config.Config
import com.specure.core.presentation.ui.UiText
import com.specure.manager.presentation.BuildConfig
import com.specure.manager.presentation.R
import com.specure.manager.presentation.data.ManagedBluetoothDevice
import com.specure.manager.presentation.mappers.toManagedBluetoothDevice
import com.specure.permissions.domain.PermissionHandler
import com.specure.permissions.presentation.appPermissions
import com.specure.track.domain.intercom.data.MeasurementProgressContent
import com.specure.track.domain.intercom.data.StartTestContent
import com.specure.track.domain.intercom.data.StopTestContent
import com.specure.updater.domain.Updater
import com.specure.updater.domain.UpdatingStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber


class ManagerOverviewViewModel(
    private val appContext: Context,
    private val appConfig: Config,
    private val applicationScope: CoroutineScope,
    private val bluetoothService: BluetoothClientService,
    private val bluetoothDevicesProvider: BluetoothDevicesProvider<BluetoothDevice>,
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

//        manageBluetoothDevices()
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

//            val pairedDevicesFlow = bluetoothDevicesProvider.observePairedDevices(DeviceType.WORKER)

//            pairedDevicesFlow.onEach { pairedDevices ->
//                stateManager.setPairedDevices(
//                    pairedDevices.values.map { it.toTrackingDevice() }.filterNotNull().toList()
//                )
//            }.launchIn(viewModelScope)

            bluetoothService.receivedActionFlow.onEach { wrappedMessage ->
                val action = wrappedMessage?.toMessageActionDto()
                when(wrappedMessage) {
                    is MessageWrapper.SendMessage -> {
                        val content = wrappedMessage.content.content
                        when (content) {
                            is MeasurementProgressContent -> {
                                updateManagedDevice(wrappedMessage.sourceDevice, content)
                                Timber.d("Obtained message: $content")
                            }
                            StartTestContent -> {}
                            StopTestContent -> {}
                            else -> Timber.e("Unknown content: ${wrappedMessage.content.content}")
                        }
                    }
                }
                state.managedDevices
            }.launchIn(viewModelScope)


            bluetoothService.pairedBluetoothDevices
                .map { connectedDevices ->
                    Timber.d("getting devices tracking devices: ${connectedDevices.values}")
                    val selectedDevicesAddresses = appConfig.getSelectedDevicesAddress()

                    connectedDevices
                        .filter { device ->
                            selectedDevicesAddresses.contains(device.value.address)
                        }
                        .mapValues { (_, bluetoothDevice) ->
                            bluetoothDevice.toManagedBluetoothDevice()
                        }
                }
                .onEach { managedDevices ->
                    updateManagedDevices(managedDevices)
                }
                .launchIn(viewModelScope)
        }
    }

    private fun updateManagedDevices(managedDevices: Map<String, ManagedBluetoothDevice>) {
        val previousState = state.copy()
        val newErrorDevices = previousState.managedDevices.filter { prevDeviceState ->
            managedDevices.filter { currentDeviceState ->
                //TODO: override tracking device with device with more details and use it on UI
                currentDeviceState.value.isStateChangedOnTheSameDevice(prevDeviceState) && (currentDeviceState.value.isErrorState() || currentDeviceState.value.isSpeedTestErrorState())
            }.isNotEmpty()
        }
        if (newErrorDevices.isNotEmpty()) {
            playAlertSound()
        }
        val updateCheckDevices = managedDevices.values.map { device ->
            val latestVersion = updater.getLatestVersion(device.deviceAppVersion, state.lastTrackerVersion)
            if (latestVersion != null && device.deviceAppVersion != latestVersion) {
                device.copy(
                    deviceAppVersion = "${device.deviceAppVersion} (${
                        UiText.StringResource(
                            R.string.udpateAvailable
                        ).asString(appContext)
                    })"
                )
            } else {
                device
            }

        }
        state = state.copy(
            managedDevices = updateCheckDevices.toList()
        )
    }

    private fun updateManagedDevice(device: com.cadrikmdev.intercom.domain.data.BluetoothDevice, content: MeasurementProgressContent) {
        val newDeviceState = ManagedBluetoothDevice(
            device = device,
            status = content.state,
            errors = content.errors,
            deviceAppVersion = content.appVersion ?: "",
        )
        val latestVersion = updater.getLatestVersion(newDeviceState.deviceAppVersion, state.lastTrackerVersion)
        val updatedDeviceState: ManagedBluetoothDevice = if (latestVersion != null && newDeviceState.deviceAppVersion != latestVersion) {
            newDeviceState.copy(
                deviceAppVersion = "${newDeviceState.deviceAppVersion} (${
                    UiText.StringResource(
                        R.string.udpateAvailable
                    ).asString(appContext)
                })"
            )
        } else {
            newDeviceState
        }
        Timber.d("Updated device state timestamp: ${updatedDeviceState.device.lastUpdatedTimestamp}")

        val previousState = state.copy()
        val previousDeviceState = previousState.managedDevices.firstOrNull { it.device.address == device.address }
        val isDeviceInErrorState = updatedDeviceState.isErrorState() || updatedDeviceState.isSpeedTestErrorState()
        val isStateChangedOnTheDevice = updatedDeviceState.isStateChangedOnTheSameDevice(previousDeviceState)

        if (previousDeviceState == null) {
            if (isDeviceInErrorState) {
                playAlertSound()
            }
            state = state.copy(
                managedDevices = state.managedDevices + updatedDeviceState
            )
        } else {
            if (isDeviceInErrorState && isStateChangedOnTheDevice) {
                playAlertSound()
            }
            state = state.copy(
                managedDevices = state.managedDevices.filterNot { it.device.address == device.address } + updatedDeviceState
            )
        }
    }

    fun onAction(action: ManagerOverviewAction) {
        when (action) {
            is ManagerOverviewAction.DeleteManager -> {
                TODO()
            }

            is ManagerOverviewAction.OnConnectClick -> {
                viewModelScope.launch {
                    val result = bluetoothService.connectToDevice(action.device.address)
                    Timber.d("Connect result: $result")
                }
            }

            is ManagerOverviewAction.OnStartClick -> {
                viewModelScope.launch {
                    bluetoothService.sendActionFlow.emit(
                        MessageWrapper.SendMessage(
                            sourceDevice = action.device,
                            content = MessageContent<SerializableContent>(
                                content = StartTestContent,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    )
                }
            }

            is ManagerOverviewAction.OnStopClick -> {
                viewModelScope.launch {
                    bluetoothService.sendActionFlow.emit(
                        MessageWrapper.SendMessage(
                            sourceDevice = action.device,
                            content = MessageContent<SerializableContent>(
                                content = StopTestContent,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    )
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

    private fun updateSetPreferences() {
        state = state.copy(
            keepScreenOn = appConfig.isKeepScreenOnEnabled()
        )
    }

    fun onEvent(event: ManagerOverviewEvent) {
        when (event) {
            is ManagerOverviewEvent.OnResumed -> {
                updateBluetoothAdapterState()
                updatePermissionsState()
                manageBluetoothDevices()
                updateSetPreferences()
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