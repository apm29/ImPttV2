package com.imptt.v2.core.rtc

import android.util.Log
import com.imptt.v2.BuildConfig
import org.webrtc.MediaConstraints

/**
 *  author : ciih
 *  date : 2020/10/12 1:46 PM
 *  description :
 */
object MediaConstraintFactory {

    ////webRtc定义常量////
    private const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
    private const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
    private const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
    private const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"

    private val TAG = MediaConstraintFactory.javaClass.canonicalName

    fun getAudioMediaConstraint(): MediaConstraints {
        return MediaConstraints().apply {
            if (!BuildConfig.AUDIO_PROCESS) {
                Log.d(TAG, "Disabling audio processing")
                this.mandatory.add(
                    MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false")
                )
                this.mandatory.add(
                    MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false")
                )
                this.mandatory.add(
                    MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false")
                )
                this.mandatory.add(
                    MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false")
                )
            }
            mandatory.add(
                MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
            )
            optional.add(
                MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true")
            )
        }
    }

}