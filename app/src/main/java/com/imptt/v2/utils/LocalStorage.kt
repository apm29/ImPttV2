package com.imptt.v2.utils

import android.content.Context
import android.content.SharedPreferences
import com.imptt.v2.AppConst

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
        private const val PasswordKey = "PasswordKey"
        private const val HostKey = "HostKey"
        private const val TokenKey = "TokenKey"
        private const val OfflineReasonKey = "OfflineReasonKey"

        //value
        const val OFFLINE_REASON_KICK = "KICK"
        const val OFFLINE_REASON_OTHER = "OTHER"
    }



    private val sharedPreferences:SharedPreferences by lazy {
        context.getSharedPreferences(FileName,Context.MODE_PRIVATE)
    }

    var userId:String?
        get() {
           return sharedPreferences
                .getString(UserIdKey,null)
        }
        set(value) {
            sharedPreferences
                .edit().putString(UserIdKey,value)
                .apply()
        }

    var password:String?
        get() {
            return sharedPreferences.getString(PasswordKey,null)
        }
        set(value) {
            sharedPreferences
                .edit().putString(PasswordKey,value)
                .apply()
        }

    var host:String?
        get() {
            return  sharedPreferences.getString(HostKey,AppConst.HOST)
        }
        set(value) {
            sharedPreferences
                .edit().putString(HostKey,value)
                .apply()
        }

    var token:String?
        get() {
            return  sharedPreferences.getString(TokenKey,null)
        }
        set(value) {
            sharedPreferences
                .edit().putString(TokenKey,value)
                .apply()
        }

    var offlineReason: String?
        get() {
            return  sharedPreferences.getString(OfflineReasonKey,null)
        }
        set(value) {
            sharedPreferences
                .edit().putString(OfflineReasonKey,value)
                .apply()
        }

}