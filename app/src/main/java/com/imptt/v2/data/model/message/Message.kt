package com.imptt.v2.data.model.message

import java.util.*

/**
 *  author : ciih
 *  date : 2020/10/19 2:29 PM
 *  description :
 */
data class Message(
    val id:Long,
    val contentType: MessageType = MessageType.AUDIO_OTHER,
    val content:String = "",
    val createTime: Date = Date(),
    val read:Boolean  = false,
    val fromId:String = "",
    val fromName:String = "",
    val toId:String = "",
    val toName:String = "",
    val groupType: GroupType = GroupType.GROUP
)

