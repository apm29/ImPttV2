package com.imptt.v2.core.rtc

import android.util.Log
import com.imptt.v2.core.websocket.SignalServiceConnector
import org.webrtc.*
import java.util.*

/**
 *  author : ciih
 *  date : 2020/10/9 3:22 PM
 *  description : 对等连接PeerConnection的包装类,N人的群组发送对讲时当前客户端应该有N-1个Peer
 */
class Peer(
    val id: String,
    private val groupId: String,
    factory: PeerConnectionFactory,
    rtcConfiguration: PeerConnection.RTCConfiguration,
    private val signalServiceConnector: SignalServiceConnector
) : SdpObserver, PeerConnection.Observer {
    companion object {
        private val TAG = Peer::class.java.canonicalName
        const val VOLUME: Double = 80.0
        const val LOCAL_AUDIO_STREAM: String = "local_audio_stream"
        const val AUDIO_TRACK_ID: String = "audio_track_id:"
    }

    private val peerConnection = try {
        factory.createPeerConnection(
            rtcConfiguration,
            this
        )
    } catch (e: Exception) {
        Log.e(TAG, "创建对等连接失败:")
        e.printStackTrace()
        null
    }

    /**SdpObserver是来回调sdp是否创建(offer,answer)成功，是否设置描述成功(local,remote）的接口**/

    //Create{Offer,Answer}成功回调
    override fun onCreateSuccess(sdp: SessionDescription) {
        Log.d(TAG, "Peer.onCreateSuccess")
        //设置本地LocalDescription
        peerConnection?.setLocalDescription(this, sdp)
        when (sdp.type) {
            SessionDescription.Type.ANSWER -> {
                signalServiceConnector.createAnswer(groupId, sdp)
            }
            SessionDescription.Type.OFFER -> {
                signalServiceConnector.createOffer(groupId, sdp)
            }
            else -> {

            }
        }
    }

    //Set{Local,Remote}Description()成功回调
    override fun onSetSuccess() {

    }

    //Create{Offer,Answer}失败回调
    override fun onCreateFailure(p0: String?) {

    }

    //Set{Local,Remote}Description()失败回调
    override fun onSetFailure(p0: String?) {

    }

    /**SdpObserver是来回调sdp是否创建(offer,answer)成功，是否设置描述成功(local,remote）的接口**/

    //信令状态改变时候触发
    override fun onSignalingChange(state: PeerConnection.SignalingState?) {

    }

    //IceConnectionState连接接收状态改变
    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {

    }

    override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
        super.onStandardizedIceConnectionChange(newState)
    }

    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
        super.onConnectionChange(newState)
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {

    }

    //IceConnectionState网络信息获取状态改变
    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {

    }

    //新ice地址被找到触发
    override fun onIceCandidate(candidate: IceCandidate) {
        signalServiceConnector.sendCandidate(
            groupId,
            candidate
        )
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {

    }

    override fun onSelectedCandidatePairChanged(event: CandidatePairChangeEvent?) {
        super.onSelectedCandidatePairChanged(event)
    }

    override fun onAddStream(stream: MediaStream?) {

    }

    override fun onRemoveStream(stream: MediaStream?) {

    }

    override fun onDataChannel(p0: DataChannel?) {

    }

    override fun onRenegotiationNeeded() {

    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {

    }

    override fun onTrack(transceiver: RtpTransceiver?) {
        super.onTrack(transceiver)
    }

    /*******************************peer公共方法*********************************************/

    fun createOffer(sdpMediaConstraints: MediaConstraints) {
        peerConnection?.createOffer(this, sdpMediaConstraints)
    }

    fun addIceCandidate(candidate: IceCandidate) {
        peerConnection?.addIceCandidate(candidate)
    }

    fun addLocalAudioTrack(factory: PeerConnectionFactory, streamList: ArrayList<String>) {
        val audioConstraints = MediaConstraints()
        //回声消除
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googEchoCancellation",
                "true"
            )
        )
        //自动增益
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
        //高音过滤
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
        //噪音处理
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googNoiseSuppression",
                "true"
            )
        )
        val audioSource = factory.createAudioSource(audioConstraints)
        val audioTrack =
            factory.createAudioTrack("$AUDIO_TRACK_ID:${UUID.randomUUID()}", audioSource)
        val localMediaStream = factory.createLocalMediaStream(LOCAL_AUDIO_STREAM)
        localMediaStream.addTrack(audioTrack)
        peerConnection?.addTrack(audioTrack, streamList)
    }
}