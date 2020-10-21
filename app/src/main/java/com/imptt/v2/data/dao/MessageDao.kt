package com.imptt.v2.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.imptt.v2.data.entity.Message
import com.imptt.v2.data.entity.Page

@Dao
interface MessageDao {
    @Query("SELECT * FROM im_message")
    suspend fun getMessages(): List<Message>

    @Insert
    suspend fun insertMessage(message: Message)

    @Query("SELECT im.* FROM im_message im LIMIT :rows OFFSET (:page-1) * :rows")
    suspend fun getPagedMessage(page: Int, rows: Int): List<Message>

    @Query("SELECT COUNT(1) FROM im_message")
    suspend fun getMessageTotal(): Int

    suspend fun getPagedMessageWithPager(page: Int, rows: Int): Page<Message> {
        return Page(
            getMessageTotal(),
            getPagedMessage(page, rows),
            page, rows
        )
    }
}

