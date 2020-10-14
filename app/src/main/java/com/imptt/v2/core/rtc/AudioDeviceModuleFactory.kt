package com.imptt.v2.core.rtc

import android.content.Context
import android.util.Log
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule

/**
 *  author : ciih
 *  date : 2020/10/14 8:41 AM
 *  description :
 */
object AudioDeviceModuleFactory {
    private val TAG = AudioDeviceModuleFactory::class.java.canonicalName
    //创建音频模式JavaAudioDevice
    fun createJavaAudioDevice(appContext: Context): AudioDeviceModule {
        // Set audio record error callbacks.
        val audioRecordErrorCallback: JavaAudioDeviceModule.AudioRecordErrorCallback = object :
            JavaAudioDeviceModule.AudioRecordErrorCallback {
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
        val audioTrackErrorCallback: JavaAudioDeviceModule.AudioTrackErrorCallback = object :
            JavaAudioDeviceModule.AudioTrackErrorCallback {
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
            .setUseHardwareAcousticEchoCanceler(false)
            .setUseHardwareNoiseSuppressor(false)
            .setAudioRecordErrorCallback(audioRecordErrorCallback)
            .setAudioTrackErrorCallback(audioTrackErrorCallback)
            .createAudioDeviceModule()
    }
    
}