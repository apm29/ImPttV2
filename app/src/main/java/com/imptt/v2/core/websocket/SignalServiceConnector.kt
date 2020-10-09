package com.imptt.v2.core.websocket

import com.imptt.v2.core.messenger.connections.MessageFactory
import com.imptt.v2.core.messenger.service.ServiceMessenger
import com.imptt.v2.data.model.UserInfo
import okhttp3.WebSocket
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

/**
 *  author : ciih
 *  date : 2020/10/9 9:36 AM
 *  description :
 */
class SignalServiceConnector(userInfo: UserInfo) {

    private val mSignalServerConnection: SignalServerConnection by inject(
        SignalServerConnection::class.java
    )

    private val mWebSocket: WebSocket by inject(
        WebSocket::class.java,
    ) {
        parametersOf(userInfo)
    }
    private val mSocketMessageFactory: WebSocketMessageFactory =
        WebSocketMessageFactory.getInstance(userInfo)

    init {
        //开启WebSocket链接
        mWebSocket.request()

        //打开事件监听
        mSignalServerConnection.whenOpen {
            println("SignalServiceConnector.whenOpen")
            registerToWebSocketServer()
        }.on(WebSocketTypes.Register) { signalMessage, _ ->
            //返回group信息
            println("groups = [${signalMessage.info?.groups}]")
            //给View发送群组信息
            ServiceMessenger.sendMessage(
                MessageFactory.createWsRegisterSuccessMessage(
                    signalMessage.info?.groups ?: arrayListOf()
                )
            )
        }
    }

    fun destroy() {
        //移除监听
        mSignalServerConnection.offAll()
        //关闭WS
        mWebSocket.close(0, "Ptt服务主动关闭")
    }

    private fun registerToWebSocketServer() {
        mSignalServerConnection.send(
            mSocketMessageFactory.createRegister(),
            mWebSocket,
        )
    }


}