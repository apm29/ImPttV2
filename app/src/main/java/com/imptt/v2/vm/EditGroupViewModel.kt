package com.imptt.v2.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.data.entity.Group
import com.imptt.v2.data.model.message.Message
import com.imptt.v2.data.model.message.MessageType
import com.imptt.v2.data.repo.ImRepository

/**
 *  author : ciih
 *  date : 2020/10/19 10:41 AM
 *  description :
 */
class EditGroupViewModel(
    private val imRepository: ImRepository
):ViewModel() {

    suspend fun addGroup(name:String?,icon:String?){
        imRepository.addGroup(
            Group(groupName = name,groupIcon = icon)
        )
    }

}