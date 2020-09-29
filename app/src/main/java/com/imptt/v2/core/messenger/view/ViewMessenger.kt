package com.imptt.v2.core.messenger.view

import android.os.*
import android.util.Log

object ViewMessenger {
    private val TAG = ViewMessenger::class.java.canonicalName
    //messenger
    private var mServiceMessenger:Messenger? = null
    private val mServiceRequestHandler = Handler(Looper.getMainLooper()){
        true
    }
    private val mViewMessenger:Messenger = Messenger(mServiceRequestHandler)
    val boundToService
        get() = mServiceMessenger != null

    fun bindService(service: IBinder?) {
        this.mServiceMessenger =  Messenger(service)
    }

    fun unbind() {
        this.mServiceMessenger = null
    }

    fun send(message: Message) {
        mServiceMessenger?.send(message)?: Log.e(TAG, "Service Messenger is not bind to ViewMessenger")
    }

    fun myself()= mViewMessenger

}