package com.imptt.v2.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.data.model.UserInfo

class SettingsViewModel () : ViewModel() {

    val userInfo:MutableLiveData<UserInfo> = MutableLiveData()

    init {
        userInfo.value = UserInfo("1")
    }

}