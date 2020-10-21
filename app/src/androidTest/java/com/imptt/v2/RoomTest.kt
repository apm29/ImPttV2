package com.imptt.v2

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imptt.v2.data.ImDataBase
import com.imptt.v2.data.dao.GroupDao
import com.imptt.v2.data.dao.UserDao
import com.imptt.v2.data.entity.Group
import com.imptt.v2.data.entity.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 *  author : ciih
 *  date : 2020/10/21 9:21 AM
 *  description :
 */
@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var userDao: UserDao
    private lateinit var groupDao: GroupDao
    private lateinit var db: ImDataBase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ImDataBase::class.java
        ).build()
        userDao = db.getUserDao()
        groupDao = db.getGroupDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
       runBlocking {
           groupDao.addGroup(
               Group(
                   groupId = "209230",
                   groupName = "测试群组001",
                   groupIcon = "http://git.ciih.net/uploads/appearance/header_logo/1/ciih100100.png"
               )
           )
       }
    }
}