package com.imptt.v2.core.websocket

import android.content.Context
import android.util.Log
import com.imptt.v2.core.messenger.connections.MESSAGE_DATA_KEY_GROUP_ID
import com.imptt.v2.core.messenger.connections.MESSAGE_TYPE_CALL
import com.imptt.v2.core.messenger.connections.MESSAGE_TYPE_END_CALL
import com.imptt.v2.core.messenger.connections.MessageFactory
import com.imptt.v2.core.messenger.service.ServiceMessenger
import com.imptt.v2.core.messenger.view.ViewMessenger
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

    private val mWebSocketConnection: WebSocketConnection by inject(
        WebSocketConnection::class.java
    )

    private val mWebRtcConnector: WebRtcConnector = WebRtcConnector(context, userInfo, this)

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
        mWebSocketConnection.whenOpen {
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
        }.on(WebSocketTypes.Joined) { signalMessage, _ ->
            val from = signalMessage.from
            val groupId = signalMessage.groupId
            mWebRtcConnector.createCalleeNewPeer(from!!, groupId!!)
        }.on(WebSocketTypes.InCall) { signalMessage, _ ->
            //收到其他人呼叫信息
            val from = signalMessage.from
            val groupId = signalMessage.groupId
            mWebRtcConnector.createCalleeNewPeer(from!!, groupId!!)
            ServiceMessenger.sendMessage(
                MessageFactory.createInCallMessage(
                    from,
                    groupId
                )
            )
        }.on(WebSocketTypes.Offer) { signalMessage, _ ->
            //收到Offer
            val sdp = signalMessage.sdp
            val from = signalMessage.from
            val groupId = signalMessage.groupId
            mWebRtcConnector.setRemoteDescriptionAndCreateAnswer(
                from!!,
                groupId!!,
                sdp!!
            )
        }.on(WebSocketTypes.Answer) { signalMessage, _ ->
            //收到Answer
            val sdp = signalMessage.sdp
            val from = signalMessage.from
            val groupId = signalMessage.groupId
            mWebRtcConnector.setRemoteDescription(
                from!!,
                groupId!!,
                sdp!!
            )
        }.on(WebSocketTypes.Fail){signalMessage, _ ->
            ViewMessenger.send(
                MessageFactory.createToastMessage(signalMessage.error?:"失败")
            )
        }

        ServiceMessenger.on(MESSAGE_TYPE_CALL) {
            startCall(it.data.getString(MESSAGE_DATA_KEY_GROUP_ID)!!)
        }.on(MESSAGE_TYPE_END_CALL) {
            endCall()
        }
    }


    fun destroy() {
        //移除监听
        mWebSocketConnection.offAll()
        //关闭WS
        mWebSocket.close(0, "Ptt服务主动关闭")
    }

    private fun startCall(groupId: String) {
        mWebSocketConnection.send(
            mSocketMessageFactory.createCall(groupId),
            mWebSocket,
        )
    }

    private fun endCall() {
        mWebRtcConnector.endCall()
    }

    private fun registerToWebSocketServer() {
        mWebSocketConnection.send(
            mSocketMessageFactory.createRegister(),
            mWebSocket,
        )
    }

    /************************************peer侧调用******************************************/
    //创建offer
    fun sendOfferToPeers(groupId: String, sdp: SessionDescription) {
        mWebSocketConnection.send(
            mSocketMessageFactory.createOffer(
                groupId, sdp
            ),
            mWebSocket,
        )
    }

    //创建answer
    fun sendAnswerToPeers(groupId: String, sdp: SessionDescription) {
        mWebSocketConnection.send(
            mSocketMessageFactory.createAnswer(
                groupId, sdp
            ),
            mWebSocket,
        )
    }

    fun sendCandidate(groupId: String, candidate: IceCandidate) {
        mWebSocketConnection.send(
            mSocketMessageFactory.createCandidate(
                groupId, candidate
            ),
            mWebSocket,
        )
    }

}