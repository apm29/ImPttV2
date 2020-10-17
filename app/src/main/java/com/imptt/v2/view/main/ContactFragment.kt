package com.imptt.v2.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.imptt.v2.R
import com.imptt.v2.utils.observe
import com.imptt.v2.view.adapter.ContactAdapter
import com.imptt.v2.vm.ContactViewModel
import com.imptt.v2.vm.HomeViewModel
import kotlinx.android.synthetic.main.fragment_contact.*
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactFragment : Fragment() {

    private val contactViewModel: ContactViewModel by viewModel(
        owner = { ViewModelOwner.from(this.requireActivity(), this.requireActivity()) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observe(contactViewModel.contacts) {
            expandableListView.setAdapter(
                ContactAdapter(
                    it, layoutInflater
                )
            )
        }
    }

}