package com.cadrikmdev.core.data.di

import AndroidBluetoothService
import android.bluetooth.BluetoothManager
import androidx.core.content.getSystemService
import com.cadrikmdev.core.domain.service.BluetoothService
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::AndroidBluetoothService).bind<BluetoothService>()
    single { androidApplication().getSystemService<BluetoothManager>() }
}