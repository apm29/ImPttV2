package com.imptt.v2.view.user

import android.os.Bundle
import android.view.View
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.di.GlideApp
import com.imptt.v2.utils.requirePttService
import com.imptt.v2.view.group.GroupFragmentArgs
import com.imptt.v2.vm.UserInfoViewModel
import kotlinx.android.synthetic.main.fragment_user_info.*
import kotlinx.coroutines.launch
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
        launch {
            val user = requirePttService().getUser(userId.toInt())
            println(user)
            user?.apply {
                setToolbarTitle(user.name)
                editTextUserCallNumber.setText(user.iId.toString())
                GlideApp.with(requireContext())
                    .load(user.avatar)
                    .into(imageViewUserAvatar)
            }
        }
    }
}