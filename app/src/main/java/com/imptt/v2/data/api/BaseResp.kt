package com.imptt.v2.data.api

/**
 *  author : ciih
 *  date : 2020/10/9 9:52 AM
 *  description :
 */
data class BaseResp<T>(
    val code:Int = 200,
    val msg:String = "success",
    val data:T? = null
){
    val success:Boolean
        get() = code == 200

    val hasData:Boolean
        get() = data != null
}