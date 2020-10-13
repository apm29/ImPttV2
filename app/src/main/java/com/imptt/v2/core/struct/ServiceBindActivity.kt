package com.imptt.v2.core.struct

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.imptt.v2.core.messenger.view.ViewMessenger

/**
 *  author : apm29[ciih]
 *  date : 2020/9/29 4:22 PM
 *  description :
 */
open class ServiceBindActivity : AppCompatActivity(), ServiceConnection {

    private val serviceBinderProxy: ServiceBinderProxy by lazy {
        ServiceBinderProxy(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        serviceBinderProxy.ensureCreated()
        super.onCreate(savedInstanceState)
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
        println("ServiceBindActivity.onServiceConnected")
        ViewMessenger.bindService(service)
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
        println("ServiceBindActivity.onServiceDisconnected")
        ViewMessenger.unbind()
    }
}