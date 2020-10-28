package com.imptt.v2.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.core.struct.BaseNestedFragment
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

fun <T> Fragment.observe(data:LiveData<T>?,observer: Observer<T>){
    data?.observe(this,observer)
}

fun Fragment.navigate(targetId:Int, args:Bundle? = null){
    findPrimaryNavController().navigate(
        targetId,
        args,
    )
}
fun Fragment.findPrimaryNavController() = Navigation.findNavController(this.requireActivity(),R.id.app_host_fragment)


fun BaseFragment.log(vararg any: Any?){
    Log.e(tagFragment,any.joinToString(","))
}

fun BaseNestedFragment.log(vararg any: Any?){
    Log.e(tagFragment,any.joinToString(","))
}

fun View.visible(delay:Long = 0){
    this.postDelayed({
        this.visibility = View.VISIBLE
    },delay)
}

fun View.invisible(delay:Long = 0){
    this.postDelayed( {
        this.visibility = View.INVISIBLE
    },delay)
}

fun View.gone(delay:Long = 0){
    this.postDelayed({
        this.visibility = View.GONE
    },delay)
}

fun View.showAndHide(delay:Long = 0){
    this.post {
        this.visibility = View.VISIBLE
    }
    this.postDelayed({
        this.visibility = View.INVISIBLE
    },delay)
}



fun InterpttService.registerObserverWithLifecycle(lifecycleOwner: LifecycleOwner,observer: BaseServiceObserver){
    lifecycleOwner.lifecycle.addObserver(object:LifecycleObserver{
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            Log.e("InterpttService","$lifecycleOwner 已启动,注册监听")
            registerObserver(observer)
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            Log.e("InterpttService","$lifecycleOwner 已停止,反注册监听")
            unregisterObserver(observer)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            Log.e("InterpttService","$lifecycleOwner 已停止,反注册LifecycleObserver")
            lifecycleOwner.lifecycle.removeObserver(this)
        }
    })
}

fun Number.clamp(floor:Double = 0.0, ceil:Double = 5000.0): Double {
    if(this.toDouble() > ceil){
        return ceil
    } else if( this.toDouble() < floor){
        return  floor
    }
    return this.toDouble()
}

suspend fun Fragment.requirePttService() = suspendCoroutine<InterpttService> { continuation ->
    try {
        val hostActivity = requireActivity() as HostActivity
        val service = hostActivity.mService
        if(service != null){
            continuation.resume(service)
        }else{
            hostActivity.tryBindService{ pttService ->
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
        if(service != null){
            continuation.resume(service)
        }else{
            this.tryBindService{ pttService ->
                continuation.resume(pttService)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        continuation.resumeWithException(e)
    }
}