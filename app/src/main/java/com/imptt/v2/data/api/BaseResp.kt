package com.imptt.v2.data.api

/**
 *  author : apm29[ciih]
 *  date : 2020/10/9 9:52 AM
 *  description :
 */
data class BaseResp<T>(
    val status:Int = 1,
    val text:String = "success",
    val data:T? = null
){
    val success:Boolean
        get() = status == 1

    val hasData:Boolean
        get() = data != null
}