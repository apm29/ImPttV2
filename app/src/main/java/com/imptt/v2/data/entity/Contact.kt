package com.imptt.v2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  author : ciih
 *  date : 2020/10/17 10:27 AM
 *  description :
 */
@Entity(tableName = "im_contact")
data class Contact(
    @field:PrimaryKey
    @field:ColumnInfo
    val id:Long? = null,
    val group:String? = null,
    val name:String? = null,
)