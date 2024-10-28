package com.specure.signaltrackermanager

import android.app.Application
import android.content.Context
import com.specure.core.data.di.coreDataModule
import com.specure.intercom.data.di.intercomDataModule
import com.specure.manager.presentation.di.managerPresentationModule
import com.specure.permissions.presentation.di.permissionsModule
import com.specure.signaltrackermanager.di.appModule
import com.google.android.play.core.splitcompat.SplitCompat
import com.specure.updater.data.di.updaterDataModule
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
                coreDataModule,
                intercomDataModule,
                managerPresentationModule,
                permissionsModule,
                updaterDataModule,
            )
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}