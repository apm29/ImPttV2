package com.imptt.v2.core.websocket

import android.content.Context
import android.util.Log
import com.imptt.v2.core.messenger.connections.*
import com.imptt.v2.core.messenger.service.ServiceMessenger
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.rtc.WebRtcConnector
import com.imptt.v2.data.model.UserInfo
import com.imptt.v2.di.WebSocketRelated
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.StringQualifier
import org.koin.java.KoinJavaComponent.inject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

/**
 *  author : apm29[ciih]
 *  date : 2020/10/9 9:36 AM
 *  description :
 */
class SignalServiceConnector(userInfo: UserInfo, context: Context) {

    private val mWebSocketConnection: WebSocketConnection by inject(
        WebSocketConnection::class.java
    )
    private val mWebRtcConnector: WebRtcConnector = WebRtcConnector(context, userInfo, this)
    private val mRequest: Request by inject(
        Request::class.java,
    ){
        parametersOf(userInfo)
    }
    private val mOkHttpClient: OkHttpClient by inject(
        OkHttpClient::class.java,
        qualifier = StringQualifier(WebSocketRelated)
    )
    private var mWebSocket:WebSocket
    private val mSocketMessageFactory: WebSocketMessageFactory =
        WebSocketMessageFactory.getInstance(userInfo)

    init {
        //开启WebSocket链接
        mWebSocket = mOkHttpClient.newWebSocket(mRequest,mWebSocketConnection)

        //打开事件监听
        mWebSocketConnection.whenOpen {
            println("SignalServiceConnector.whenOpen")
            registerToWebSocketServer()
        }.whenClosing {
            ServiceMessenger.sendMessage(
                MessageFactory.createToastMessage("与信令服务器连接断开")
            )
        }.whenFail { penddingIds, throwable ->
            ServiceMessenger.sendMessage(
                MessageFactory.createToastMessage("与信令服务器连接失败:${penddingIds?.joinToString(",") ?: throwable.localizedMessage}")
            )
            mWebSocket = mOkHttpClient.newWebSocket(mRequest,mWebSocketConnection)
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
            Log.e("STEP","02: 收到CALLEE名单")
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
            Log.e("STEP","02-1: 收到CALL,创建应答端Peer")
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
            Log.e("STEP","06: 应答端 RECEIVE OFFER from ${signalMessage.from} , sdp = SDP@${signalMessage.sdp?.description?.hashCode()}")
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
            Log.e("STEP","09: RECEIVE ANSWER")
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
        }.on(MESSAGE_TYPE_GET_GROUPS_INFO){
            mWebSocketConnection.send(
                mSocketMessageFactory.createRegister(),
                mWebSocket
            )
        }
    }


    fun destroy() {
        //移除监听
        mWebSocketConnection.offAll()
        //关闭WS
        mWebSocket.close(0, "Ptt服务主动关闭")
    }

    private fun startCall(groupId: String) {
        Log.e("STEP","01: CALL")
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
        Log.e("STEP","05: SEND OFFER sdp = SDP@${sdp.description.hashCode()}")
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