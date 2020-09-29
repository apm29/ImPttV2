package com.imptt.v2.vm


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imptt.v2.data.repo.ImRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(
    private val imRepository: ImRepository,
    @Assisted val id:Long
): ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun say(){
        println(id)
        println(imRepository.getMessages())
    }

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(id: Long): HomeViewModel
    }

    companion object {

        fun provideFactory(
            assistedFactory: AssistedFactory,
            id: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(id) as T
            }
        }
    }
}