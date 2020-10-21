package com.imptt.v2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "im_group", indices = [Index(value = ["group_id"], unique = true)])
data class Group(
    @field:PrimaryKey(autoGenerate = true)
    @field:ColumnInfo
    val id: Long? = null,

    @field:ColumnInfo(name = "group_id")
    var groupId: String? = null,

    @field:ColumnInfo(name = "group_name")
    var groupName: String? = null,

    @field:ColumnInfo(name = "group_icon",defaultValue = "NULL",typeAffinity = ColumnInfo.TEXT)
    var groupIcon: String? = null,
)