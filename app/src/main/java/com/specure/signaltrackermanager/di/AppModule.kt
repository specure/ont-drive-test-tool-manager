package com.specure.signaltrackermanager.di


import com.specure.signaltrackermanager.MainViewModel
import com.specure.signaltrackermanager.SignalTrackerManagerApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::MainViewModel)

    single<CoroutineScope> {
        (androidApplication() as SignalTrackerManagerApp).applicationScope
    }
}