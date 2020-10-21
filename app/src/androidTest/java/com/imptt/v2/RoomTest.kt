package com.imptt.v2

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imptt.v2.data.ImDataBase
import com.imptt.v2.data.dao.GroupDao
import com.imptt.v2.data.dao.MessageDao
import com.imptt.v2.data.dao.UserDao
import com.imptt.v2.data.entity.Message
import org.junit.Assert.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

/**
 *  author : ciih
 *  date : 2020/10/21 9:21 AM
 *  description :
 */
@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var userDao: UserDao
    private lateinit var groupDao: GroupDao
    private lateinit var messageDao: MessageDao
    private lateinit var db: ImDataBase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ImDataBase::class.java
        ).build()
        userDao = db.getUserDao()
        groupDao = db.getGroupDao()
        messageDao = db.getMessageDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndQueryPagedMessages() {
       runBlocking {
           messageDao.insertMessage(Message(
               id = 1,
               groupId = "123",
               content = "",
               createTime = Date(),
               fromId = "1",
               fromName = "ss",
               toId = "12",
               toName = "333"
           ))
           val message = messageDao.getPagedMessageWithPager(1, 20)

           assertEquals(1,message.data.size)
           assertEquals(1,message.page)
           assertEquals(20,message.rows)
           assertEquals(1,message.total)
       }
    }
}