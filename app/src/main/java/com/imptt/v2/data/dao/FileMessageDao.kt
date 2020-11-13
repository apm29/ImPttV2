package com.imptt.v2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imptt.v2.data.entity.FileMessage

/**
 *  author : ciih
 *  date : 2020/10/17 10:29 AM
 *  description :
 */
@Dao
interface FileMessageDao {
    @Insert(entity = FileMessage::class, onConflict = OnConflictStrategy.REPLACE)
    fun insert(messages: List<FileMessage>)

    @Query("SELECT * FROM t_file_message WHERE date >= :timeStart AND date < :timeEnd AND c_id = :channelId ORDER BY date DESC")
    fun getFileMessageWithTimeRange(timeStart: Long, timeEnd: Long, channelId: Int):List<FileMessage>

    @Query("SELECT * FROM t_file_message WHERE  date <= :timeMax AND c_id = :channelId ORDER BY date DESC  LIMIT 20 ")
    fun getFileMessageWithTimeMax(timeMax: Long, channelId: Int): List<FileMessage>

    @Query("select COUNT(*) FROM t_file_message WHERE c_id = :channelId")
    fun getCount(channelId: Int):Int
}