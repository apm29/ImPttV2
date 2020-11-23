package com.imptt.v2.data.model.v2

import com.google.gson.annotations.SerializedName

/**
 *  author : ciih
 *  date : 2020/11/16 1:34 PM
 *  description :
 */
data class ChannelUser(
    @SerializedName("t_uid")
    val userId:String,
    @SerializedName("nick_name")
    val nickName:String
)