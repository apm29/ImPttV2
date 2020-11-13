package com.imptt.v2.data.repo

import com.imptt.v2.data.ImDataBase
import com.imptt.v2.data.dao.*
import com.imptt.v2.data.entity.Group
import com.imptt.v2.utils.LocalStorage

class ImRepository constructor(
    private val messageDao: MessageDao,
    private val groupDao: GroupDao,
    private val groupUserDao: GroupUserDao,
    private val userDao: UserDao,
    private val fileMessageDao: FileMessageDao,
    private val localStorage: LocalStorage,
    private val imDataBase: ImDataBase
) {
    suspend fun getMessages() = messageDao.getMessages()

    suspend fun getGroups() = groupDao.getGroups()

    suspend fun getGroupsWithUsers() = groupUserDao.getGroupWithUsers()

    suspend fun addGroup(group: Group) = groupDao.addGroup(group)

    suspend fun queryGroupById(id: String) = groupDao.queryGroupById(id.toLong())

    fun getFileMessageWithTimeRange(timeStart: Long, timeEnd: Long,channelId:Int) =
        fileMessageDao.getFileMessageWithTimeRange(timeStart, timeEnd,channelId)

    fun getFileMessageWithTimeMax(timeMax: Long,channelId:Int) =
        fileMessageDao.getFileMessageWithTimeMax(timeMax,channelId)

    fun getCount(channelId:Int) = fileMessageDao.getCount(channelId)

}