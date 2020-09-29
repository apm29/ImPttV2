package com.imptt.v2.core.messenger.service

import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Messenger

object ServiceMessenger {

    //messenger
    private var mMessenger:Messenger? = null
    private val mViewRequestHandler = Handler(Looper.getMainLooper()){
        true
    }
    val activityBound
        get() = mMessenger != null

    fun bindMessenger(): IBinder? {
        this.mMessenger =  Messenger(mViewRequestHandler)
        return mMessenger?.binder
    }

    fun unbind() {
        this.mMessenger = null
    }

}