package com.imptt.v2.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.PowerManager
import android.text.format.Formatter
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.imptt.v2.R
import com.imptt.v2.core.ptt.AppConstants
import com.imptt.v2.core.ptt.PttObserver
import com.imptt.v2.core.struct.PttServiceBindActivity
import com.imptt.v2.receiver.MediaButtonReceiver
import com.imptt.v2.utils.*
import com.itsmartreach.libzm.ZmCmdLink
import com.kylindev.pttlib.service.InterpttProtocolHandler
import com.kylindev.pttlib.service.InterpttService
import com.kylindev.pttlib.service.model.User
import com.kylindev.pttlib.utils.ServerProto
import com.permissionx.guolindev.PermissionX
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_host.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 *  author : ciih
 *  date : 2020/10/19 10:50 AM
 *  description :
 */
class HostActivity : PttServiceBindActivity(), CoroutineScope {

    private val mExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e(TAG,"COROUTINE EXCEPTION:")
            //在此处捕获异常
            throwable.printStackTrace()
        }
    //SupervisorJob在子协程抛出异常时不会被取消
    private val mJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mJob + mExceptionHandler

    companion object {
        const val TAG = "HostActivity"
    }

    val localStorage: LocalStorage by lazy {
        LocalStorage.getInstance(this)
    }
    private val mAudioManager by lazy {
        getSystemService(AUDIO_SERVICE) as AudioManager
    }

    /**
     * 智咪按键监听
     */
    private lateinit var zmLink: ZmCmdLink

    private fun createZmLink(): ZmCmdLink {
        return ZmCmdLink(this, object : ZmCmdLink.ZmEventListener {
            //sco
            override fun onScoStateChanged(sco: Boolean) {
                println("AudioRecord.onScoStateChanged sco = [${sco}]")
//                if(sco){
//                    zmLink.enterSppMode()
//                }else{
//                    zmLink.enterSppStandbyMode()
//                }
            }

            //spp
            override fun onSppStateChanged(spp: Boolean) {
                println("AudioRecord.onSppStateChanged state = [$spp]")
                Toast.makeText(
                    this@HostActivity,
                    if (spp) "连接蓝牙肩咪成功" else "外放模式",
                    Toast.LENGTH_SHORT
                ).show()
//                if(!spp){
//                    zmLink.enterSpeakMode()
//                }
            }

            //用户按键
            override fun onUserEvent(event: ZmCmdLink.ZmUserEvent?) {
                println("AudioRecord.onUserEvent event = [${event}]")
                println("event = [${event}]")
                if (event == ZmCmdLink.ZmUserEvent.zmEventPttPressed) {
                    launch {
                        val pttService = requirePttService()
                        mAudioManager.startBluetoothSco()
                        pttService.userPressDown()
                    }
                } else if (event == ZmCmdLink.ZmUserEvent.zmEventPttReleased) {
                    launch {
                        val pttService = requirePttService()
                        pttService.userPressUp()
                        mAudioManager.stopBluetoothSco()
                    }
                }
            }

            override fun onBatteryLevelChanged(p0: Int) {
            }

            override fun onVolumeChanged(p0: Boolean) {
            }
        }, true)
    }

//    //按键监听
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
//            launch {
//                requirePttService().userPressDown()
//            }
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
//        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
//            launch {
//                requirePttService().userPressUp()
//            }
//            return true
//        }
//        return super.onKeyUp(keyCode, event)
//    }

    private val mPowerManager by lazy {
        getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    private val mWakeLock by lazy {
        mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ptt:MyWakelockTag")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWakeLock.acquire()

        setContentView(R.layout.activity_host)
        doRequestPermissions {
            Log.e("HostActivity", "权限获取成功")
        }
        zmLink = createZmLink()
        findNavController(R.id.app_host_fragment)
        launch {
            val pttService = requirePttService()
            var lastRhythmTime = 0L
            pttService.registerObserverWithLifecycle(this@HostActivity,
                object : PttObserver(this@HostActivity::class.simpleName) {
                    override fun onLocalUserTalkingChanged(user: User?, talking: Boolean) {
                        super.onLocalUserTalkingChanged(user, talking)
                        val fromSelf = pttService.currentUser!=null && pttService.currentUser?.iId == user?.iId
                        if (talking) {
                            if(pttService.voiceOn || fromSelf) {
                                layoutVolume.visible()
                            }
                        }else {
                            layoutVolume.gone()
                        }
                        textViewCaller.text =
                            if (user != null && !fromSelf && talking)
                                "${user.channel.name} | ${user.name}\r\n正在讲话"
                            else if(talking){
                                "正在讲话"
                            } else {
                                "结束"
                            }
                    }

                    override fun onNewVolumeData(volume: Short) {
                        super.onNewVolumeData(volume)
                        val currentTimeMillis = System.currentTimeMillis()
                        if (currentTimeMillis - lastRhythmTime > 500) {
                            rhythmView.setPerHeight((volume.clamp() / 5000f).toFloat())
                            lastRhythmTime = currentTimeMillis
                        }
                    }

                    override fun onTalkingTimerCanceled() {
                        super.onTalkingTimerCanceled()
                        textViewDuration.gone()
                        textViewCaller.text = "结束"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onTalkingTimerTick(duration: Int) {
                        super.onTalkingTimerTick(duration)
                        textViewDuration.visible()
                        textViewDuration.text = "已录制${duration}秒"
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
                                ).show()
                            }
                        }
                    }

                    override fun onListenChanged(listen: Boolean) {
                        super.onListenChanged(listen)
//                        runOnUiThread {
//                            Toast.makeText(
//                                this@HostActivity,
//                                if (listen) "开始监听频道" else "停止监听频道",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    }

                    override fun onPermissionDenied(reason: String, code: Int) {
                        super.onPermissionDenied(reason, code)
                        runOnUiThread {
                            Toast.makeText(
                                this@HostActivity,
                                "操作失败(${AppConstants.permReason[code]})$reason",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onHeadsetStateChanged(headState: InterpttService.HeadsetState) {
                        super.onHeadsetStateChanged(headState)
                    }

                    override fun onShowToast(message: String) {
                        super.onShowToast(message)
                        runOnUiThread {
                            Toast.makeText(
                                this@HostActivity,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })
            pttService.recordMode
        }

//        mAudioManager.registerMediaButtonEventReceiver(
//            ComponentName(this,MediaButtonReceiver::class.java)
//        )
//
//        val mediaSession = MediaSession(this, "ms")
//        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS  or
//                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
//        )
//        val playbackState = PlaybackState.Builder()
//            .setActions(PlaybackState.ACTION_PLAY_PAUSE)
//            .build()
//        mediaSession.setPlaybackState(playbackState)
//        mediaSession.setCallback(object:MediaSession.Callback(){
//            override fun onPlay() {
//                println("HostActivity.onPlay")
//            }
//
//            override fun onPause() {
//                println("HostActivity.onPause")
//            }
//
//            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
//                println("HostActivity.onMediaButtonEvent")
//                return super.onMediaButtonEvent(mediaButtonIntent)
//            }
//        })
//        mediaSession.isActive = true
    }

    override fun onStart() {
        super.onStart()
        Beta.checkAppUpgrade(false,false)
    }

    override fun onRestart() {
        super.onRestart()
        launch {
            val pttService = requirePttService()
            if(pttService.isAduioPlaying && pttService.voiceOn){
                layoutVolume.visible()
            }else{
                layoutVolume.gone()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val serviceRunning = isServiceRunning(this)
        println("HostActivity.onDestroy:$serviceRunning")
        if(!serviceRunning){
            zmLink.destroy()
        }
        mWakeLock.release()
        mAudioManager.unregisterMediaButtonEventReceiver(
            ComponentName(this,MediaButtonReceiver::class.java)
        )
        mJob.cancelChildren()
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
                Log.e(TAG,"pttKeycode:${pttService.pttKeycode}")
                pttService.strongOnline = true
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