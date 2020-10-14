package com.imptt.v2.vm


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.imptt.v2.core.websocket.Group

class HomeViewModel: ViewModel() {

    private val groups:MutableLiveData<ArrayList<Group>> = MutableLiveData()

    val imGroups = Transformations.map(groups){
        it
    }
    fun setImGroups(groups:ArrayList<Group>){
        this.groups.value = groups
    }

}