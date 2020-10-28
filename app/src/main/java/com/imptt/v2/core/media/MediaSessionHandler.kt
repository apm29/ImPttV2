package com.imptt.v2.core.media

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.KeyEvent
import androidx.media.session.MediaButtonReceiver

/**
 *  author : apm29[ciih]
 *  date : 2020/9/29 4:46 PM
 *  description :
 */
object MediaSessionHandler {
    //处理媒体按键监听
    fun handle(packageName:String,intent: Intent?,context: Context,onMediaIntent:(Intent)->Boolean){
        val mbr = ComponentName(packageName, MediaButtonReceiver::class.java.name)
        val mMediaSession = MediaSessionCompat(context, "mbr", mbr, null)
        //一定要设置
        mMediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        if (!mMediaSession.isActive) {
            mMediaSession.isActive = true
        }
        mMediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onMediaButtonEvent(intent: Intent): Boolean {
                Log.e("MediaSessionHandler","action:${intent.action},key_event:${intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)}")
                return onMediaIntent.invoke(intent)
            }
        })
        mMediaSession.isActive = true
        MediaButtonReceiver.handleIntent(mMediaSession, intent)
    }
}