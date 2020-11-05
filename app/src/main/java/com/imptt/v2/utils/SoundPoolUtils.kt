package com.imptt.v2.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import com.imptt.v2.R

/**
 *  author : ciih
 *  date : 2020/11/5 1:16 PM
 *  description :
 */
class SoundPoolUtils(private val context: Context) {

    companion object:IContextSingleton<SoundPoolUtils>(){
        override fun createInstance(context: Context): SoundPoolUtils {
            return SoundPoolUtils(context)
        }
    }
    private val mSoundPool by lazy {

        val soundPool = SoundPool.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build()
            )
            .build()
        soundPool
    }

    private val mPttStartHam by lazy {
        mSoundPool
            .load(context, R.raw.talkroom_begin_ham, 10000)
    }

    fun playPttStartHam(){
        mSoundPool.stop(mPttStartHam)
        mSoundPool.play(mPttStartHam,1F,1F,10000,0,1F)
    }
}