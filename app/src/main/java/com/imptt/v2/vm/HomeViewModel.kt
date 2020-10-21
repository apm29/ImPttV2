package com.imptt.v2.vm


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.data.entity.Group
import com.imptt.v2.data.entity.GroupWithUsers
import com.imptt.v2.data.repo.ImRepository

class HomeViewModel(
    private val imRepository: ImRepository
): ViewModel() {

    private val _groups:MutableLiveData<ArrayList<GroupWithUsers>> = MutableLiveData()

    val groups: LiveData<ArrayList<GroupWithUsers>> = _groups

    suspend fun loadGroups(){
        _groups.value = ArrayList(imRepository.getGroupsWithUsers())
    }

    fun setImGroups(groups: java.util.ArrayList<com.imptt.v2.core.websocket.Group>) {

    }

}