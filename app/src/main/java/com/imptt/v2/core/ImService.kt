package com.imptt.v2.core

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.view.KeyEvent
import android.widget.Toast
import com.imptt.v2.core.media.MediaSessionHandler
import com.imptt.v2.core.binder.ServicePushToTalk
import com.imptt.v2.core.notification.NotificationFactory

/**
 *  ptt对讲服务总Service
 *  author : ciih
 *  date : 2020/9/29 4:28 PM
 *  description :
 */
class ImService:Service() {
    companion object {
        const val NOTIFICATION_ID = 4310
        const val NOTIFICATION_CHANNEL_ID = "ImPttV2网络对讲服务"
        const val NOTIFICATION_CHANNEL_NAME = "ImPttV2网络对讲服务"
    }

    override fun onCreate() {
        println("ImService.onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("ImService.onStartCommand")
        //监听媒体按键
        MediaSessionHandler.handle(packageName,intent,this){
            val action = intent?.action
            if (action != null) {
                if (TextUtils.equals(action, Intent.ACTION_MEDIA_BUTTON)) {
                    val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                    if (keyEvent != null) {
                        if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                            when (keyEvent.keyCode) {
                                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                                    Toast.makeText(this, "播放", Toast.LENGTH_SHORT).show()
                                }
                                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                                    Toast.makeText(this, "暂停", Toast.LENGTH_SHORT).show()
                                }
                                KeyEvent.KEYCODE_MEDIA_NEXT -> {
                                    Toast.makeText(this, "下一曲", Toast.LENGTH_SHORT).show()
                                }
                                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                                    Toast.makeText(this, "上一曲", Toast.LENGTH_SHORT).show()
                                }
                            }
                            return@handle true
                        }
                    }
                }
            }
            return@handle false
        }
        startForeground(
            NOTIFICATION_ID, NotificationFactory.createNotification(
                this,
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME
            )
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("ImService.onBind")
        return ServicePushToTalk()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("ImService.onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        println("ImService.onDestroy")
        super.onDestroy()
        stopForeground(true)
    }

}