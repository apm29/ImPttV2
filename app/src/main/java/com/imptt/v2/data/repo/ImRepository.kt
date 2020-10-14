package com.imptt.v2.data.repo

import com.imptt.v2.data.dao.MessageDao

class ImRepository constructor(
    private val messageDao: MessageDao
) {
    fun getMessages() = messageDao.getMessages()
}