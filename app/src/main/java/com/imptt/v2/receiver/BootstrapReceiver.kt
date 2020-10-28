package com.imptt.v2.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.imptt.v2.core.ImService

class BootstrapReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action === Manifest.permission.RECEIVE_BOOT_COMPLETED){
            context.startService(
                Intent(context,ImService::class.java)
            )
        }
    }
}

class MediaButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("MediaButtonReceiver.onReceive")
        println("context = [${context}], intent = [${intent}]")
    }
}