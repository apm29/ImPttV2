package com.imptt.v2.view.splash

import android.app.ActivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.imptt.v2.R
import kotlinx.coroutines.Dispatchers

/**
 *  author : ciih
 *  date : 2020/10/19 11:04 AM
 *  description :
 */
class SplashFragment:Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_splash,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findNavController(
            R.id.app_host_fragment
        ).navigate(R.id.mainFragment)
    }
}