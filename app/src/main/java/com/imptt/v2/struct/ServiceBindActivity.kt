package com.imptt.v2.struct

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.imptt.v2.core.binder.ServicePushToTalk

/**
 *  author : ciih
 *  date : 2020/9/29 4:22 PM
 *  description :
 */
open class ServiceBindActivity : AppCompatActivity(), ServiceConnection {

    private val proxy:ServiceBinderProxy by lazy {
        ServiceBinderProxy(this,this)
    }

    private var mBinder:ServicePushToTalk? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        proxy.ensureCreated()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.mBinder = service as? ServicePushToTalk
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        this.mBinder = null
    }
}