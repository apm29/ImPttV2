package com.imptt.v2.core.rtc

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

/**
 *  author : ciih
 *  date : 2020/10/12 8:46 AM
 *  description :
 */
interface CustomSdpObserver:SdpObserver {
    override fun onCreateSuccess(sdp: SessionDescription?) {

    }

    override fun onSetSuccess() {
    }

    override fun onCreateFailure(reason: String?) {
    }

    override fun onSetFailure(reason: String?) {
    }
}