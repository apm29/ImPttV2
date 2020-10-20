package com.imptt.v2.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.data.model.UserInfo

/**
 *  author : ciih
 *  date : 2020/10/19 10:41 AM
 *  description :
 */
class GroupUsersViewModel:ViewModel() {

    private val _users: MutableLiveData<ArrayList<UserInfo>> = MutableLiveData()

    val users: LiveData<ArrayList<UserInfo>> = _users


    private fun setMessages(users:ArrayList<UserInfo>){
        this._users.value = users
    }


    init {
        setMessages(
            arrayListOf(
                UserInfo("用户001"),
                UserInfo("用户002"),
                UserInfo("用户003"),
                UserInfo("用户004"),
                UserInfo("用户005"),
                UserInfo("用户005"),
                UserInfo("用户005"),
                UserInfo("用户005"),
                UserInfo("用户005"),
                UserInfo("用户005"),
                UserInfo("用户005"),
                UserInfo("用户005"),
                UserInfo("用户005"),
                UserInfo("用户005"),
            )
        )
    }

}