package com.imptt.v2.view.group

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.utils.findPrimaryNavController
import com.imptt.v2.utils.navigate
import com.imptt.v2.utils.requirePttService
import com.imptt.v2.view.adapter.GroupUserGridAdapter
import com.imptt.v2.view.adapter.PickListAdapter
import com.imptt.v2.view.user.UserInfoFragmentArgs
import com.imptt.v2.vm.GroupSettingsViewModel
import com.kylindev.pttlib.service.model.User
import kotlinx.android.synthetic.main.fragment_group_settings.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.IllegalArgumentException

/**
 *  author : ciih
 *  date : 2020/10/19 2:41 PM
 *  description :
 */
class GroupSettingsFragment : BaseFragment() {

    private val groupSettingsViewModel: GroupSettingsViewModel by viewModel()
    private val groupId: String by lazy {
        GroupSettingsFragmentArgs.fromBundle(requireArguments()).groupId
            ?: throw IllegalArgumentException("群组id为空")
    }

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_group_settings
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        imageViewGroupIcon.setImageResource(R.mipmap.ic_launcher)
        setHasOptionsMenu(true)
        launch {
            val service = requirePttService()
            val channel = service.getChannelByChanId(groupId.toInt())
            setToolbarTitle(channel.name)
            textViewGroupUserCount.text = "群组成员${channel.memberCount}人"
            val users = service.sortedChannelMap[channel.id]
            initialGrid(users ?: arrayListOf())
            editTextGroupName.setText(channel.name)
            buttonDismissGroup.setOnClickListener {
                service.deleteChannel(groupId.toInt())
                //登录成功
                findPrimaryNavController().navigate(
                    R.id.mainFragment,
                    null,
                    navOptions {
                        popUpTo(R.id.host_nav_graph) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                )
            }
            buttonQuitGroup.setOnClickListener {
                service.quitChannel(groupId.toInt())
                //登录成功
                findPrimaryNavController().navigate(
                    R.id.mainFragment,
                    null,
                    navOptions {
                        popUpTo(R.id.host_nav_graph) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                )
            }
        }

        observeResult<Boolean>(EditGroupFragment.KEY_SAVE_GROUP_RESULT) {
            if (it) {
                launch {
                    val service = requirePttService()
                    val channel = service.getChannelByChanId(groupId.toInt())
                    setToolbarTitle(channel.name)
                    editTextGroupName.setText(channel.name)
                }
            }
        }
    }

    private fun initialGrid(users: MutableList<User>) {
        if (recyclerViewGroupMembers.adapter == null) {
            recyclerViewGroupMembers.adapter =
                GroupUserGridAdapter(users, layoutInflater, ::onGroupUserClicked, ::onAddUserClick)
        } else {
            (recyclerViewGroupMembers.adapter as GroupUserGridAdapter).newList(users)
        }
    }

    private fun onAddUserClick(view: View) {
        launch {
            val pttService = requirePttService()
            val userList = pttService.userList
            BottomSheetDialog(
                requireContext()
            ).apply {
                setContentView(R.layout.dialog_pick_user_layout)
                val list: RecyclerView? = delegate.findViewById(R.id.recyclerViewUsers)
                list?.layoutManager = LinearLayoutManager(requireContext())
                list?.adapter = PickListAdapter(userList, layoutInflater) { user, view ->
                    dismiss()
                }
            }.show()
        }
    }

    private fun onGroupUserClicked(user: User, view: View) {
        navigate(
            R.id.action_groupSettingsFragment_to_userInfoFragment,
            UserInfoFragmentArgs.Builder(user.iId.toString()).build().toBundle()
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.group_setting_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editGroup -> {
                navigate(
                    R.id.action_groupSettingsFragment_to_editGroupFragment,
                    EditGroupFragmentArgs.Builder().apply {
                        groupId = this@GroupSettingsFragment.groupId
                    }.build().toBundle()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}