package com.imptt.v2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.imptt.v2.data.entity.HiddenChannelUser

/**
 *  author : ciih
 *  date : 2020/10/21 8:54 AM
 *  description :
 */
@Dao
interface HiddenChannelUserDao {

    fun setInitialData(){
        clearData()
        add(
            HiddenChannelUser(
                1,1001018,1001
            )
        )
        add(
            HiddenChannelUser(
                2,1001019,1002
            )
        )
        add(
            HiddenChannelUser(
                3,1001020,1004
            )
        )
    }

    @Query("Delete from im_hidden where 1=1  ")
    fun clearData()

    @Insert
    fun add(data:HiddenChannelUser)

    @Query("SELECT * FROM im_hidden WHERE t_uid = :toolUserId LIMIT 1")
    fun get(toolUserId:Long):HiddenChannelUser
}