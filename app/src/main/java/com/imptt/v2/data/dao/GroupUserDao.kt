package com.imptt.v2.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.imptt.v2.data.entity.GroupWithUsers
import com.imptt.v2.data.entity.UsersWithGroup

/**
 *  author : ciih
 *  date : 2020/10/21 8:54 AM
 *  description :
 */
@Dao
interface GroupUserDao {

    @Transaction
    @Query("SELECT * FROM im_group")
    suspend fun getGroupWithUsers():List<GroupWithUsers>

    @Transaction
    @Query("SELECT * FROM im_user")
    suspend fun getUserWithGroups():List<UsersWithGroup>


}