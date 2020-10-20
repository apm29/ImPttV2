package com.imptt.v2.view.user

import android.os.Bundle
import android.view.View
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.view.group.GroupFragmentArgs
import com.imptt.v2.vm.UserInfoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.IllegalArgumentException

class UserInfoFragment : BaseFragment() {

    private val userInfoViewModel:UserInfoViewModel by viewModel()

    private val userId: String by lazy {
        UserInfoFragmentArgs.fromBundle(requireArguments()).userId
            ?: throw IllegalArgumentException("群组id为空")
    }
    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_user_info
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        setToolbarTitle(userId)
    }
}