package com.imptt.v2.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.imptt.v2.R

/**
 *  author : ciih
 *  date : 2020/10/15 1:37 PM
 *  description :
 */

fun <T> Fragment.observe(data:LiveData<T>,observer: Observer<T>){
    data.observe(this,observer)
}

fun Fragment.navigate(targetId:Int, args:Bundle? = null){
    requireActivity().findNavController(R.id.app_host_fragment).navigate(
        targetId,
        args,
    )
}