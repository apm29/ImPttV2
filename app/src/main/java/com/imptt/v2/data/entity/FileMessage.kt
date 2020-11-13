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
    val tUserId: String,
    @field:ColumnInfo(name = "c_id")
    @SerializedName("c_id")
    val channelId: String,
    @field:ColumnInfo(name = "content")
    val content: String,
    @field:ColumnInfo(name = "nick_name")
    @SerializedName("nick_name")
    val nickName: String,
    @field:ColumnInfo(name = "avatar")
    val avatar: String? = null,
    @field:ColumnInfo(name = "type")
    val type: Int,
    @field:ColumnInfo(name = "date")
    val date: Date,
    @field:ColumnInfo(name = "local_path")
    val localPath: String? = null,
    @field:ColumnInfo(name = "local_uri")
    val localUri: String? = null,
    @field:ColumnInfo(name = "file_name")
    val fileName: String? = null,
)