package com.imptt.v2.core.messenger.service

import android.os.*
import android.util.Log
import com.imptt.v2.core.messenger.connections.MESSAGE_TYPE_ECHO_TEST
import com.imptt.v2.core.messenger.connections.MESSAGE_TYPE_REGISTER_VIEW
import com.imptt.v2.core.messenger.connections.MESSAGE_TYPE_UNREGISTER_VIEW
import com.imptt.v2.core.messenger.connections.MessageFactory

object ServiceMessenger {

    private val TAG = ServiceMessenger::class.java.canonicalName
    //messenger
    private val mViewRequestHandler = Handler(Looper.getMainLooper()){ message ->
        logReceive(message)
        handleMessage(message)
    }
    private val mMessenger:Messenger =  Messenger(mViewRequestHandler)
    private val mViewMessengers:HashSet<Messenger> = HashSet(1)

    private var activityBound = false

    fun bindMessenger(): IBinder? {
        activityBound = true
        return mMessenger.binder
    }

    fun unbind() {
        activityBound = false
    }

    fun sendMessage(message: Message){
        logSend(message)
        mViewMessengers.forEach {
            it.send(message)
        }
    }

    private fun logSend(message: Message){
        MessageFactory.log(message,"PTT服务进程发送消息")
    }
    private fun logReceive(message: Message){
        MessageFactory.log(message,"PTT服务进程收到消息")
    }

    private fun handleMessage(message: Message):Boolean{
        when(message.what){
            MESSAGE_TYPE_REGISTER_VIEW->{
                if(mViewMessengers.add(message.replyTo)){
                    Log.d(TAG,"新增ViewMessenger")
                }else{
                    Log.d(TAG,"ViewMessenger已存在:注册失败")
                }
            }
            MESSAGE_TYPE_UNREGISTER_VIEW->{
                if(mViewMessengers.remove(message.replyTo)){
                    Log.d(TAG,"移除ViewMessenger")
                }else{
                    Log.d(TAG,"ViewMessenger不存在:注销失败")
                }
            }
            MESSAGE_TYPE_ECHO_TEST->{
                sendMessage(message)
            }
            else->{
                //其他
            }
        }
        return true
    }

}