package com.imptt.v2.view.main

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.utils.observe
import com.imptt.v2.vm.SettingsViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment() {

    private val settingsViewModel: SettingsViewModel by viewModel(
        owner = { ViewModelOwner.from(this.requireActivity(), this.requireActivity()) }
    )

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_settings
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        observe(settingsViewModel.userInfo) {
            editTextName.setText("张三")
            editTextGroupName.setText("156XXXXXXX")
            editTextCallNumber.setText("02321111")
            Glide.with(view)
                .load(R.drawable.ic_launcher_foreground)
                .into(imageViewGroupIcon)
        }
    }
}