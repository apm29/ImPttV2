package com.imptt.v2.core.rtc

import android.content.Context
import android.util.Log
import com.imptt.v2.BuildConfig
import com.imptt.v2.core.websocket.SignalServiceConnector
import com.imptt.v2.data.model.UserInfo
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnectionFactory
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback
import org.webrtc.audio.JavaAudioDeviceModule.AudioTrackErrorCallback
import java.util.*

/**
 *  author : ciih
 *  date : 2020/10/9 3:30 PM
 *  description :
 */
class WebRtcConnector(
    appContext: Context,
    private val userInfo: UserInfo,
    private val signalServiceConnector: SignalServiceConnector
) {

    companion object {
        private val TAG = WebRtcConnector::class.java.canonicalName

        ////webRtc定义常量////
        private const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        private const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        private const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        private const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
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
    //PeerConnect 音频约束
    private val audioConstraints: MediaConstraints
    //PeerConnect sdp约束
    private val sdpMediaConstraints: MediaConstraints
    init {
        //创建webRtc连接工厂类
        //音频模式
        val adm: AudioDeviceModule = createJavaAudioDevice(appContext)
        //PeerConnectionFactory.initialize
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(appContext)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
        //构建PeerConnectionFactory
        val options = PeerConnectionFactory.Options()
        factory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setAudioDeviceModule(adm)
            .createPeerConnectionFactory()

        //创建IceServers参数
        iceServers.add(IceServer.builder("stun:stun.xten.com").createIceServer())

        //创建RTCConfiguration参数
        rtcConfig = RTCConfiguration(iceServers)
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy =
            PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        // Enable DTLS for normal calls and disable for loopback calls.
        rtcConfig.enableDtlsSrtp = false
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN

        // 音频约束
        audioConstraints = MediaConstraints()
        // added for audio performance measurements
        if(BuildConfig.AUDIO_PROCESS){
            Log.d(
                TAG,
                "Disabling audio processing"
            )
            audioConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_ECHO_CANCELLATION_CONSTRAINT,
                    "false"
                )
            )
            audioConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT,
                    "false"
                )
            )
            audioConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_HIGH_PASS_FILTER_CONSTRAINT,
                    "false"
                )
            )
            audioConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_NOISE_SUPPRESSION_CONSTRAINT,
                    "false"
                )
            )
        }
        //SDP约束 createOffer  createAnswer
        sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpMediaConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", "true"
            )
        )
        sdpMediaConstraints.optional.add(
            MediaConstraints.KeyValuePair(
                "DtlsSrtpKeyAgreement",
                "true"
            )
        )
    }

    //创建音频模式JavaAudioDevice
    private fun createJavaAudioDevice(appContext: Context): AudioDeviceModule {
        // Set audio record error callbacks.
        val audioRecordErrorCallback: AudioRecordErrorCallback = object : AudioRecordErrorCallback {
            override fun onWebRtcAudioRecordInitError(errorMessage: String) {
                Log.e(
                    TAG,
                    "onWebRtcAudioRecordInitError: $errorMessage"
                )
            }

            override fun onWebRtcAudioRecordStartError(
                errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode, errorMessage: String
            ) {
                Log.e(
                    TAG,
                    "onWebRtcAudioRecordStartError: $errorCode. $errorMessage"
                )
            }

            override fun onWebRtcAudioRecordError(errorMessage: String) {
                Log.e(
                    TAG,
                    "onWebRtcAudioRecordError: $errorMessage"
                )
            }
        }
        val audioTrackErrorCallback: AudioTrackErrorCallback = object : AudioTrackErrorCallback {
            override fun onWebRtcAudioTrackInitError(errorMessage: String) {
                Log.e(
                    TAG,
                    "onWebRtcAudioTrackInitError: $errorMessage"
                )
            }

            override fun onWebRtcAudioTrackStartError(
                errorCode: JavaAudioDeviceModule.AudioTrackStartErrorCode, errorMessage: String
            ) {
                Log.e(
                    TAG,
                    "onWebRtcAudioTrackStartError: $errorCode. $errorMessage"
                )
            }

            override fun onWebRtcAudioTrackError(errorMessage: String) {
                Log.e(
                    TAG,
                    "onWebRtcAudioTrackError: $errorMessage"
                )
            }
        }
        return JavaAudioDeviceModule.builder(appContext) //.setSamplesReadyCallback(saveRecordedAudioToFile)
            .setUseHardwareAcousticEchoCanceler(true)
            .setUseHardwareNoiseSuppressor(true)
            .setAudioRecordErrorCallback(audioRecordErrorCallback)
            .setAudioTrackErrorCallback(audioTrackErrorCallback)
            .createAudioDeviceModule()
    }

    //获取/新建peerConnection
    private fun getOrCreatePeer(id:String, groupId: String): Peer {
        return peers[id]?:Peer(id,groupId,factory,rtcConfig,signalServiceConnector).also {
            peers[id] = it
        }
    }

    //添加本地音频track
    private fun Peer.addLocalAudioTrack() {
        this.addLocalAudioTrack(factory,streamList)
    }


    //创立对等连接s,添加音频
    fun createPeersByGroupUserIds(groupId: String, groupUsers: List<String>) {
        peers.clear()
        groupUsers.forEach { id ->
            getOrCreatePeer(id,groupId).also {
                it.createOffer(sdpMediaConstraints)
                it.addLocalAudioTrack()
            }
        }
    }

    //交换iceCandidate
    fun addIceCandidate(candidate: IceCandidate, from: String, groupId: String) {
        getOrCreatePeer(from,groupId).addIceCandidate(candidate)
    }

    //p2p时有新的用户加入
    fun createNewPeer(from: String, groupId: String) {
        getOrCreatePeer(from,groupId)
    }




}