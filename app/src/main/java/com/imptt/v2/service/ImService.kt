package com.imptt.v2.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.imptt.v2.service.binder.ServicePushToTalk

/**
 *  ptt对讲服务总Service
 *  author : ciih
 *  date : 2020/9/29 4:28 PM
 *  description :
 */
class ImService:Service() {

    override fun onCreate() {
        println("ImService.onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("ImService.onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("ImService.onBind")
        return ServicePushToTalk()
    }

    override fun onDestroy() {
        println("ImService.onDestroy")
        super.onDestroy()
    }

}