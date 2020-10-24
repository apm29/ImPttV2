package com.imptt.v2.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.imptt.v2.R
import com.imptt.v2.core.ptt.PttObserver
import com.imptt.v2.core.struct.PttServiceBindActivity
import com.imptt.v2.utils.*
import com.itsmartreach.libzm.ZmCmdLink
import com.kylindev.pttlib.service.InterpttProtocolHandler
import com.kylindev.pttlib.service.InterpttService
import com.kylindev.pttlib.service.model.User
import com.kylindev.pttlib.utils.ServerProto
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_host.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 *  author : ciih
 *  date : 2020/10/19 10:50 AM
 *  description :
 */
class HostActivity : PttServiceBindActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {
        const val TAG = "HostActivity"
    }

    val localStorage: LocalStorage by lazy {
        LocalStorage.getInstance(this)
    }

    private val zmLink: ZmCmdLink by lazy {
        ZmCmdLink(this, object : ZmCmdLink.ZmEventListener {
            override fun onScoStateChanged(sco: Boolean) {
                println("AudioRecordActivity.onScoStateChanged")
                println("sco = [${sco}]")
            }

            override fun onSppStateChanged(spp: Boolean) {
                Toast.makeText(
                    this@HostActivity,
                    if (spp) "连接蓝牙肩咪成功" else "连接蓝牙肩咪失败",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onUserEvent(event: ZmCmdLink.ZmUserEvent?) {
                println("AudioRecordActivity.onUserEvent")
                println("event = [${event}]")
                if (event == ZmCmdLink.ZmUserEvent.zmEventPttPressed) {
                    launch {
                        val pttService = requirePttService()
                        pttService.userPressDown()
                    }
                } else if (event == ZmCmdLink.ZmUserEvent.zmEventPttReleased) {
                    launch {
                        val pttService = requirePttService()
                        pttService.userPressUp()
                    }
                }
            }

            override fun onBatteryLevelChanged(p0: Int) {
            }

            override fun onVolumeChanged(p0: Boolean) {
            }
        }, true)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        doRequestPermissions {
            Log.e("HostActivity", "权限获取成功")
        }
        Log.e(TAG, zmLink.isConnected.toString())
        findNavController(R.id.app_host_fragment)

        launch {
            val pttService = requirePttService()
            pttService.registerObserverWithLifecycle(this@HostActivity,
                object : PttObserver(this@HostActivity::class.simpleName) {
                    override fun onLocalUserTalkingChanged(user: User?, talking: Boolean) {
                        super.onLocalUserTalkingChanged(user, talking)
                        layoutVolume.visibility = if (talking) View.VISIBLE else View.GONE
                        textViewCaller.text =
                            if (user != null) "${user.channel.name}|${user.name}\r\n正在讲话" else "正在讲话"
                    }

                    override fun onNewVolumeData(volume: Short) {
                        super.onNewVolumeData(volume)
                        rhythmView.setPerHeight((volume.clamp() / 5000f).toFloat())
                    }

                    override fun onPcmRecordFinished(
                        sendPcmRecord: ShortArray,
                        pcmRecordLength: Int
                    ) {
                        super.onPcmRecordFinished(sendPcmRecord, pcmRecordLength)
                        Log.e(
                            TAG,
                            "PCM文件:${
                                Formatter.formatFileSize(
                                    this@HostActivity,
                                    pcmRecordLength.toLong()
                                )
                            }"
                        )
                    }

                    override fun onConnectionStateChanged(state: InterpttService.ConnState) {
                        Log.e(TAG, state.toString())
                        changeState(state, pttService)
                    }

                    override fun onAmrData(data: ByteArray?, length: Int, duration: Int) {
                        super.onAmrData(data, length, duration)
                        Log.e(
                            TAG,
                            "AMR文件:${
                                Formatter.formatFileSize(
                                    this@HostActivity,
                                    length.toLong()
                                )
                            },时长:$duration"
                        )
                    }

                    override fun onRejected(rejectType: ServerProto.Reject.RejectType) {
                        Log.e(TAG, rejectType.toString())
                        when (rejectType) {
                            ServerProto.Reject.RejectType.None -> {
                                localStorage.userId = pttService.userid
                                runOnUiThread {
                                    //登录成功
                                    findNavController(R.id.app_host_fragment).navigate(
                                        R.id.mainFragment,
                                        null,
                                        navOptions {
                                            popUpTo(R.id.host_nav_graph) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    )
                                }
                            }
                            else -> {
                                //登录失败
                                Toast.makeText(
                                    this@HostActivity,
                                    "登录失败:$rejectType",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }

                    override fun onListenChanged(listen: Boolean) {
                        super.onListenChanged(listen)
                        runOnUiThread {
                            Toast.makeText(
                                this@HostActivity,
                                if (listen) "开始监听频道" else "停止监听频道",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }
    }


    fun changeState(state: InterpttService.ConnState, pttService: InterpttService) {
        when (state) {
            InterpttService.ConnState.CONNECTION_STATE_CONNECTING -> {
                layoutState.visible()
                textViewState.text = "正在连接服务器"
            }
            InterpttService.ConnState.CONNECTION_STATE_DISCONNECTED -> {


                layoutState.gone()
                textViewState.text = "对讲服务已断开"
                //断开原因
                if (pttService.disconnectReason == InterpttProtocolHandler.DisconnectReason.Kick) {
                    Toast.makeText(this, "账号在其他设备登录", Toast.LENGTH_LONG).show()
                    localStorage.offlineReason = LocalStorage.OFFLINE_REASON_KICK
                    pttService.stopSelf()
                } else {
                    Toast.makeText(this, "对讲服务连接失败", Toast.LENGTH_LONG).show()
                    localStorage.offlineReason = LocalStorage.OFFLINE_REASON_OTHER
                }

                //登录成功
                findNavController(R.id.app_host_fragment).navigate(
                    R.id.loginFragment,
                    null,
                    navOptions {
                        popUpTo(R.id.host_nav_graph) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                )
            }
            InterpttService.ConnState.CONNECTION_STATE_SYNCHRONIZING -> {
                layoutState.visible()
                textViewState.text = "正在同步"
            }
            InterpttService.ConnState.CONNECTION_STATE_CONNECTED -> {
                layoutState.gone()
                textViewState.text = "对讲服务已连接"
            }
        }
    }

    override fun onServiceBind(service: InterpttService) {
        Log.e(TAG, mService?.connectionState.toString())

        //channel界面出现过一次后，才设置notification对应的activity
        val notifIntent = Intent(this, HostActivity::class.java)
        mService?.setNotifIntent(notifIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.app_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        zmLink.destroy()
    }

    private fun doRequestPermissions(
        permissions: List<String> = arrayListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ),
        callback: (() -> Unit)? = null
    ) {
        PermissionX.init(this).permissions(
            permissions
        )
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                if (deniedList.isNotEmpty())
                    scope.showRequestReasonDialog(
                        deniedList,
                        "App运行需要获取手机内部存储权限以及录音权限！",
                        "好的",
                        "取消"
                    )
            }
            .onForwardToSettings { scope, deniedList ->
                if (deniedList.isNotEmpty())
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "请到设置中心打开所需的权限",
                        "好的",
                        "取消"
                    )
            }
            .request { allGranted, _, deniedList ->
                if (allGranted && deniedList.isEmpty()) {
                    callback?.invoke()
                } else {
                    doRequestPermissions(deniedList)
                }
            }
    }
}