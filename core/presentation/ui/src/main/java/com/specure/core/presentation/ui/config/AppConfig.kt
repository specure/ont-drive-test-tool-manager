package com.specure.core.presentation.ui.config

import android.content.SharedPreferences
import com.specure.core.domain.config.Config

class AppConfig(
    private val preferences: SharedPreferences
) : Config {

    companion object {
        const val IS_ALERT_SOUND_ON_TEST_ERROR_ENABLED_BY_DEFAULT = true
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
}