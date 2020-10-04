package com.imptt.v2

import android.app.Application
import android.content.Intent
import com.imptt.v2.core.ImService
import com.imptt.v2.di.serviceModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@HiltAndroidApp
class App:Application() {
    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(serviceModule)
        }
        startService(Intent(this,ImService::class.java))
    }
}