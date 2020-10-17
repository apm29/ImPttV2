package com.imptt.v2.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.imptt.v2.R
import com.imptt.v2.utils.observe
import com.imptt.v2.view.adapter.ContactAdapter
import com.imptt.v2.vm.ContactViewModel
import com.imptt.v2.vm.SettingsViewModel
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModel(
        owner = { ViewModelOwner.from(this.requireActivity(), this.requireActivity()) }
    )
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observe(settingsViewModel.userInfo){
            editTextName.setText("张三")
            editTextPhoneNumber.setText("156XXXXXXX")
            editTextCallNumber.setText("02321111")
            Glide.with(view)
                .load(R.drawable.ic_launcher_foreground)
                .into(imageViewAvatar)
        }
    }
}