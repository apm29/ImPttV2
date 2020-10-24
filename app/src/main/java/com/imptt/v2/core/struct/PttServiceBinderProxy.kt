package com.imptt.v2.core.struct

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.imptt.v2.core.ImService
import com.kylindev.pttlib.service.InterpttService

/**
 *  author : apm29[ciih]
 *  date : 2020/9/29 4:26 PM
 *  description :
 */
class PttServiceBinderProxy(
    private val lifecycleOwner: ComponentActivity,
    private val serviceConnection: ServiceConnection
) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.removeObserver(this)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    var serviceBind = false

    fun ensureCreated() {}

    private fun createServiceIntent(): Intent {
        return Intent(lifecycleOwner, InterpttService::class.java)
    }

    private val serviceIntent: Intent by lazy {
        createServiceIntent()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        println("PttServiceBinderProxy.onCreate")
        println("lifecycleOwner = $lifecycleOwner")
        lifecycleOwner.startService(serviceIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        println("PttServiceBinderProxy.onStart")
        tryBind()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        println("PttServiceBinderProxy.onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        println("PttServiceBinderProxy.onPause")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        println("PttServiceBinderProxy.onStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        println("PttServiceBinderProxy.onDestroy")
        if (serviceBind) {
            lifecycleOwner.unbindService(serviceConnection)
        }
        serviceBind = false
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    fun tryBind() {
        serviceBind =
            lifecycleOwner.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

}