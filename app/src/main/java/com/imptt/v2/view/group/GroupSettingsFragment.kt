package com.imptt.v2.view.group

import android.os.Bundle
import android.view.View
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.data.model.UserInfo
import com.imptt.v2.utils.observe
import com.imptt.v2.view.adapter.GroupUserGridAdapter
import com.imptt.v2.vm.GroupSettingsViewModel
import kotlinx.android.synthetic.main.fragment_group_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.IllegalArgumentException
import java.util.*

/**
 *  author : ciih
 *  date : 2020/10/19 2:41 PM
 *  description :
 */
class GroupSettingsFragment:BaseFragment() {

    private val groupSettingsViewModel:GroupSettingsViewModel by viewModel()
    private val groupId: String by lazy {
        GroupFragmentArgs.fromBundle(requireArguments()).groupId
            ?: throw IllegalArgumentException("群组id为空")
    }
    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_group_settings
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        observe(groupSettingsViewModel.users){
            initialGrid(it)
        }
        setToolbarTitle(groupId)
        editTextGroupName.setText(groupId)
        imageViewGroupIcon.setImageResource(R.mipmap.ic_launcher)
        textViewGroupUserCount.text = "群组成员${Random().nextInt(99)}人"
    }

    private fun initialGrid(users: ArrayList<UserInfo>) {
        if (recyclerViewGroupMembers.adapter == null) {
            recyclerViewGroupMembers.adapter = GroupUserGridAdapter(users, layoutInflater, ::onGroupUserClicked)
        } else {
            (recyclerViewGroupMembers.adapter as GroupUserGridAdapter).newList(users)
        }
    }

    private fun onGroupUserClicked(user: UserInfo, view: View) {

    }
}