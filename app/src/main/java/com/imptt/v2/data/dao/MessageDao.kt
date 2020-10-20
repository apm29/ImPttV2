package com.imptt.v2.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.imptt.v2.data.entity.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM im_message")
    suspend fun getMessages():List<Message>
}

