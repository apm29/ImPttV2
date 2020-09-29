package com.imptt.v2.core.binder

import com.imptt.v2.IServicePushToTalk

/**
 *  author : ciih
 *  date : 2020/9/29 4:39 PM
 *  description :
 */
class ServicePushToTalk:IServicePushToTalk.Stub() {
    override fun basicTypes(
        anInt: Int,
        aLong: Long,
        aBoolean: Boolean,
        aFloat: Float,
        aDouble: Double,
        aString: String?
    ) {

    }
}