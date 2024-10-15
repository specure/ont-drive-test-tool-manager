package com.cadrikmdev.manager.presentation.di


import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.cadrikmdev.core.domain.config.Config
import com.cadrikmdev.core.presentation.ui.config.AppConfig
import com.cadrikmdev.manager.presentation.about.AboutScreenViewModel
import com.cadrikmdev.manager.presentation.screens.manager_overview.ManagerOverviewViewModel
import com.cadrikmdev.manager.presentation.screens.settings.SettingsScreenViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val managerPresentationModule = module {
    viewModelOf(::ManagerOverviewViewModel)
    viewModelOf(::AboutScreenViewModel)
    viewModelOf(::SettingsScreenViewModel)

    single<SharedPreferences> {
        // TODO: migrate to new preference management AndroidX Preference Library
        PreferenceManager.getDefaultSharedPreferences(androidApplication())
    }
    singleOf(::AppConfig).bind<Config>()
}