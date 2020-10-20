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
class GroupViewModel(
    private val imRepository: ImRepository
):ViewModel() {

    private val _messages: MutableLiveData<ArrayList<Message>> = MutableLiveData()

    val messages: LiveData<ArrayList<Message>> = _messages


    private fun setMessages(groups:ArrayList<Message>){
        this._messages.value = groups
    }

    private val currentGroup:MutableLiveData<Group> = MutableLiveData()
    val current:LiveData<Group> = currentGroup

    init {
        setMessages(
            arrayListOf(
                Message(1L),
                Message(2L,contentType = MessageType.AUDIO_ME),
                Message(3L,contentType = MessageType.AUDIO_ME),
                Message(4L),
                Message(5L,contentType = MessageType.TEXT_ME),
                Message(6L,contentType = MessageType.TEXT_OTHER),
            )
        )
    }

    suspend fun loadGroupInfo(id:String){
        currentGroup.value = imRepository.queryGroupById(id)
    }

}