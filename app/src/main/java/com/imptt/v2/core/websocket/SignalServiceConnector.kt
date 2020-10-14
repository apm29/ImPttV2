package com.imptt.v2.core.websocket

import android.content.Context
import android.util.Log
import com.imptt.v2.core.messenger.connections.*
import com.imptt.v2.core.messenger.service.ServiceMessenger
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.rtc.CandidateSender
import com.imptt.v2.core.rtc.WebRtcConnector
import com.imptt.v2.data.model.UserInfo
import com.imptt.v2.di.WebSocketRelated
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.StringQualifier
import org.koin.java.KoinJavaComponent.inject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import kotlin.coroutines.CoroutineContext

/**
 *  author : apm29[ciih]
 *  date : 2020/10/9 9:36 AM
 *  description :
 */
class SignalServiceConnector(userInfo: UserInfo, context: Context) : CoroutineScope,
    CandidateSender {

    companion object {
        private val TAG = SignalServiceConnector::class.java.canonicalName
    }

    /**
     * The context of this scope.
     * Context is encapsulated by the scope and used for implementation of coroutine builders that are extensions on the scope.
     * Accessing this property in general code is not recommended for any purposes except accessing the [Job] instance for advanced usages.
     *
     * By convention, should contain an instance of a [job][Job] to enforce structured concurrency.
     */
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private val mWebSocketConnection: WebSocketConnection by inject(
        WebSocketConnection::class.java
    )
    private val mWebRtcConnector: WebRtcConnector = WebRtcConnector(context, userInfo, this)
    private val mRequest: Request by inject(
        Request::class.java,
    ) {
        parametersOf(userInfo)
    }
    private val mOkHttpClient: OkHttpClient by inject(
        OkHttpClient::class.java,
        qualifier = StringQualifier(WebSocketRelated)
    )
    private var mWebSocket: WebSocket
    private val mSocketMessageFactory: WebSocketMessageFactory =
        WebSocketMessageFactory.getInstance(userInfo)

    init {
        //开启WebSocket链接
        mWebSocket = mOkHttpClient.newWebSocket(mRequest, mWebSocketConnection)

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
            mWebSocket = mOkHttpClient.newWebSocket(mRequest, mWebSocketConnection)
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
//            val from = signalMessage.from
//            val groupId = signalMessage.groupId
//            launch {
//                val sdp = mWebRtcConnector.createPeerAndOffer(from!!, groupId!!)
//                if (sdp != null && mWebRtcConnector.setLocalDescriptionAsync(from, groupId, sdp)) {
//                    sendOfferToPeers(groupId, sdp)
//                } else {
//                    Log.e(TAG, "创建Peer/Offer失败")
//                }
//            }
        }.on(WebSocketTypes.InCall) { signalMessage, _ ->
            //收到其他人呼叫信息!!
            val from = signalMessage.from!!
            val groupId = signalMessage.groupId!!
            launch {
                val sdp = mWebRtcConnector.createOfferAsync(from, groupId)
                ServiceMessenger.sendMessage(
                    MessageFactory.createInCallMessage(
                        from,
                        groupId
                    )
                )
                if (sdp != null && mWebRtcConnector.setLocalDescriptionAsync(from, groupId, sdp)) {
                    sendOfferToPeers(groupId, sdp)
                } else {
                    Log.e(TAG, "应答:创建Peer/Offer失败")
                }
            }
        }.on(WebSocketTypes.Offer) { signalMessage, _ ->
            //收到Offer
            val sdp = signalMessage.sdp!!
            val from = signalMessage.from!!
            val groupId = signalMessage.groupId!!
            launch {
                if (mWebRtcConnector.setRemoteDescriptionAsync(from, groupId, sdp)) {
                    val answerSdp = mWebRtcConnector.createAnswerAsync(from, groupId)
                    if(answerSdp!=null){
                        mWebRtcConnector.setLocalDescriptionAsync(from, groupId, answerSdp)
                        sendAnswerToPeers(groupId, answerSdp)
                    }else{
                        Log.e(TAG, "应答:创建Answer失败")
                    }
                }
            }
        }.on(WebSocketTypes.Answer) { signalMessage, _ ->
            //收到Answer
            val sdp = signalMessage.sdp!!
            val from = signalMessage.from!!
            val groupId = signalMessage.groupId!!
            launch {
                val answerSdp =SessionDescription(
                    SessionDescription.Type.ANSWER,
                    sdp.description
                )
                mWebRtcConnector.setRemoteDescriptionAsync(from, groupId, answerSdp)
            }
        }.on(WebSocketTypes.Fail){signalMessage, _ ->
            ViewMessenger.send(
                MessageFactory.createToastMessage(signalMessage.error ?: "失败")
            )
        }

        ServiceMessenger.on(MESSAGE_TYPE_CALL) {
            startCall(it.data.getString(MESSAGE_DATA_KEY_GROUP_ID)!!)
        }.on(MESSAGE_TYPE_END_CALL) {
            endCall()
        }.on(MESSAGE_TYPE_GET_GROUPS_INFO) {
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
    private fun sendOfferToPeers(groupId: String, sdp: SessionDescription) {
        mWebSocketConnection.send(
            mSocketMessageFactory.createOffer(
                groupId, sdp
            ),
            mWebSocket,
        )
    }

    //创建answer
    private fun sendAnswerToPeers(groupId: String, sdp: SessionDescription) {
        mWebSocketConnection.send(
            mSocketMessageFactory.createAnswer(
                groupId, sdp
            ),
            mWebSocket,
        )
    }

    override fun sendCandidate(groupId: String, candidate: IceCandidate) {
        mWebSocketConnection.send(
            mSocketMessageFactory.createCandidate(
                groupId, candidate
            ),
            mWebSocket,
        )
    }

}