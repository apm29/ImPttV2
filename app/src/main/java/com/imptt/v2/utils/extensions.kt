package com.imptt.v2.utils

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.navigation.navOptions
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.core.struct.BaseNestedFragment
import com.imptt.v2.di.GlideApp
import com.imptt.v2.view.HostActivity
import com.kylindev.pttlib.service.BaseServiceObserver
import com.kylindev.pttlib.service.InterpttService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 *  author : ciih
 *  date : 2020/10/15 1:37 PM
 *  description :
 */

fun <T> Fragment.observe(data: LiveData<T>?, observer: Observer<T>) {
    data?.observe(this, observer)
}

fun Fragment.navigate(targetId: Int, args: Bundle? = null, popUpToMain: Boolean = false) {
    return findPrimaryNavController().navigate(
        targetId,
        args,
        if (popUpToMain) navOptions {
            popUpTo(R.id.host_nav_graph) {
                inclusive = true
            }
            launchSingleTop = true
        } else {
            null
        }
    )
}

fun Fragment.pop(): Boolean {
    return findPrimaryNavController().popBackStack()
}

fun Fragment.findPrimaryNavController() =
    Navigation.findNavController(this.requireActivity(), R.id.app_host_fragment)


fun BaseFragment.log(vararg any: Any?) {
    Log.e(tagFragment, any.joinToString(","))
}

fun BaseNestedFragment.log(vararg any: Any?) {
    Log.e(tagFragment, any.joinToString(","))
}

fun View.visible(delay: Long = 0) {
    this.postDelayed({
        this.visibility = View.VISIBLE
    }, delay)
}

fun View.invisible(delay: Long = 0) {
    this.postDelayed({
        this.visibility = View.INVISIBLE
    }, delay)
}

fun View.gone(delay: Long = 0) {
    this.postDelayed({
        this.visibility = View.GONE
    }, delay)
}

fun View.showAndHide(delay: Long = 0) {
    this.post {
        this.visibility = View.VISIBLE
    }
    this.postDelayed({
        this.visibility = View.INVISIBLE
    }, delay)
}


fun InterpttService.registerObserverWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    observer: BaseServiceObserver
) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            Log.e("InterpttService", "$lifecycleOwner 已启动,注册监听")
            registerObserver(observer)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            Log.e("InterpttService", "$lifecycleOwner 已停止,反注册监听")
            unregisterObserver(observer)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            Log.e("InterpttService", "$lifecycleOwner 已停止,反注册LifecycleObserver")
            lifecycleOwner.lifecycle.removeObserver(this)
        }
    })
}

fun Number.clamp(floor: Double = 0.0, ceil: Double = 5000.0): Double {
    if (this.toDouble() > ceil) {
        return ceil
    } else if (this.toDouble() < floor) {
        return floor
    }
    return this.toDouble()
}

suspend fun Fragment.requirePttService() = suspendCoroutine<InterpttService> { continuation ->
    try {
        val hostActivity = requireActivity() as HostActivity
        val service = hostActivity.mService
        if (service != null) {
            continuation.resume(service)
        } else {
            hostActivity.tryBindService { pttService ->
                continuation.resume(pttService)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        continuation.resumeWithException(e)
    }
}

suspend fun HostActivity.requirePttService() = suspendCoroutine<InterpttService> { continuation ->
    try {
        val service = this.mService
        if (service != null) {
            continuation.resume(service)
        } else {
            this.tryBindService { pttService ->
                continuation.resume(pttService)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        continuation.resumeWithException(e)
    }
}


suspend fun HostActivity.requireInterPttService() = suspendCoroutine<InterpttService> {
    if (mService == null) {
        bindService(Intent(this, InterpttService::class.java), object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val localBinder = service as InterpttService.LocalBinder
                mService = localBinder.service
                it.resume(mService!!)
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }, Context.BIND_AUTO_CREATE)
    } else {
        it.resume(mService!!)
    }
}

suspend fun Fragment.requireInterPttService() =
    (requireActivity() as HostActivity).requireInterPttService()


fun isServiceRunning(context: Context, className: String = "com.kylindev.pttlib.service.InterpttService"): Boolean {
    var isRunning = false
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    //200 安全起见，此值选大点，以免不够
    val serviceList = activityManager.getRunningServices(200)
    if (serviceList.size <= 0) {
        return false
    }
    for (i in serviceList.indices) {
        if (serviceList[i].service.className == className) {
            isRunning = true
            break
        }
    }
    return isRunning
}

fun getAppVersionCode(context: Context): Long {
    var appVersionCode: Long = 0
    try {
        val packageInfo = context.applicationContext
            .packageManager
            .getPackageInfo(context.packageName, 0)
        appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return appVersionCode
}

/**
 * 获取当前app version name
 */
fun getAppVersionName(context: Context): String? {
    var appVersionName = ""
    try {
        val packageInfo = context.applicationContext
            .packageManager
            .getPackageInfo(context.packageName, 0)
        appVersionName = packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return appVersionName
}

fun ImageView.loadImageData(res:ByteArray?){
    res?.let {
        GlideApp.with(this)
            .load(it)
            .into(this)
    }
}

fun InterpttService.userPressedWithHam(){
    SoundPoolUtils.getInstance(this).playPttStartHam()
    this.userPressDown()
}