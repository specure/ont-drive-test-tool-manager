package com.cadrikmdev.core.domain.config

interface Config {

    companion object {
        const val ALERT_SOUND_ON_TEST_ERROR_ENABLED_CONFIG_KEY = "ALERT_SOUND_ON_TEST_ERROR_ENABLED_CONFIG_KEY"
    }

    fun isAlertSoundOnTestErrorEnabled(): Boolean

    fun setIsAlertSoundOnTestErrorEnabled(enabled: Boolean)

    fun getIsAlertSoundOnTestErrorEnabledDefault(): Boolean
}
