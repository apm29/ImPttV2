package com.imptt.v2.core.rtc

import android.util.Log
import com.imptt.v2.core.websocket.SignalServiceConnector
import org.webrtc.*
import java.lang.IllegalStateException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *  author : apm29[ciih]
 *  date : 2020/10/9 3:22 PM
 *  description : 对等连接PeerConnection的包装类,N人的群组发送对讲时当前客户端应该有N-1个Peer
 */
class Peer(
    val id: String,
    private val groupId: String,
    factory: PeerConnectionFactory,
    rtcConfiguration: PeerConnection.RTCConfiguration,
    private val candidateSender: CandidateSender
) :  PeerConnection.Observer {
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

    init {
        Log.e(TAG, "创建PeerConnection:$peerConnection")
        Log.e(TAG, "id = $id , groupId = $groupId ")
    }



    //信令状态改变时候触发
    override fun onSignalingChange(state: PeerConnection.SignalingState?) {
        Log.d(TAG, "Peer-$id.onSignalingChange : $state")
    }

    //IceConnectionState连接接收状态改变
    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
        Log.d(TAG, "Peer-$id.onIceConnectionChange : $state")
    }

    override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
        super.onStandardizedIceConnectionChange(newState)
        Log.d(TAG, "Peer-$id.onStandardizedIceConnectionChange : $newState")
    }

    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
        super.onConnectionChange(newState)
        Log.d(TAG, "Peer-$id.onConnectionChange : $newState")
    }

    override fun onIceConnectionReceivingChange(receiving: Boolean) {
        Log.d(TAG, "Peer-$id.onIceConnectionReceivingChange : $receiving")
    }

    //IceConnectionState网络信息获取状态改变
    override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) {
        Log.d(TAG, "Peer-$id.onIceGatheringChange : $state")
    }

    //新ice地址被找到触发
    override fun onIceCandidate(candidate: IceCandidate) {
        Log.d(TAG, "Peer-$id.onIceCandidate : $candidate")
        peerConnection?.addIceCandidate(candidate)
        candidateSender.sendCandidate(
            groupId,
            candidate
        )
    }

    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {
        Log.d(TAG, "Peer-$id.onIceCandidatesRemoved : $candidates")
    }

    override fun onSelectedCandidatePairChanged(event: CandidatePairChangeEvent?) {
        super.onSelectedCandidatePairChanged(event)
        Log.d(TAG, "Peer-$id.onSelectedCandidatePairChanged : $event")
    }

    override fun onAddStream(stream: MediaStream?) {
        Log.d(TAG, "Peer-$id.onAddStream : $stream")
    }

    override fun onRemoveStream(stream: MediaStream?) {
        Log.d(TAG, "Peer-$id.onRemoveStream : $stream")
    }

    override fun onDataChannel(channel: DataChannel?) {
        Log.d(TAG, "Peer-$id.onRemoveStream : $channel")
    }

    override fun onRenegotiationNeeded() {
        Log.d(TAG, "Peer-$id.onRenegotiationNeeded")
    }

    override fun onAddTrack(reveiver: RtpReceiver?, streams: Array<out MediaStream>?) {
        Log.d(TAG, "Peer-$id.onAddTrack : $reveiver $streams")
    }

    override fun onTrack(transceiver: RtpTransceiver?) {
        super.onTrack(transceiver)
        Log.d(TAG, "Peer-$id.onTrack : $transceiver")
    }

    /*******************************peer公共方法*********************************************/
    suspend fun createOfferAsync(sdpMediaConstraints: MediaConstraints): SessionDescription? {
        return suspendCoroutine {
            if (peerConnection != null) {
                try {
                    peerConnection.createOffer(
                        object : CustomSdpObserver {
                            override fun onCreateSuccess(sdp: SessionDescription?) {
                                it.resume(sdp)
                            }

                            override fun onCreateFailure(reason: String?) {
                                Log.e(TAG,reason?:"创建offer失败")
                                it.resume(null)
                            }
                        },
                        sdpMediaConstraints
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.resumeWithException(e)
                }
            } else {
                it.resumeWithException(IllegalStateException("Peer连接未建立"))
            }
        }
    }

    fun addIceCandidate(candidate: IceCandidate): Boolean {
        return peerConnection?.addIceCandidate(candidate) ?: false
    }

    /**
     * @param addLocalTrack 是否发送本地音频
     */
    fun addLocalAudioTrack(
        factory: PeerConnectionFactory,
        streamList: ArrayList<String>,
        audioConstraints: MediaConstraints,
        addLocalTrack: Boolean = true
    ) {
        val audioSource = factory.createAudioSource(audioConstraints)
        val audioTrack =
            factory.createAudioTrack("$AUDIO_TRACK_ID:${UUID.randomUUID()}", audioSource)
        val localMediaStream = factory.createLocalMediaStream(LOCAL_AUDIO_STREAM)
        localMediaStream.addTrack(audioTrack)
        audioTrack.setVolume(VOLUME)
        audioTrack.setEnabled(true)
        if (addLocalTrack) {
            try {
                peerConnection?.addTrack(audioTrack, streamList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    suspend fun setRemoteDescriptionAsync(sdp: SessionDescription): Boolean {
        return suspendCoroutine {
            if (peerConnection != null) {
                try {
                    peerConnection.setRemoteDescription(
                        object : CustomSdpObserver {
                            override fun onSetSuccess() {
                                Log.e(TAG,"设置remote offer成功")
                                it.resume(true)
                            }

                            override fun onSetFailure(reason: String?) {
                                Log.e(TAG,reason?:"设置remote offer失败")
                                it.resume(false)
                            }
                        },
                        sdp
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.resumeWithException(e)
                }
            } else {
                it.resumeWithException(IllegalStateException("Peer连接未建立"))
            }
        }
    }


    suspend fun createAnswerAsync(sdpMediaConstraints: MediaConstraints): SessionDescription? {
        return suspendCoroutine {
            if (peerConnection != null) {
                try {
                    peerConnection.createAnswer(
                        object : CustomSdpObserver {
                            override fun onCreateSuccess(sdp: SessionDescription?) {
                                it.resume(sdp)
                            }

                            override fun onCreateFailure(reason: String?) {
                                Log.e(TAG,reason?:"创建answer失败")
                                it.resume(null)
                            }
                        },
                        sdpMediaConstraints
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.resumeWithException(e)
                }
            } else {
                it.resumeWithException(IllegalStateException("Peer连接未建立"))
            }
        }
    }

    fun close() {
        peerConnection?.close()
    }

    suspend fun setLocalDescriptionAsync(sdp: SessionDescription): Boolean {
        return suspendCoroutine {
            if (peerConnection != null) {
                try {
                    println(peerConnection.signalingState())
                    peerConnection.setLocalDescription(
                        object : CustomSdpObserver {
                            override fun onSetSuccess() {
                                Log.e(TAG,"设置local offer成功")
                                it.resume(true)
                                println(peerConnection.signalingState())
                            }

                            override fun onSetFailure(reason: String?) {
                                Log.e(TAG,reason?:"设置local offer失败")
                                it.resume(false)
                                println(peerConnection.signalingState())
                            }
                        },
                        sdp
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.resumeWithException(e)
                }
            } else {
                it.resumeWithException(IllegalStateException("Peer连接未建立"))
            }
        }
    }
}