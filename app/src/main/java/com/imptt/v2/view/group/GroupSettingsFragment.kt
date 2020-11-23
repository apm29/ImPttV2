package com.imptt.v2.view.group

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.data.model.v2.ChannelUser
import com.imptt.v2.utils.findPrimaryNavController
import com.imptt.v2.utils.navigate
import com.imptt.v2.utils.observe
import com.imptt.v2.utils.requirePttService
import com.imptt.v2.view.adapter.GroupUserGridAdapter
import com.imptt.v2.view.adapter.PickListAdapter
import com.imptt.v2.view.user.UserInfoFragmentArgs
import com.imptt.v2.vm.GroupSettingsViewModel
import com.kylindev.pttlib.service.model.User
import kotlinx.android.synthetic.main.fragment_group_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
//            val users = service.sortedChannelMap[channel.id]
//            initialGrid(users ?: arrayListOf())
            editTextGroupName.setText(channel.name)
            buttonDismissGroup.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.title_warning)
                    .setMessage(R.string.desc_dissmiss_group)
                    .setPositiveButton(R.string.title_confirm){
                        dialog,_->
                        service.deleteChannel(groupId.toInt())
                        dialog.dismiss()
                        navigate(
                            R.id.mainFragment,
                            null,
                            true
                        )
                    }
                    .create()
                    .show()

            }
            buttonQuitGroup.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.title_warning)
                    .setMessage(R.string.desc_quit_group)
                    .setPositiveButton(R.string.title_confirm){
                            dialog,_->
                        service.quitChannel(groupId.toInt())
                        dialog.dismiss()
                        navigate(
                            R.id.mainFragment,
                            null,
                            true
                        )
                    }
                    .create()
                    .show()

            }

            layoutGroupUsersDetail.setOnClickListener {
                navigate(
                    R.id.action_groupSettingsFragment_to_groupUsersFragment,
                    GroupUsersFragmentArgs.Builder(groupId).build().toBundle()
                )
            }
            groupSettingsViewModel.getChannelUserList(groupId.toInt())
        }



        observe(groupSettingsViewModel.channelUserList){
            initialGrid(it)
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

    private fun initialGrid(users: List<ChannelUser>) {
        if (recyclerViewGroupMembers.adapter == null) {
            recyclerViewGroupMembers.adapter =
                GroupUserGridAdapter(users, layoutInflater, ::onGroupUserClicked, ::onAddUserClick)
        } else {
            (recyclerViewGroupMembers.adapter as GroupUserGridAdapter).newList(users)
        }
    }

    private fun onAddUserClick(view: View) {
        launch {
            view.isEnabled = false
            try {
                val userList = withContext(ioContext){
                    groupSettingsViewModel.getChannelCanAddUserList(groupId.toInt())
                }

                BottomSheetDialog(
                    requireContext()
                ).apply {
                    setContentView(R.layout.dialog_pick_user_layout)
                    val list: RecyclerView? = delegate.findViewById(R.id.recyclerViewUsers)
                    list?.layoutManager = LinearLayoutManager(requireContext())
                    list?.adapter = PickListAdapter(userList, layoutInflater,::onAddUserToGroup)
                }.show()
            } catch (e: Exception) {
            } finally {
                view.isEnabled = true
            }
        }
    }

    private fun onAddUserToGroup(user: ChannelUser, view: View){
        launch(ioContext) {
            val resp = groupSettingsViewModel.addUserToChannel(groupId.toInt(),user.userId)
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(), resp.text, Toast.LENGTH_SHORT).show()
                groupSettingsViewModel.getChannelUserList(groupId.toInt())
            }
        }
    }

    private fun onGroupUserClicked(user: ChannelUser, view: View) {
        navigate(
            R.id.action_groupSettingsFragment_to_userInfoFragment,
            UserInfoFragmentArgs.Builder(user.userId).build().toBundle()
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