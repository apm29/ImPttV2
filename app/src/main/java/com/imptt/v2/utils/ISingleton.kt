package com.imptt.v2.utils

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
abstract class ISingleton<T> {

    @Volatile
    private var instance: T? = null

    fun getInstance(): T {
        return instance ?: synchronized(this) {
            instance ?: createInstance().also { instance = it }
        }
    }

    abstract fun createInstance(): T
}