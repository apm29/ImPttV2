package com.imptt.v2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 *  author : ciih
 *  date : 2020/10/17 10:27 AM
 *  description :
 */
@Entity(tableName = "im_user", indices = [Index(value = ["user_id"], unique = true)])
data class User(
    @field:PrimaryKey(autoGenerate = true)
    @field:ColumnInfo
    val id: Long? = null,

    @field:ColumnInfo(name = "user_id")
    val userId: String? = null,

    @field:ColumnInfo
    val name: String? = null
)