package com.imptt.v2.data.entity

import androidx.room.Entity

/**
 *  author : ciih
 *  date : 2020/10/21 6:44 PM
 *  description :
 */

data class Page<T>(
    val total:Int,
    val data:List<T>,
    val page:Int,
    val rows:Int
)