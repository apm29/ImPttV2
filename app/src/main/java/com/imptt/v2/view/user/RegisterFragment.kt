package com.imptt.v2.view.user

import android.os.Bundle
import android.view.View
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.utils.requirePttService
import kotlinx.coroutines.launch

/**
 *  author : ciih
 *  date : 2020/10/29 8:34 AM
 *  description :
 */
class RegisterFragment:BaseFragment() {
    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_register
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        launch {
            val pttService = requirePttService()
        }
    }
}