package com.imptt.v2.data.repo

import com.imptt.v2.data.dao.GroupDao
import com.imptt.v2.data.dao.GroupUserDao
import com.imptt.v2.data.dao.MessageDao
import com.imptt.v2.data.dao.UserDao
import com.imptt.v2.data.entity.Group

class ImRepository constructor(
    private val messageDao: MessageDao,
    private val groupDao: GroupDao,
    private val groupUserDao: GroupUserDao,
    private val userDao: UserDao
) {
    suspend fun getMessages() = messageDao.getMessages()

    suspend fun getGroups() = groupDao.getGroups()

    suspend fun getGroupsWithUsers() = groupUserDao.getGroupWithUsers()

    suspend fun addGroup(group:Group) =  groupDao.addGroup(group)

    suspend fun queryGroupById(id: String) = groupDao.queryGroupById(id.toLong())
}