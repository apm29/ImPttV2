package com.imptt.v2.core.rtc

import android.util.Log
import com.imptt.v2.BuildConfig
import org.webrtc.MediaConstraints

/**
 *  author : apm29[ciih]
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
            }else{
                this.mandatory.add(
                    MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true")
                )
                this.mandatory.add(
                    MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "true")
                )
                this.mandatory.add(
                    MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true")
                )
                this.mandatory.add(
                    MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true")
                )
            }
            // mandatory.add(
            //                MediaConstraints.KeyValuePair(
            //                    "echoCancellation", "false"
            //                )
            //            )
            //            mandatory.add(
            //                MediaConstraints.KeyValuePair(
            //                    "googEchoCancellation", "false"
            //                )
            //            )
            //            mandatory.add(
            //                MediaConstraints.KeyValuePair(
            //                    "googEchoCancellation2", "false"
            //                )
            //            )
            //            mandatory.add(
            //                MediaConstraints.KeyValuePair(
            //                    "googDAEchoCancellation", "true"
            //                )
            //            )
            mandatory.add(
                MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
            )
            optional.add(
                MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true")
            )
        }
    }

    fun audioMediaConstraint(): MediaConstraints {
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
        return audioConstraints
    }

}