package com.imptt.v2.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imptt.v2.data.model.Contact

class ContactViewModel : ViewModel() {

    val contacts: MutableLiveData<Contact> = MutableLiveData()

    init {
        val groups: ArrayList<String> = ArrayList()
        val items: ArrayList<ArrayList<String>> = ArrayList()
        groups.add("公安局")
        groups.add("消防")
        groups.add("特警")
        val itemList1: ArrayList<String> = ArrayList()
        itemList1.add("联系人A")
        itemList1.add("联系人B")
        itemList1.add("联系人C")
        val itemList2: ArrayList<String> = ArrayList()
        itemList2.add("联系人D")
        itemList2.add("联系人E")
        itemList2.add("联系人F")
        val itemList3: ArrayList<String> = ArrayList()
        itemList3.add("联系人G")
        itemList3.add("联系人H")
        items.add(itemList1)
        items.add(itemList2)
        items.add(itemList3)
        contacts.value = Contact(items, groups)
    }
}