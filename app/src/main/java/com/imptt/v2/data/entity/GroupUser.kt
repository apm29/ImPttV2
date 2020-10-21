package com.imptt.v2.data.entity

import androidx.room.*

/**
 *  author : ciih
 *  date : 2020/10/21 8:49 AM
 *  description : 定义群组与成员的关系,多对多
 */
@Entity(
    tableName = "im_group_user",
    primaryKeys = ["group_id", "user_id"],
    indices = [Index(value = ["group_id", "user_id"])]
)
data class GroupUserCrossRef(
    @ColumnInfo(name = "group_id")
    val groupId: String,
    @ColumnInfo(name = "user_id")
    val userId: String
)

data class GroupWithUsers(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "user_id",
        associateBy = Junction(GroupUserCrossRef::class)
    )
    val users: List<User>
)

data class UsersWithGroup(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "group_id",
        associateBy = Junction(GroupUserCrossRef::class)
    )
    val groups: List<Group>
)
