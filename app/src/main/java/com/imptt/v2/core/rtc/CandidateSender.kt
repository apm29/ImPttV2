package com.imptt.v2.core.rtc

import org.webrtc.IceCandidate

/**
 *  author : ciih
 *  date : 2020/10/14 11:08 AM
 *  description :
 */
interface CandidateSender {
    fun sendCandidate(groupId:String,candidate: IceCandidate)
}