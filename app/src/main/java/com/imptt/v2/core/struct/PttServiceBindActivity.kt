package com.imptt.v2.core.struct

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.imptt.v2.R
import com.imptt.v2.core.ptt.PttObserver
import com.imptt.v2.view.HostActivity
import com.kylindev.pttlib.service.InterpttService
import com.kylindev.pttlib.service.InterpttService.LocalBinder

/**
 *  author : apm29[ciih]
 *  date : 2020/9/29 4:22 PM
 *  description :
 */
open class PttServiceBindActivity : AppCompatActivity(), ServiceConnection {

    var mService: InterpttService? = null

    private val serviceBinderProxy: PttServiceBinderProxy by lazy {
        PttServiceBinderProxy(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        serviceBinderProxy.ensureCreated()
        super.onCreate(savedInstanceState)
    }

    private val connectListeners:ArrayList<(InterpttService) -> Unit> = arrayListOf()

    open fun tryBindService(onConnected: ((InterpttService) -> Unit)? = null) {
        onConnected?.let { connectListeners.add(it) }
        serviceBinderProxy.tryBind()
    }


    /**
     * Called when a connection to the Service has been established, with
     * the {@link android.os.IBinder} of the communication channel to the
     * Service.
     *
     * <p class="note"><b>Note:</b> If the system has started to bind your
     * client app to a service, it's possible that your app will never receive
     * this callback. Your app won't receive a callback if there's an issue with
     * the service, such as the service crashing while being created.
     *
     * @param name The concrete component name of the service that has
     * been connected.
     *
     * @param service The IBinder of the Service's communication channel,
     * which you can now make calls on.
     */
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        //this.pttService = service as ImService.ServicePushToTalk?
        println("PttServiceBindActivity.onServiceConnected")
        val localBinder = service as LocalBinder
        mService = localBinder.service
        onServiceBind(mService!!)
        connectListeners.forEach {
            it.invoke(mService!!)
        }
    }

    open fun onServiceBind(service: InterpttService) {

    }

    /**
     * Called when a connection to the Service has been lost.  This typically
     * happens when the process hosting the service has crashed or been killed.
     * This does <em>not</em> remove the ServiceConnection itself -- this
     * binding to the service will remain active, and you will receive a call
     * to {@link #onServiceConnected} when the Service is next running.
     *
     * @param name The concrete component name of the service whose
     * connection has been lost.
     */
    override fun onServiceDisconnected(name: ComponentName?) {
        //this.pttService = null
        println("PttServiceBindActivity.onServiceDisconnected")
        //此函数只有在service被异常停止时才会调用，如被系统或其他软件强行停止
        finish()
    }

    override fun onDestroy() {
        // Unbind to service
        //为保证service不被杀死，activity在back按键时，只pause，不destroy。
        //那么，如果发现destroy，则应检查是否是用户关闭的。如果不是，则应重新启动activity
        //此时，说明activity不是用户退出的，而是被系统或其他应用杀死的。
        //应通知service，让其稍后重启activity
        super.onDestroy()
    }
}