package com.imptt.v2.view.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.core.struct.BaseNestedFragment
import com.imptt.v2.utils.*
import com.imptt.v2.vm.SettingsViewModel
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
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

        setToolbarTitle("设置")
        textViewVersion.text = "${getAppVersionName(requireContext())} ${getAppVersionCode(requireContext())}"
        textViewVersion.setOnClickListener {
            Beta.checkAppUpgrade(true,false)
        }
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
            checkboxAutoPlay.setOnCheckedChangeListener { _, isChecked ->
                pttService.toggleVoiceOn()
            }
        }
    }
}