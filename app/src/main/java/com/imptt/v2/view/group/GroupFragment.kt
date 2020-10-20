package com.imptt.v2.view.group

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.data.model.message.Message
import com.imptt.v2.utils.navigate
import com.imptt.v2.utils.observe
import com.imptt.v2.view.adapter.MessageListAdapter
import com.imptt.v2.view.user.UserInfoFragmentArgs
import com.imptt.v2.vm.GroupViewModel
import com.imptt.v2.widget.PttButton
import kotlinx.android.synthetic.main.fragment_group.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.IllegalArgumentException
import java.util.ArrayList


class GroupFragment : BaseFragment() {


    private val groupViewModel: GroupViewModel by viewModel()
    private val groupId: String by lazy {
        GroupFragmentArgs.fromBundle(requireArguments()).groupId
            ?: throw IllegalArgumentException("群组id为空")
    }

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_group
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        buttonPtt.pttButtonState = object : PttButton.PttButtonState {
            override fun onPressDown() {
                super.onPressDown()
            }

            override fun onPressUp() {
                super.onPressUp()
            }
        }
        setHasOptionsMenu(true)

        observe(groupViewModel.messages) {
            initialList(it)
        }
        observe(groupViewModel.current){
            setToolbarTitle(it.groupName)
        }
        launch {
            groupViewModel.loadGroupInfo(groupId)
        }
    }

    private fun initialList(messages: ArrayList<Message>) {
        recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,true)
        if (recyclerViewMessages.adapter == null) {
            recyclerViewMessages.adapter =
                MessageListAdapter(messages, layoutInflater, ::onMessageClicked,::onUserClicked)
        } else {
            (recyclerViewMessages.adapter as MessageListAdapter).newList(messages)
        }
    }

    private fun onMessageClicked(message: Message, view: View) {

    }

    private fun onUserClicked(message: Message, view: View) {
        navigate(R.id.userInfoFragment, UserInfoFragmentArgs.Builder(message.fromId).build().toBundle())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                navigate(
                    R.id.action_groupFragment_to_groupSettingsFragment,
                    GroupSettingsFragmentArgs.Builder(
                        groupId
                    ).build().toBundle()
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}