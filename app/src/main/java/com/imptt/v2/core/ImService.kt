package com.imptt.v2.core

import android.app.Service
import android.content.Intent
import android.os.*
import android.text.TextUtils
import android.view.KeyEvent
import android.widget.Toast
import com.imptt.v2.core.media.MediaSessionHandler
import com.imptt.v2.core.messenger.connections.MessageFactory
import com.imptt.v2.core.messenger.service.ServiceMessenger
import com.imptt.v2.core.notification.NotificationFactory
import com.imptt.v2.core.websocket.SignalServiceConnector
import com.imptt.v2.core.websocket.WebSocketConnection
import com.imptt.v2.data.api.SignalServerApi
import com.imptt.v2.utils.LocalStorage
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent
import kotlin.coroutines.CoroutineContext

/**

 *  ptt对讲服务总Service
 *  author : apm29[ciih]
 *  date : 2020/9/29 4:28 PM
 *  description :
 */
class ImService : Service(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    companion object {
        const val NOTIFICATION_ID = 4310
        const val NOTIFICATION_CHANNEL_ID = "ImPttV2网络对讲服务"
        const val NOTIFICATION_CHANNEL_NAME = "ImPttV2网络对讲服务"
    }

    private val mSignalServerApi: SignalServerApi by inject()
    private var mSignalServiceConnector: SignalServiceConnector? = null
    override fun onCreate() {
        super.onCreate()
        println("ImService.onCreate")
        loginAsUser()
    }

    private fun loginAsUser() {
        launch(ioContext) {
            try {
                val userId = LocalStorage.getInstance(this@ImService).getUserId()?:"yjw"
                val baseResp = mSignalServerApi.login(userId)
                if (baseResp.success && baseResp.hasData) {
                    mSignalServiceConnector = SignalServiceConnector(
                        baseResp.data!!,
                        this@ImService
                    )
                    startForeground(
                        NOTIFICATION_ID, NotificationFactory.createNotification(
                            this@ImService,
                            NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANNEL_NAME,
                            "已登录:${baseResp.data.userId}"
                        )
                    )
                } else {
                    startForeground(
                        NOTIFICATION_ID, NotificationFactory.createNotification(
                            this@ImService,
                            NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANNEL_NAME,
                            "登录失败"
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                startForeground(
                    NOTIFICATION_ID, NotificationFactory.createNotification(
                        this@ImService,
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL_NAME,
                        "登录失败"
                    )
                )
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("ImService.onStartCommand")
        //监听媒体按键
        MediaSessionHandler.handle(packageName, intent, this) {
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
                NOTIFICATION_CHANNEL_NAME,
                "未登录"
            )
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("ImService.onBind")
        // return ServicePushToTalk()
        return ServiceMessenger.bindMessenger()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("ImService.onUnbind")
        ServiceMessenger.unbind()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        println("ImService.onDestroy")
        super.onDestroy()
        stopForeground(true)
    }

}