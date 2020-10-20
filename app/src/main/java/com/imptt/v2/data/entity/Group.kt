package com.imptt.v2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "im_group")
data class Group(
    @field:PrimaryKey(autoGenerate = true)
    @field:ColumnInfo
    val id: Long? = null,

    @field:ColumnInfo(name = "group_name")
    var groupName: String? = null,

    @field:ColumnInfo(name = "group_icon")
    var groupIcon: String? = null,
)