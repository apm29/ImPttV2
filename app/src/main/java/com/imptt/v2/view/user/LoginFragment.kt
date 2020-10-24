package com.imptt.v2.view.user

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.imptt.v2.R
import com.imptt.v2.core.ptt.PttObserver
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.utils.*
import com.kylindev.pttlib.service.InterpttService
import com.kylindev.pttlib.utils.ServerProto
import kotlinx.android.synthetic.main.fragment_user_login.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 *  author : ciih
 *  date : 2020/10/23 8:45 AM
 *  description :
 */
class LoginFragment : BaseFragment() {


    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_user_login
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        enableUserInterface(false)
        username.setText(localStorage.userId)
        launch {
            val pttService = requirePttService()
            enableUserInterface(true)
            println("isLoginOnceOK = ${pttService.isLoginOnceOK}")
            println("userId = ${localStorage.userId}")
            println("offlineReason = ${localStorage.offlineReason}")
            if (
                pttService.isLoginOnceOK
                && localStorage.userId != null
//                && localStorage.offlineReason != LocalStorage.OFFLINE_REASON_KICK
            ) {
                hideIme()
                toMain()
            }
            login.setOnClickListener {
                hideIme()
                if(pttService.isLoginOnceOK){
                    pttService.relogin()
                }else {
                    pttService.login(
                        localStorage.host,
                        0,
                        username.text.trim().toString(),
                        password.text.trim().toString()
                    )
                }
            }
        }
    }

    private fun enableUserInterface(enable: Boolean) {
        login.isEnabled = enable
        username.isEnabled = enable
        password.isEnabled = enable
    }

    private fun toMain() {
        findPrimaryNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
    }

}