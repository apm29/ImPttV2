package com.imptt.v2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  author : ciih
 *  date : 2020/11/12 11:13 AM
 *  description :
 */
@Entity(tableName = "im_hidden")
data  class HiddenChannelUser (
    @field:PrimaryKey
    @field:ColumnInfo
    val id: Long,

    @field:ColumnInfo(name = "t_uid")
    var toolUserId: Long,

    @field:ColumnInfo(name = "c_id")
    var channelId: Long,
)
