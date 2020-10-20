package com.imptt.v2.view.main

import android.os.Bundle
import android.view.View
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseNestedFragment
import com.imptt.v2.utils.observe
import com.imptt.v2.view.adapter.ContactAdapter
import com.imptt.v2.vm.ContactViewModel
import kotlinx.android.synthetic.main.fragment_contact.*
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactFragment : BaseNestedFragment() {

    private val contactViewModel: ContactViewModel by viewModel(
        owner = { ViewModelOwner.from(this.requireActivity(), this.requireActivity()) }
    )

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return  R.layout.fragment_contact
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        observe(contactViewModel.contacts) {
            expandableListView.setAdapter(
                ContactAdapter(
                    it, layoutInflater
                )
            )
        }
        setToolbarTitle("联系人")
    }

}