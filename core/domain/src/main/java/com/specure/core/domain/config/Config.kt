package com.specure.core.domain.config

interface Config {

    companion object {
        const val ALERT_SOUND_ON_TEST_ERROR_ENABLED_CONFIG_KEY = "ALERT_SOUND_ON_TEST_ERROR_ENABLED_CONFIG_KEY"
        const val KEEP_SCREEN_ON_ENABLED_CONFIG_KEY = "KEEP_SCREEN_ON_ENABLED_CONFIG_KEY"
    }

    fun isAlertSoundOnTestErrorEnabled(): Boolean

    fun setIsAlertSoundOnTestErrorEnabled(enabled: Boolean)

    fun getIsAlertSoundOnTestErrorEnabledDefault(): Boolean

    fun isKeepScreenOnEnabled(): Boolean

    fun setKeepScreenOnEnabled(enabled: Boolean)

    fun getKeepScreenOnEnabledDefault(): Boolean
}
