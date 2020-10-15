package com.imptt.v2.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 *  author : ciih
 *  date : 2020/10/15 1:37 PM
 *  description :
 */

fun <T> Fragment.observe(data:LiveData<T>,observer: Observer<T>){
    data.observe(this,observer)
}