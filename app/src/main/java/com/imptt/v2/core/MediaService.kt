package com.imptt.v2.core

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.IBinder
import android.text.TextUtils
import android.view.KeyEvent
import com.imptt.v2.R
import com.imptt.v2.core.media.MediaSessionHandler
import com.imptt.v2.core.notification.NotificationFactory


class MediaService : Service() {

    companion object {
        const val NOTIFICATION_ID = 2938
        const val NOTIFICATION_CHANNEL_ID = "对讲服务"
        const val NOTIFICATION_CHANNEL_NAME = "对讲服务"
        const val ACTION_VOLUME_UP_DOWN = "ACTION_VOLUME_UP_DOWN"
        const val ACTION_VOLUME_UP_RELEASE = "ACTION_VOLUME_UP_RELEASE"
    }


    private val mMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(this, R.raw.silence10sec)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID, NotificationFactory.createNotification(
                this,
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NOTIFICATION_CHANNEL_NAME
            )
        )
        //直接创建，不需要设置setDataSource
        mMediaPlayer.isLooping = true
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer.start()

        //handle(intent)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun handle(intent: Intent?) {
        MediaSessionHandler.handle(packageName, intent, this) {
            val action = intent?.action
            println("intent = [$intent] , action = [$action]")
            if (action != null) {
                if (TextUtils.equals(action, Intent.ACTION_MEDIA_BUTTON)) {
                    val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                    if (keyEvent != null) {
                        if (keyEvent.action == KeyEvent.ACTION_DOWN && keyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                            sendBroadcast(
                                Intent(
                                    ACTION_VOLUME_UP_DOWN
                                )
                            )
                            return@handle true
                        } else if (keyEvent.action == KeyEvent.ACTION_UP && keyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                            sendBroadcast(
                                Intent(
                                    ACTION_VOLUME_UP_RELEASE
                                )
                            )
                            return@handle true
                        }
                    }
                }
            }
            false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.stop()
        mMediaPlayer.release()
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
