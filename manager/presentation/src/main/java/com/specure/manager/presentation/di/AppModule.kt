package com.specure.manager.presentation.di


import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.specure.core.domain.config.Config
import com.specure.core.presentation.ui.config.AppConfig
import com.specure.intercom.data.di.DI_BLUETOOTH_CLIENT_SERVICE_BLE
import com.specure.intercom.data.di.DI_BLUETOOTH_CLIENT_SERVICE_CLASSIC
import com.specure.intercom.domain.client.BluetoothClientService
import com.specure.manager.presentation.about.AboutScreenViewModel
import com.specure.manager.presentation.screens.devices.DevicesViewModel
import com.specure.manager.presentation.screens.manager_overview.ManagerOverviewViewModel
import com.specure.manager.presentation.screens.settings.SettingsScreenViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val managerPresentationModule = module {
    viewModel {
        val bluetoothClientService = get<BluetoothClientService>(named(
            DI_BLUETOOTH_CLIENT_SERVICE_CLASSIC))

        ManagerOverviewViewModel(
            get(),
            get(),
            get(),
            bluetoothClientService,
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModelOf(::AboutScreenViewModel)
    viewModelOf(::DevicesViewModel)
    viewModelOf(::SettingsScreenViewModel)

    single<SharedPreferences> {
        // TODO: migrate to new preference management AndroidX Preference Library
        PreferenceManager.getDefaultSharedPreferences(androidApplication())
    }
    singleOf(::AppConfig).bind<Config>()
}