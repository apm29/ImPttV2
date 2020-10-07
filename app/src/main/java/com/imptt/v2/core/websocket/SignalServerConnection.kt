package com.imptt.v2.core.websocket

import android.util.Log
import com.google.gson.Gson
import com.imptt.v2.di.ParserGson
import com.imptt.v2.di.PrettyPrintGson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import org.koin.core.qualifier.StringQualifier
import org.koin.java.KoinJavaComponent.inject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

/**
 *  author : ciih
 *  date : 2020/9/30 10:12 AM
 *  description :
 */
class SignalServerConnection : WebSocketListener() {
    companion object {
        val TAG = SignalServerConnection::class.java.canonicalName
    }

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        closeListeners.forEach { callback ->
            webSocket.callback(reason)
        }
    }

    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
     */
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
    }

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        val message: String? = sentMessageMap[lastMessageUUID]
        failListeners.forEach { callback ->
            webSocket.callback(message, t)
        }
    }

    /** Invoked when a text (type `0x1`) message has been received. */
    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val json = try {
            JSONObject(text)
        } catch (e: Exception) {
            Log.e(TAG, "WebSocket消息序列化失败:message:$text")
            JSONObject()
        }
        val type = json.getString("type")
        textMessageListenerMap[type]?.forEach { callback ->
            webSocket.callback(text)
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
        super.onOpen(webSocket, response)
        openListeners.forEach {
            it.invoke(webSocket, response)
        }
    }


    private val textMessageListenerMap: HashMap<String, ArrayList<WebSocketMessageCallback>> =
        hashMapOf()
    private val openListeners: ArrayList<WebSocketOpenCallback> = arrayListOf()
    private val closeListeners: ArrayList<WebSocketCloseCallback> = arrayListOf()
    private val failListeners: ArrayList<WebSocketFailCallback> = arrayListOf()
    private val sentMessageMap: LinkedHashMap<UUID, String> = LinkedHashMap()
    private val gson: Gson  by inject(Gson::class.java,qualifier = StringQualifier(ParserGson))
    private val printerGson:Gson by inject(Gson::class.java,qualifier = StringQualifier(PrettyPrintGson))

    @Volatile
    private var lastMessageUUID: UUID = UUID.randomUUID()
    fun on(type: String, callback: WebSocketMessageCallback): SignalServerConnection {
        val listeners = textMessageListenerMap[type] ?: arrayListOf()
        listeners.add(callback)
        return this
    }

    fun whenOpen(callback: WebSocketOpenCallback): SignalServerConnection {
        openListeners.add(callback)
        return this
    }

    fun whenClose(callback: WebSocketCloseCallback): SignalServerConnection {
        closeListeners.add(callback)
        return this
    }

    fun whenFail(callback: WebSocketFailCallback): SignalServerConnection {
        failListeners.add(callback)
        return this
    }

    fun sendTextMessage(message: String, webSocket: WebSocket, id: UUID = UUID.randomUUID()) {
        if (webSocket.send(message)) synchronized(webSocket) {
            lastMessageUUID = id
            sentMessageMap[id] = message
            logSend(message)
        }
    }

    fun <T> send(message: T, webSocket: WebSocket, id: UUID = UUID.randomUUID()) {
        val textMessage = gson.toJson(message)
        sendTextMessage(textMessage, webSocket, id)
    }

    private fun logSend(message: String){
        Log.d(TAG,"WebSocket客户端发送:${printerGson.toJson(message)}")
    }
}

typealias WebSocketMessageCallback = WebSocket.(String) -> Unit
typealias WebSocketOpenCallback = WebSocket.(Response) -> Unit
typealias WebSocketCloseCallback = WebSocket.(String) -> Unit
typealias WebSocketFailCallback = WebSocket.(String?, Throwable) -> Unit