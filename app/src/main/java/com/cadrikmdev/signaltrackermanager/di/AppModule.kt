package com.cadrikmdev.signaltrackermanager.di


import com.cadrikmdev.signaltrackermanager.MainViewModel
import com.cadrikmdev.signaltrackermanager.SignalTrackerManagerApp
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