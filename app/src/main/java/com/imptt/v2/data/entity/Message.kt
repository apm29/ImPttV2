package com.imptt.v2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "im_message")
data class Message(
    @field:PrimaryKey
    @field:ColumnInfo
    val id:Long
)