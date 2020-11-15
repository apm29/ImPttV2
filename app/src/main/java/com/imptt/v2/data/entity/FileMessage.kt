package com.imptt.v2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 *  author : ciih
 *  date : 2020/11/12 8:11 PM
 *  description :
 */
@Entity(tableName = "t_file_message")
data class FileMessage(
    @field:PrimaryKey
    @field:ColumnInfo
    val id: String,
    @field:ColumnInfo(name = "t_uid")
    @SerializedName("t_uid")
    var tUserId: String,
    @field:ColumnInfo(name = "c_id")
    @SerializedName("c_id")
    var channelId: String,
    @field:ColumnInfo(name = "content")
    var content: String,
    @field:ColumnInfo(name = "nick_name")
    @SerializedName("nick_name")
    var nickName: String,
    @field:ColumnInfo(name = "avatar")
    var avatar: String? = null,
    @field:ColumnInfo(name = "type")
    var type: Int,
    @field:ColumnInfo(name = "date")
    var date: Date,
    @field:ColumnInfo(name = "local_path")
    var localPath: String? = null,
    @field:ColumnInfo(name = "local_uri")
    var localUri: String? = null,
    @SerializedName("file_name")
    @field:ColumnInfo(name = "file_name")
    var fileName: String? = null,
)