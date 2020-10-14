package com.imptt.v2.core.rtc

import android.content.Context
import android.util.Log
import com.imptt.v2.core.websocket.SignalServiceConnector
import com.imptt.v2.data.model.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.webrtc.*
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.voiceengine.WebRtcAudioUtils
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 *  author : apm29[ciih]
 *  date : 2020/10/9 3:30 PM
 *  description :
 */
class WebRtcConnector(
    appContext: Context,
    private val userInfo: UserInfo,
    private val candidateSender: CandidateSender
) : CoroutineScope {

    /**
     * The context of this scope.
     * Context is encapsulated by the scope and used for implementation of coroutine builders that are extensions on the scope.
     * Accessing this property in general code is not recommended for any purposes except accessing the [Job] instance for advanced usages.
     *
     * By convention, should contain an instance of a [job][Job] to enforce structured concurrency.
     */
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {
        private val TAG = WebRtcConnector::class.java.canonicalName

        private const val VIDEO_FLEXFEC_FIELDTRIAL =
            "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/"
        private const val VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/"
        private const val DISABLE_WEBRTC_AGC_FIELDTRIAL =
            "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/"
    }

    private val peers: HashMap<String, Peer> = hashMapOf()

    //IceServer集合 用于构建PeerConnection
    private val iceServers = LinkedList<IceServer>()
    private val streamList: ArrayList<String> = arrayListOf()
    val factory: PeerConnectionFactory
    private val rtcConfig: RTCConfiguration

    //PeerConnect sdp约束
    private val sdpMediaConstraints: MediaConstraints
        get() = MediaConstraintFactory.getAudioMediaConstraint()

    private val eglBase: EglBase by lazy { EglBase.create() }
    private val adm: AudioDeviceModule = AudioDeviceModuleFactory.createJavaAudioDevice(appContext)

    init {
        //创建webRtc连接工厂类
        //音频模式

        //PeerConnectionFactory.initialize
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(appContext)
                .setFieldTrials("$VIDEO_FLEXFEC_FIELDTRIAL$VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL$DISABLE_WEBRTC_AGC_FIELDTRIAL")
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
        //构建PeerConnectionFactory
        val options = PeerConnectionFactory.Options()
        factory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBase.eglBaseContext,
                    true,
                    true
                )
            )
            .setAudioDeviceModule(adm)
            .setAudioDecoderFactoryFactory(
                BuiltinAudioDecoderFactoryFactory()
            )
            .createPeerConnectionFactory()
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
        //创建IceServers参数
        iceServers.add(IceServer.builder("stun:stun2.1.google.com:19302").createIceServer())
        iceServers.add(IceServer.builder("stun:23.21.150.121").createIceServer())
        iceServers.add(
            IceServer.builder("turn:numb.viagenie.ca")
                .setUsername("webrtc@live.com").setPassword("muazkh").createIceServer()
        )
        //创建RTCConfiguration参数
        rtcConfig = RTCConfiguration(iceServers)
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy =
            PeerConnection.ContinualGatheringPolicy.GATHER_ONCE
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        // Enable DTLS for normal calls and disable for loopback calls.
        rtcConfig.enableDtlsSrtp = false
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
    }

    //获取/新建peerConnection
    private fun getOrCreatePeer(id: String, groupId: String): Peer {
        return peers[id]
            ?: Peer(id, groupId, factory, rtcConfig, candidateSender).also {
                peers[id] = it
            }
    }

    //添加本地音频track
    private fun Peer.addLocalAudioTrack(addLocalTrack: Boolean = true) {
        this.addLocalAudioTrack(factory, streamList, sdpMediaConstraints,addLocalTrack)
    }


    //创立对等连接s,添加音频
    fun createPeersByGroupUserIds(groupId: String, groupUsers: List<String>) {
        println("WebRtcConnector.createPeersByGroupUserIds")
        println("groupId = [${groupId}], groupUsers = [${groupUsers}]")
        peers.clear()
        groupUsers.forEach { id ->
            getOrCreatePeer(id, groupId).also { peer ->
                peer.addLocalAudioTrack()
            }
        }
    }

    //交换iceCandidate
    fun addIceCandidate(candidate: IceCandidate, from: String, groupId: String) {
        println("WebRtcConnector.addIceCandidate")
        println("candidate = [${candidate}], from = [${from}], groupId = [${groupId}]")
        getOrCreatePeer(from, groupId).addIceCandidate(candidate)
    }

    //p2p时有新的用户加入
    suspend fun createOfferAsync(from: String, groupId: String): SessionDescription? {
        println("WebRtcConnector.createOfferAsync")
        println("from = [${from}], groupId = [${groupId}]")
        val peer = getOrCreatePeer(from, groupId)
        peer.addLocalAudioTrack(false)
        return try {
            peer.createOfferAsync(sdpMediaConstraints)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //p2p时有新的用户加入
    suspend fun createAnswerAsync(from: String, groupId: String): SessionDescription? {
        val peer = getOrCreatePeer(from, groupId)
        return try {
            peer.createAnswerAsync(sdpMediaConstraints)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun setRemoteDescriptionAsync(
        from: String,
        groupId: String,
        sdp: SessionDescription
    ): Boolean {
        println("WebRtcConnector.setRemoteDescriptionAsync")
        println("from = [${from}], groupId = [${groupId}], sdp = [${sdp}]")
        val peer = getOrCreatePeer(from, groupId)
        return try {
            peer.setRemoteDescriptionAsync(sdp)
        } catch (e: Exception) {
            false
        }
    }

    fun endCall() {
        println("WebRtcConnector.endCall")
        peers.forEach {
            it.value.close()
        }
        peers.clear()
    }

    suspend fun setLocalDescriptionAsync(
        from: String,
        groupId: String, sdp: SessionDescription
    ): Boolean {
        val peer = getOrCreatePeer(from, groupId)
        return try {
            peer.setLocalDescriptionAsync(sdp)
        } catch (e: Exception) {
            false
        }
    }

}