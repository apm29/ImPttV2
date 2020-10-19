package com.imptt.v2.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.data.model.message.Message
import com.imptt.v2.data.model.message.MessageType

/**
 *  author : ciih
 *  date : 2020/10/19 10:41 AM
 *  description :
 */
class GroupViewModel:ViewModel() {

    private val _messages: MutableLiveData<ArrayList<Message>> = MutableLiveData()

    val messages: LiveData<ArrayList<Message>> = _messages


    private fun setMessages(groups:ArrayList<Message>){
        this._messages.value = groups
    }


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

}