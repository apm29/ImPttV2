package com.imptt.v2.data.repo

import com.imptt.v2.data.dao.MessageDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImRepository @Inject constructor(
    private val messageDao: MessageDao
) {
    fun getMessages() = messageDao.getMessages()
}