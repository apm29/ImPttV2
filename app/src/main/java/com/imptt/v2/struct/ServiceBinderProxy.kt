package com.imptt.v2.struct

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.imptt.v2.core.ImService

/**
 *  author : ciih
 *  date : 2020/9/29 4:26 PM
 *  description :
 */
class ServiceBinderProxy(
    private val lifecycleOwner: ComponentActivity,
    private val serviceConnection: ServiceConnection
) :LifecycleObserver{

    init {
        lifecycleOwner.lifecycle.removeObserver(this)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun ensureCreated(){}

    private fun createServiceIntent():Intent{
        return Intent(lifecycleOwner,ImService::class.java)
    }

    private val serviceIntent:Intent by lazy {
        createServiceIntent()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        println("ServiceBinderProxy.onCreate")
        lifecycleOwner.startService(serviceIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        println("ServiceBinderProxy.onStart")
        lifecycleOwner.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        println("ServiceBinderProxy.onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        println("ServiceBinderProxy.onPause")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        println("ServiceBinderProxy.onStop")
        lifecycleOwner.unbindService(serviceConnection)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        println("ServiceBinderProxy.onDestroy")
        lifecycleOwner.lifecycle.removeObserver(this)
    }

}