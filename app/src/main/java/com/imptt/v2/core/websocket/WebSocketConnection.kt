package com.imptt.v2.core.websocket

import android.util.Log
import com.google.gson.Gson
import com.imptt.v2.di.ParserGson
import com.imptt.v2.di.PrettyPrintGson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.koin.core.qualifier.StringQualifier
import org.koin.java.KoinJavaComponent.inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

/**
 *  author : apm29[ciih]
 *  date : 2020/9/30 10:12 AM
 *  description :
 */
class WebSocketConnection : WebSocketListener() {
    companion object {
        val TAG = WebSocketConnection::class.java.canonicalName
    }

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        println("SignalServerConnection.onClosed")
        closeListeners.forEach { callback ->
            webSocket.callback(reason)
        }
    }

    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
     */
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("SignalServerConnection.onClosing")
        super.onClosing(webSocket, code, reason)
    }

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("SignalServerConnection.onFailure")
        println("webSocket = [${webSocket}], t = [${t}], response = [${response}]")
        super.onFailure(webSocket, t, response)
        //筛选出已发送未接受的消息
        val message: Collection<String>? = sentMessageMap.filter {
            queuedMessagesId.contains(it.key)
        }.values
        failListeners.forEach { callback ->
            webSocket.callback(message, t)
        }
    }

    /** Invoked when a text (type `0x1`) message has been received. */
    override fun onMessage(webSocket: WebSocket, text: String) {
        println("SignalServerConnection.onMessage")
        logReceive(text)
        super.onMessage(webSocket, text)
        //尝试解析为SignalMessage
        val signalMessage = try {
            parserGson.fromJson(text,SignalMessage::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "WebSocket消息序列化失败:message:$text")
            e.printStackTrace()
            SignalMessage.Fail()
        }
        //已发送列表中移除有消息的id
        queuedMessagesId.remove(signalMessage.id)
        val type = signalMessage.type
        textMessageListenerMap[type]?.forEach { callback ->
            try {
                //回调signalMessage和原始String
                webSocket.callback(signalMessage,text)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Invoked when a binary (type `0x2`) message has been received. */
    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
    }

    /**
     * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
     * messages.
     */
    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("SignalServerConnection.onOpen")
        super.onOpen(webSocket, response)
        openListeners.forEach { callback ->
            try {
                callback.invoke(webSocket, response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //消息类型监听
    private val textMessageListenerMap: HashMap<String, ArrayList<WebSocketMessageCallback>> =
        hashMapOf()
    //ws开启监听
    private val openListeners: ArrayList<WebSocketOpenCallback> = arrayListOf()
    //ws关闭监听
    private val closeListeners: ArrayList<WebSocketCloseCallback> = arrayListOf()
    //ws消息发送失败监听
    private val failListeners: ArrayList<WebSocketFailCallback> = arrayListOf()
    //已发送的消息 类型-> 消息内容 map
    private val sentMessageMap: LinkedHashMap<String, String> = LinkedHashMap()
    private val parserGson: Gson  by inject(Gson::class.java,qualifier = StringQualifier(ParserGson))
    private val printerGson:Gson by inject(Gson::class.java,qualifier = StringQualifier(PrettyPrintGson))
    //已发送但信令服务器未响应的消息id集合
    private val queuedMessagesId:ArrayList<String> = arrayListOf()

    fun on(type: String, callback: WebSocketMessageCallback): WebSocketConnection {
        val listeners = textMessageListenerMap[type] ?: arrayListOf()
        listeners.add(callback)
        textMessageListenerMap[type] = listeners
        return this
    }

    fun on(type:WebSocketTypes,callback: WebSocketMessageCallback): WebSocketConnection{
        return this.on(type.type,callback)
    }

    fun whenOpen(callback: WebSocketOpenCallback): WebSocketConnection {
        openListeners.add(callback)
        return this
    }

    fun whenClose(callback: WebSocketCloseCallback): WebSocketConnection {
        closeListeners.add(callback)
        return this
    }

    fun whenFail(callback: WebSocketFailCallback): WebSocketConnection {
        failListeners.add(callback)
        return this
    }

    /**
     * 去掉所有事件监听
     */
    fun offAll(){
        textMessageListenerMap.clear()
        openListeners.clear()
        failListeners.clear()
        closeListeners.clear()
    }

    private fun sendTextMessage(message: String, webSocket: WebSocket, id: String) {
        if (webSocket.send(message)) synchronized(webSocket) {
            queuedMessagesId.add(id)
            sentMessageMap[id] = message
            logSend(message)
        }
    }

    fun  send(message: SignalMessage, webSocket: WebSocket, id: String = message.id) {
        val textMessage = parserGson.toJson(message)
        sendTextMessage(textMessage, webSocket, id)
    }

    private fun logSend(message: String){
        Log.e(TAG,"=============================================>>>")
        Log.i(TAG,"WebSocket客户端发送:$message")
    }

    private fun logReceive(message: String){
        Log.e(TAG,"<<<=============================================")
        Log.v(TAG,"WebSocket客户端接收:$message")
    }
}

typealias WebSocketMessageCallback = WebSocket.(SignalMessage,String) -> Unit
typealias WebSocketOpenCallback = WebSocket.(Response) -> Unit
typealias WebSocketCloseCallback = WebSocket.(String) -> Unit
//参数一，截止目前未发送成功（服务端无响应的）消息，参数二：错误信息
typealias WebSocketFailCallback = WebSocket.(Collection<String>?, Throwable) -> Unit