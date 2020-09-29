package com.imptt.v2.core.messenger.service

import android.os.*

object ServiceMessenger {

    //messenger
    private val mViewRequestHandler = Handler(Looper.getMainLooper()){
        true
    }
    private val mMessenger:Messenger =  Messenger(mViewRequestHandler)

    private var activityBound = false

    fun bindMessenger(): IBinder? {
        activityBound = true
        return mMessenger.binder
    }

    fun unbind() {
        activityBound = false
    }

    fun sendMessage(message: Message){
        mMessenger.send(message)
    }

}