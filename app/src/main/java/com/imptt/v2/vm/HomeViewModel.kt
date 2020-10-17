package com.imptt.v2.vm


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.core.websocket.Group
import com.imptt.v2.data.repo.ImRepository

class HomeViewModel(
    private val imRepository: ImRepository
): ViewModel() {

    val groups:MutableLiveData<ArrayList<Group>> = MutableLiveData()


    fun setImGroups(groups:ArrayList<Group>){
        this.groups.value = groups
    }


    init {
        setImGroups(
            arrayListOf(
                Group("测试频道1"),
                Group("测试频道2")
            )
        )
    }

}