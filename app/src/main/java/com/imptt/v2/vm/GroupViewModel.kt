package com.imptt.v2.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.data.entity.Group
import com.imptt.v2.data.model.message.Message
import com.imptt.v2.data.model.message.MessageType
import com.imptt.v2.data.repo.ImRepository
import com.kylindev.pttlib.db.ChatMessageBean
import com.kylindev.pttlib.service.InterpttService
import java.util.ArrayList

/**
 *  author : ciih
 *  date : 2020/10/19 10:41 AM
 *  description :
 */
class GroupViewModel(
    private val imRepository: ImRepository
):ViewModel() {

    private val pageSize = 20
    private val messages: ArrayList<ChatMessageBean> = arrayListOf()
    val data:MutableLiveData<ArrayList<ChatMessageBean>> = MutableLiveData(messages)

    fun loadMessages(groupId:Int,service:InterpttService,refresh:Boolean = true){
        if(refresh){
            messages.clear()
        }
        val loaded = service.loadDBRecords(groupId,messages.size,pageSize)
        if(loaded!=null&&loaded.isNotEmpty()) {
            messages.addAll(0,loaded)
            data.value = messages
        }
    }

}