package com.imptt.v2.utils

import android.content.Context

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
abstract class IContextSingleton<T> {

    @Volatile
    private var instance: T? = null

    fun getInstance(context: Context): T {
        return instance ?: synchronized(this) {
            instance ?: createInstance(context).also { instance = it }
        }
    }

    abstract fun createInstance(context: Context): T
}