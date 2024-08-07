package com.cadrikmdev.signaltrackermanager

import android.app.Application
import android.content.Context
import com.cadrikmdev.intercom.data.di.intercomDataModule
import com.cadrikmdev.manager.presentation.di.managerPresentationModule
import com.cadrikmdev.permissions.presentation.di.permissionsModule
import com.cadrikmdev.signaltrackermanager.di.appModule
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class SignalTrackerManagerApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {

        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@SignalTrackerManagerApp)
            modules(
                appModule,
                intercomDataModule,
                managerPresentationModule,
                permissionsModule,
            )
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}