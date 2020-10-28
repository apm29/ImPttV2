package com.imptt.v2.view.main

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.core.struct.BaseNestedFragment
import com.imptt.v2.utils.findPrimaryNavController
import com.imptt.v2.utils.observe
import com.imptt.v2.utils.requirePttService
import com.imptt.v2.vm.SettingsViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseNestedFragment() {

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

        setToolbarTitle("设置")

        launch {
            val pttService = requirePttService()
            val currentUser = pttService.currentUser
            editTextName.setText("")
            imageViewGroupIcon.setOnClickListener {
                findPrimaryNavController().navigate(R.id.agentWebViewFragment)
            }
            editTextGroupName.setText(currentUser.name)
            editTextCallNumber.setText(currentUser.iId.toString())
            checkboxAlertWindow.isChecked = pttService.floatWindow
            checkboxHeadSet.isChecked = pttService.supportHeadsetKey
            checkboxAlertWindow.setOnCheckedChangeListener { _, isChecked ->
                pttService.floatWindow = isChecked
            }
            checkboxHeadSet.setOnCheckedChangeListener { _, isChecked ->
                pttService.supportHeadsetKey = isChecked
            }
        }
    }
}