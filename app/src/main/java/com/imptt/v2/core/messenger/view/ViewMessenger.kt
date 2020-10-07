package com.imptt.v2.core.messenger.view

import android.os.*
import android.util.Log
import com.imptt.v2.core.messenger.connections.MessageFactory

object ViewMessenger {
    private val TAG = ViewMessenger::class.java.canonicalName

    //messenger
    private var mServiceMessenger: Messenger? = null
    private val mServiceRequestHandler = Handler(Looper.getMainLooper()) { message ->
        logReceive(message)
        true
    }
    private val mViewMessenger: Messenger = Messenger(mServiceRequestHandler)
    private val boundToService
        get() = mServiceMessenger != null

    fun bindService(service: IBinder?) {
        this.mServiceMessenger = Messenger(service)
        send(MessageFactory.createViewRegisterMessage())
    }

    fun unbind() {
        send(MessageFactory.createViewUnregisterMessage())
        this.mServiceMessenger = null
    }

    fun send(message: Message) {
        if (boundToService) {
            logSend(message)
            mServiceMessenger?.send(message)
        } else {
            Log.e(TAG, "还未绑定到Ptt服务:Service Messenger is not bind to ViewMessenger")
        }

    }

    fun myself() = mViewMessenger
    fun handler() = mServiceRequestHandler

    private fun logSend(message: Message){
        MessageFactory.log(message,"UI进程发送消息")
    }
    private fun logReceive(message: Message){
        MessageFactory.log(message,"UI进程收到消息")
    }

}