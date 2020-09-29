package com.imptt.v2.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.data.repo.ImRepository
import javax.inject.Inject

class NotificationsViewModel @ViewModelInject constructor(
    private val messRepository: ImRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}