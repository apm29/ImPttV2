package com.imptt.v2.utils

import android.content.Context
import com.imptt.v2.data.model.UserInfo

/**
 *  author : apm29[ciih]
 *  date : 2020/9/11 3:38 PM
 *  description :
 *
 *  class TestSingleton private constructor() {
 *      companion object : ISingleton<TestSingleton>() {
 *          override fun createInstance(): TestSingleton {
 *              return TestSingleton()
 *          }
 *      }
 *  }
 *  使用：
 *  TestSingleton.getInstance()
 */
abstract class IUserSingleton<T> {

    @Volatile
    private var instance: T? = null

    fun getInstance(user: UserInfo): T {
        return instance ?: synchronized(this) {
            instance ?: createInstance(user).also { instance = it }
        }
    }

    abstract fun createInstance(user: UserInfo): T
}