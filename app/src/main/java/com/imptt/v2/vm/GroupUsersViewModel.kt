package com.imptt.v2.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.data.api.SignalServerApi
import com.imptt.v2.data.model.UserInfo
import com.imptt.v2.data.model.v2.ChannelUser
import com.imptt.v2.data.repo.ImRepository

/**
 *  author : ciih
 *  date : 2020/10/19 10:41 AM
 *  description :
 */
class GroupUsersViewModel(
    val repo: ImRepository, val api: SignalServerApi
) : ViewModel() {


    val channelUserList: MutableLiveData<List<ChannelUser>> = MutableLiveData(arrayListOf())


    suspend fun getChannelUserList(channelId: Int) {
        try {
            val resp = api.getAllUserInChannel(channelId)
            if (resp.success) {
                channelUserList.value = resp.data
            }
        } catch (e: Exception) {

        }
    }

}