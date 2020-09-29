package com.imptt.v2.core.struct

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.imptt.v2.core.messenger.view.ViewMessenger

/**
 *  author : ciih
 *  date : 2020/9/29 4:22 PM
 *  description :
 */
open class ServiceBindActivity : AppCompatActivity(), ServiceConnection {

    private val serviceBinderProxy: ServiceBinderProxy by lazy {
        ServiceBinderProxy(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceBinderProxy.ensureCreated()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        //this.pttService = service as ImService.ServicePushToTalk?
        ViewMessenger.bindService(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        //this.pttService = null
        ViewMessenger.unbind()
    }
}