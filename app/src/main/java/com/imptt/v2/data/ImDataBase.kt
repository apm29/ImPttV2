package com.imptt.v2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.imptt.v2.data.dao.UserDao
import com.imptt.v2.data.dao.GroupDao
import com.imptt.v2.data.dao.MessageDao
import com.imptt.v2.data.entity.Group
import com.imptt.v2.data.entity.Message
import com.imptt.v2.data.entity.User
import com.imptt.v2.utils.IContextSingleton

@Database(entities = [Message::class,User::class,Group::class],version = 2,exportSchema = false)
abstract class ImDataBase:RoomDatabase() {

    abstract fun getMessageDao():MessageDao

    abstract fun getUserDao():UserDao

    abstract fun getGroupDao():GroupDao

    companion object: IContextSingleton<ImDataBase>(){

        // For Singleton instantiation
        private const val DATABASE_NAME = "im_ptt"

        override fun createInstance(context: Context): ImDataBase {
            return buildDatabase(context)
        }
        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): ImDataBase {
            return Room.databaseBuilder(context, ImDataBase::class.java, DATABASE_NAME)
                .build()
        }
    }
}