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

    @field:ColumnInfo
    val content: String,
    @field:ColumnInfo
    val createTime: Date = Date(),
    @field:ColumnInfo
    val read: Boolean = false,
    @field:ColumnInfo
    val fromId: String,
    @field:ColumnInfo
    val fromName: String,
    @field:ColumnInfo
    val toId: String,
    @field:ColumnInfo
    val toName: String,
)