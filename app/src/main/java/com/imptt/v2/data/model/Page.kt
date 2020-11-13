package com.imptt.v2.data.model

import com.google.gson.annotations.SerializedName

/**
 *  author : ciih
 *  date : 2020/11/12 7:57 PM
 *  description :
 */
data class Page<T>(
    val rows:List<T> = arrayListOf(),
    val page:Int = 1,
    @SerializedName("pagenum")
    val pageNum:Int = 20,
    val total:Int = 0
)