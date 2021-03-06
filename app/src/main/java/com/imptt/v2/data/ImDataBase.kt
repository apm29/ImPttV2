package com.imptt.v2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imptt.v2.data.converters.Converters
import com.imptt.v2.data.dao.*
import com.imptt.v2.data.entity.*
import com.imptt.v2.utils.IContextSingleton

@Database(
    entities = [Message::class, User::class, Group::class, GroupUserCrossRef::class,HiddenChannelUser::class,FileMessage::class],
    version = 18,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class ImDataBase : RoomDatabase() {

    abstract fun getMessageDao(): MessageDao

    abstract fun getUserDao(): UserDao

    abstract fun getGroupDao(): GroupDao

    abstract fun getGroupUserDao(): GroupUserDao

    abstract fun getHiddenChannelUser(): HiddenChannelUserDao

    abstract fun getFileMessageDao(): FileMessageDao

    companion object : IContextSingleton<ImDataBase>() {

        // For Singleton instantiation
        private const val DATABASE_NAME = "im_ptt"

        override fun createInstance(context: Context): ImDataBase {
            return buildDatabase(context).apply {
                this.getHiddenChannelUser().setInitialData()
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): ImDataBase {
            return Room.databaseBuilder(context, ImDataBase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}