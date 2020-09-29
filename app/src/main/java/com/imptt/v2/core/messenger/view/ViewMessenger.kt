package com.imptt.v2.core.messenger.view

import android.os.*

object ViewMessenger {

    //messenger
    private var mServiceMessenger:Messenger? = null
    private val mServiceRequestHandler = Handler(Looper.getMainLooper()){
        true
    }
    private val mViewMessenger:Messenger = Messenger(mServiceRequestHandler)
    val boundToService
        get() = mServiceMessenger != null

    fun bindMessenger(service: IBinder?) {
        this.mServiceMessenger =  Messenger(service)
    }

    fun unbind() {
        this.mServiceMessenger = null
    }

    fun send(message: Message) {
        mViewMessenger.send(message)
    }

    fun myself()= mViewMessenger

}