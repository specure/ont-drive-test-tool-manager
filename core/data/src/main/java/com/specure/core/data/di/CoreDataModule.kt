package com.specure.core.data.di

import AndroidBluetoothService
import android.bluetooth.BluetoothManager
import androidx.core.content.getSystemService
import com.specure.core.data.package_info.AndroidPackageInfoProvider
import com.specure.core.domain.package_info.PackageInfoProvider
import com.specure.core.domain.service.BluetoothService
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::AndroidBluetoothService).bind<BluetoothService>()
    singleOf(::AndroidPackageInfoProvider).bind<PackageInfoProvider>()

    single { androidApplication().getSystemService<BluetoothManager>() }
}