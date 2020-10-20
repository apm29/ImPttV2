package com.imptt.v2.view.group

import android.os.Bundle
import android.view.View
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.data.model.UserInfo
import com.imptt.v2.utils.navigate
import com.imptt.v2.utils.observe
import com.imptt.v2.view.adapter.GroupUserGridAdapter
import com.imptt.v2.view.user.UserInfoFragmentArgs
import com.imptt.v2.vm.GroupUsersViewModel
import kotlinx.android.synthetic.main.fragment_group_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.IllegalArgumentException

class GroupUsersFragment : BaseFragment() {

    private val groupUserViewModel:GroupUsersViewModel by viewModel()

    private val groupId: String by lazy {
        GroupUsersFragmentArgs.fromBundle(requireArguments()).groupId
            ?: throw IllegalArgumentException("群组id为空")
    }

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_group_users
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        setToolbarTitle(groupId)
        observe(groupUserViewModel.users){
            initialGrid(it)
        }
    }

    private fun initialGrid(users: ArrayList<UserInfo>) {
        if (recyclerViewGroupMembers.adapter == null) {
            recyclerViewGroupMembers.adapter = GroupUserGridAdapter(users, layoutInflater, ::onGroupUserClicked)
        } else {
            (recyclerViewGroupMembers.adapter as GroupUserGridAdapter).newList(users)
        }
    }

    private fun onGroupUserClicked(user: UserInfo, view: View) {
        navigate(R.id.action_groupUsersFragment_to_userInfoFragment, UserInfoFragmentArgs.Builder(user.userId).build().toBundle())
    }
}