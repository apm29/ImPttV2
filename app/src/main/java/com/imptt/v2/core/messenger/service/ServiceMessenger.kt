package com.imptt.v2.core.messenger.service

import android.os.*
import android.util.Log
import com.imptt.v2.core.messenger.connections.*
import com.imptt.v2.core.messenger.view.ViewMessenger

object ServiceMessenger {

    private val TAG = ServiceMessenger::class.java.canonicalName

    //messenger
    private val mViewRequestHandler = Handler(Looper.getMainLooper()) { message ->
        logReceive(message)
        handleMessage(message)
    }
    private val mMessenger: Messenger = Messenger(mViewRequestHandler)
    private val mViewMessengers: HashSet<Messenger> = HashSet(1)

    private var activityBound = false
    private val messageCallbacks = HashMap<Int, ArrayList<ViewMessageCallback>>()
    fun bindMessenger(): IBinder? {
        activityBound = true
        return mMessenger.binder
    }

    fun unbind() {
        activityBound = false
    }

    fun sendMessage(message: Message) {
        logSend(message)
        //倒序发送消息
        mViewMessengers.reversed().forEach {
            it.send(message)
        }
    }

    fun on(type: Int, callback: ViewMessageCallback): ServiceMessenger {
        val callbacks = messageCallbacks[type] ?: arrayListOf()
        callbacks.add(callback)
        messageCallbacks[type] = callbacks
        return this
    }

    fun offAll(type: Int) {
        messageCallbacks[type]?.clear()
    }

    fun off(type: Int, callback: ServiceMessageCallback): ServiceMessenger {
        messageCallbacks[type]?.remove(callback)
        return this
    }

    private fun handleMessage(message: Message): Boolean {
        messageCallbacks[message.what]?.forEach { callback ->
            try {
                callback.invoke(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        when (message.what) {
            MESSAGE_TYPE_REGISTER_VIEW -> {
                if (mViewMessengers.add(message.replyTo)) {
                    Log.d(TAG, "新增ViewMessenger")
                } else {
                    Log.d(TAG, "ViewMessenger已存在:无需注册")
                }
            }
            MESSAGE_TYPE_UNREGISTER_VIEW -> {
                if (mViewMessengers.remove(message.replyTo)) {
                    Log.d(TAG, "移除ViewMessenger")
                } else {
                    Log.d(TAG, "ViewMessenger不存在:注销失败")
                }
            }
            MESSAGE_TYPE_ECHO_TEST -> {
                sendMessage(message)
            }
            else -> {
                //其他
            }
        }
        return true
    }

    fun myself(): Messenger = mMessenger

    fun handler(): Handler = mViewRequestHandler

    private fun logSend(message: Message) {
        MessageFactory.log(message, "PTT服务进程发送消息")
    }

    private fun logReceive(message: Message) {
        MessageFactory.log(message, "PTT服务进程收到消息")
    }

}