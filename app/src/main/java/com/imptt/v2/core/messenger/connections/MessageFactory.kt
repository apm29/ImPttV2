package com.imptt.v2.core.messenger.connections

import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.core.os.bundleOf
import com.google.gson.Gson
import com.imptt.v2.core.messenger.service.ServiceMessenger
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

    private fun createServiceSideMessage(
        type: Int,
        objData: Any? = null,
        bundleData: Bundle = Bundle.EMPTY
    ): Message {
        return Message.obtain(ServiceMessenger.handler()).apply {
            what = type
            obj = objData
            data = bundleData
            replyTo = ServiceMessenger.myself()
        }
    }


    /******************************************Log相关********************************************/
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

    private fun dataToString(data: Bundle): String {
        return try {
            data.classLoader = MessageFactory::class.java.classLoader
            data.keySet().joinToString("\r\n") {
                prettyGson.toJson(data.get(it))
            }
        } catch (e: Exception) {
            "获取bundle数据失败"
        }
    }

    /******************************************View侧调用********************************************/
    //View进程注册反注册Messenger
    fun createViewRegisterMessage(): Message {
        return createViewSideMessage(MESSAGE_TYPE_REGISTER_VIEW)
    }

    fun createViewUnregisterMessage(): Message {
        return createViewSideMessage(MESSAGE_TYPE_UNREGISTER_VIEW)
    }

    //开始呼叫
    fun createCallMessage(groupId: String): Message {
        return createViewSideMessage(
            MESSAGE_TYPE_CALL, bundleData = bundleOf(
                MESSAGE_DATA_KEY_GROUP_ID to groupId
            )
        )
    }

    fun createEndCallMessage(): Message {
        return createViewSideMessage(MESSAGE_TYPE_END_CALL)
    }

    /******************************************Service侧调用********************************************/
    //ptt进程获取群组信息后发送给view
    fun createWsRegisterSuccessMessage(groups: List<Group>): Message {
        return createServiceSideMessage(
            MESSAGE_TYPE_GROUP_LIST,
            bundleData = bundleOf(
                MESSAGE_DATA_KEY_GROUP_LIST to ArrayList(groups)
            )
        )
    }

    fun createInCallMessage(from: String, groupId: String): Message {
        return createServiceSideMessage(
            MESSAGE_TYPE_IN_CALL,
            bundleData = bundleOf(
                MESSAGE_DATA_KEY_FROM_USER_ID to from,
                MESSAGE_DATA_KEY_GROUP_ID to groupId
            )
        )
    }

    fun createToastMessage(message: String): Message {
        return createViewSideMessage(
            MESSAGE_TYPE_MESSAGE, bundleData = bundleOf(
                MESSAGE_DATA_KEY_MESSAGE to message
            )
        )
    }


}