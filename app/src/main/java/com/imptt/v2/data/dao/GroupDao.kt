package com.imptt.v2.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imptt.v2.data.entity.Group

@Dao
interface GroupDao {
    @Query("SELECT * FROM im_group")
    suspend fun getGroups(): List<Group>

    @Insert(entity = Group::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun addGroup(group: Group)

    @Query("SELECT * FROM im_group WHERE im_group.id = :id")
    suspend fun queryGroupById(id: Long):Group
}