package com.specure.core.domain.config

import kotlinx.coroutines.flow.Flow

interface Config {

    companion object {
        const val ALERT_SOUND_ON_TEST_ERROR_ENABLED_CONFIG_KEY = "ALERT_SOUND_ON_TEST_ERROR_ENABLED_CONFIG_KEY"
        const val KEEP_SCREEN_ON_ENABLED_CONFIG_KEY = "KEEP_SCREEN_ON_ENABLED_CONFIG_KEY"
        const val SELECTED_DEVICE_ADDRESSES_CONFIG_KEY = "SELECTED_DEVICE_ADDRESSES_CONFIG_KEY"
    }

    fun isAlertSoundOnTestErrorEnabled(): Boolean

    fun setIsAlertSoundOnTestErrorEnabled(enabled: Boolean)

    fun getIsAlertSoundOnTestErrorEnabledDefault(): Boolean

    fun isKeepScreenOnEnabled(): Boolean

    fun setKeepScreenOnEnabled(enabled: Boolean)

    fun getKeepScreenOnEnabledDefault(): Boolean

    fun setSelectedDevicesAddresses(deviceAddresses: Set<String>)

    fun getSelectedDevicesAddress(): Set<String>

    fun listenToPreferencesChanges(): Flow<Pair<String?, Any?>>
}
