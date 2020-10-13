package com.imptt.v2.utils

import android.content.Context

/**
 *  author : apm29[ciih]
 *  date : 2020/10/10 10:02 AM
 *  description :
 */
class LocalStorage private constructor(private val context: Context){

    companion object:IContextSingleton<LocalStorage>(){
        override fun createInstance(context: Context): LocalStorage {
            return LocalStorage(context)
        }
        private const val FileName = "user_info"
        private const val UserIdKey = "user_id"
    }



    fun setUserId(userId:String){
        context.getSharedPreferences(FileName,Context.MODE_PRIVATE)
            .edit().putString(UserIdKey,userId)
            .apply()
    }

    fun getUserId():String?{
        return context.getSharedPreferences(FileName,Context.MODE_PRIVATE)
            .getString(UserIdKey,null)
    }

}