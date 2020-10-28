package com.imptt.v2.core.ptt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kylindev.pttlib.LibConstants
import com.kylindev.pttlib.service.InterpttService
import kotlin.concurrent.thread

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        thread {
            try {
                Thread.sleep(5000)
                val serviceIntent = Intent(context, InterpttService::class.java)

                //自动启动service，在Service的实现里判断，如果是自动启动的，则自动登录
                serviceIntent.action = LibConstants.ACTION_AUTO_LAUNCH
                if (Build.VERSION.SDK_INT >= 26) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } catch (e: Exception) {
            }
        }
    }
}