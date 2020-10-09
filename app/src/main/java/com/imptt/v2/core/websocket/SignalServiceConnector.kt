package com.imptt.v2.core.websocket

import android.content.Context
import com.imptt.v2.core.messenger.connections.MessageFactory
import com.imptt.v2.core.messenger.service.ServiceMessenger
import com.imptt.v2.core.rtc.WebRtcConnector
import com.imptt.v2.data.model.UserInfo
import okhttp3.WebSocket
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

/**
 *  author : ciih
 *  date : 2020/10/9 9:36 AM
 *  description :
 */
class SignalServiceConnector(userInfo: UserInfo, context: Context) {

    private val mSignalServerConnection: SignalServerConnection by inject(
        SignalServerConnection::class.java
    )

    private val mWebRtcConnector: WebRtcConnector = WebRtcConnector(context,userInfo, this)

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
        }.on(WebSocketTypes.Call) { signalMessage, _ ->
            //呼叫成功,返回群组用户列表
            val groupUsers = signalMessage.groupUsers
            val groupId = signalMessage.groupId
            //rtcConnector创建对应的peer
            mWebRtcConnector.createPeersByGroupUserIds(groupId!!, groupUsers)
        }.on(WebSocketTypes.Candidate) { signalMessage, _ ->
            val candidate = signalMessage.candidate
            val from = signalMessage.from
            val groupId = signalMessage.groupId
            mWebRtcConnector.addIceCandidate(candidate!!, from!!, groupId!!)
        }.on(WebSocketTypes.Joined){
            signalMessage, _ ->
            val from = signalMessage.from
            val groupId = signalMessage.groupId
            mWebRtcConnector.createNewPeer(from!!,groupId!!)
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

    /************************************peer侧调用******************************************/
    //创建offer
    fun createOffer(groupId: String, sdp: SessionDescription) {
        mSignalServerConnection.send(
            mSocketMessageFactory.createOffer(
                groupId, sdp
            ),
            mWebSocket,
        )
    }

    //创建offer
    fun createAnswer(groupId: String, sdp: SessionDescription) {
        mSignalServerConnection.send(
            mSocketMessageFactory.createAnswer(
                groupId, sdp
            ),
            mWebSocket,
        )
    }

    fun sendCandidate(groupId: String, candidate: IceCandidate) {
        mSignalServerConnection.send(
            mSocketMessageFactory.createCandidate(
                groupId, candidate
            ),
            mWebSocket,
        )
    }

}