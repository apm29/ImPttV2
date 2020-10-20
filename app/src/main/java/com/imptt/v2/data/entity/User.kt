package com.imptt.v2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  author : ciih
 *  date : 2020/10/17 10:27 AM
 *  description :
 */
@Entity(tableName = "im_user")
data class User(
    @field:PrimaryKey
    @field:ColumnInfo
    val id:Long? = null,

    @field:ColumnInfo
    val name:String? = null,
)