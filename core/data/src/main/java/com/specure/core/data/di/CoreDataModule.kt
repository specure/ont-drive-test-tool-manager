package com.specure.core.data.di

import com.specure.core.data.package_info.AndroidPackageInfoProvider
import com.specure.core.domain.package_info.PackageInfoProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {

    singleOf(::AndroidPackageInfoProvider).bind<PackageInfoProvider>()
}