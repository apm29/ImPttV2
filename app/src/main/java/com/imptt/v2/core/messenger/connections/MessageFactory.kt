package com.imptt.v2.core.messenger.connections

import android.os.Bundle
import android.os.Message
import android.util.Log
import com.google.gson.Gson
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.websocket.Group
import com.imptt.v2.di.PrettyPrintGson
import org.koin.core.qualifier.StringQualifier
import org.koin.java.KoinJavaComponent.inject
import java.util.ArrayList

object MessageFactory {
    private val TAG = MessageFactory::class.java.canonicalName
    private val prettyGson: Gson by inject(
        Gson::class.java,
        StringQualifier(PrettyPrintGson)
    )

    private fun createViewSideMessage(
        type: Int,
        objData: Any? = null,
        bundleData: Bundle = Bundle.EMPTY
    ): Message {
        return Message.obtain(ViewMessenger.handler()).apply {
            what = type
            obj = objData
            data = bundleData
            replyTo = ViewMessenger.myself()
        }
    }


    //View进程注册反注册Messenger
    fun createViewRegisterMessage(): Message {
        return createViewSideMessage(MESSAGE_TYPE_REGISTER_VIEW)
    }

    fun createViewUnregisterMessage(): Message {
        return createViewSideMessage(MESSAGE_TYPE_UNREGISTER_VIEW)
    }


    fun createWsRegisterSuccessMessage(groups:List<Group>):Message{
        return  createViewSideMessage(
            MESSAGE_TYPE_GROUP_LIST,
            bundleData = Bundle().apply {
                putParcelableArrayList(MESSAGE_DATA_KEY_GROUP_LIST, ArrayList(groups))
            }
        )
    }


    private fun log(message: Message) {
        this.log(message, tag = "创建消息")
    }

    fun log(message: Message, tag: String) {
        Log.d(TAG, "========================================================")
        Log.d(TAG, "$tag:$message")
        Log.d(TAG, "Target:${message.target}")
        Log.d(TAG, "What:${message.what}")
        Log.d(TAG, "Arg1:${message.arg1}")
        Log.d(TAG, "Arg2:${message.arg2}")
        Log.d(TAG, "Data:${dataToString(message.data)}")
        Log.d(TAG, "Obj:\r\n${prettyGson.toJson(message.obj)}")
        Log.d(TAG, "ReplyTo:${message.replyTo}")
        Log.d(TAG, "========================================================")
    }

    private fun dataToString(data:Bundle):String{
        return try {
            data.classLoader = MessageFactory::class.java.classLoader
            data.keySet().joinToString("\r\n") {
                prettyGson.toJson(data.get(it))
            }
        } catch (e: Exception) {
            "获取bundle数据失败"
        }
    }
}