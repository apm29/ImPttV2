package com.imptt.v2

import android.app.Application
import android.os.Process
import com.imptt.v2.di.serviceModule
import com.imptt.v2.di.viewModule
import com.tencent.bugly.Bugly
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        println("start koin : pid = ${Process.myPid()}")
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(serviceModule,viewModule)
        }
        if(!BuildConfig.DEBUG)
            Bugly.init(this, "3328c83306", BuildConfig.DEBUG)
    }
}