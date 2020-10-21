package com.imptt.v2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.imptt.v2.data.model.message.GroupType
import java.util.*

@Entity(tableName = "im_message")
data class Message(
    @field:PrimaryKey
    @field:ColumnInfo
    val id: Long,
    @field:ColumnInfo(name = "group_id")
    val groupId: String,
    @field:ColumnInfo
    val content: String,
    @field:ColumnInfo(name = "create_time")
    val createTime: Date = Date(),
    @field:ColumnInfo
    val read: Boolean = false,
    @field:ColumnInfo(name = "from_id") //对应im_user.user_id
    val fromId: String,
    @field:ColumnInfo(name = "from_name")//对应im_user.name
    val fromName: String,
    @field:ColumnInfo(name = "to_id")//对应im_user.user_id
    val toId: String,
    @field:ColumnInfo(name = "to_name")//对应im_user.name
    val toName: String,
)