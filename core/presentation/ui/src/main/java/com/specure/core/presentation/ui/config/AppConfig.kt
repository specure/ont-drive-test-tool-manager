package com.specure.core.presentation.ui.config

import android.content.SharedPreferences
import com.specure.core.domain.config.Config

class AppConfig(
    private val preferences: SharedPreferences
) : Config {

    companion object {
        const val IS_ALERT_SOUND_ON_TEST_ERROR_ENABLED_BY_DEFAULT = true
        const val KEEP_SCREEN_ON_ENABLED_BY_DEFAULT = true
    }

    override fun isAlertSoundOnTestErrorEnabled(): Boolean {
        return preferences.getBoolean(
            Config.ALERT_SOUND_ON_TEST_ERROR_ENABLED_CONFIG_KEY,
            getIsAlertSoundOnTestErrorEnabledDefault()
        )
    }

    override fun setIsAlertSoundOnTestErrorEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(Config.ALERT_SOUND_ON_TEST_ERROR_ENABLED_CONFIG_KEY, enabled).apply()
    }

    override fun getIsAlertSoundOnTestErrorEnabledDefault(): Boolean {
        return IS_ALERT_SOUND_ON_TEST_ERROR_ENABLED_BY_DEFAULT
    }

    override fun isKeepScreenOnEnabled(): Boolean {
        return preferences.getBoolean(
            Config.KEEP_SCREEN_ON_ENABLED_CONFIG_KEY,
            getKeepScreenOnEnabledDefault()
        )
    }

    override fun setKeepScreenOnEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(Config.KEEP_SCREEN_ON_ENABLED_CONFIG_KEY, enabled).apply()
    }

    override fun getKeepScreenOnEnabledDefault(): Boolean {
        return KEEP_SCREEN_ON_ENABLED_BY_DEFAULT
    }

    override fun setSelectedDevicesAddresses(deviceAddresses: List<String>) {
        TODO("Not yet implemented")
    }

    override fun getSelectedDevicesAddress(): List<String> {
        TODO("Not yet implemented")
    }
}