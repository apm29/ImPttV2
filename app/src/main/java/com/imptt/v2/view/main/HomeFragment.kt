package com.imptt.v2.view.main

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.imptt.v2.R
import com.imptt.v2.core.ptt.PttObserver
import com.imptt.v2.core.struct.BaseNestedFragment
import com.imptt.v2.utils.log
import com.imptt.v2.utils.navigate
import com.imptt.v2.utils.registerObserverWithLifecycle
import com.imptt.v2.utils.requirePttService
import com.imptt.v2.view.adapter.GroupListAdapter
import com.imptt.v2.view.group.EditGroupFragment
import com.imptt.v2.view.group.GroupFragmentArgs
import com.imptt.v2.vm.HomeViewModel
import com.kylindev.pttlib.service.InterpttService
import com.kylindev.pttlib.service.model.Channel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseNestedFragment() {

    private val homeViewModel: HomeViewModel by viewModel(
        owner = { ViewModelOwner.from(this.requireActivity(), this.requireActivity()) }
    )

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_home
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {

        initialList(arrayListOf(), arrayListOf())

        observeResult<Boolean>(EditGroupFragment.KEY_SAVE_GROUP_RESULT) {
            println("${EditGroupFragment.KEY_SAVE_GROUP_RESULT}:$it")
        }

        setToolbarTitle("首页")
        setHasOptionsMenu(true)

        launch(ioContext) {
            homeViewModel.loadChannels()
        }

        launch {
            val service = requirePttService()
            initialList(service.channelList, service.listenChannels)
            changeChannel(service.currentChannel)
            service.registerObserverWithLifecycle(this@HomeFragment, object : PttObserver(
                tagFragment
            ) {
                override fun onCurrentChannelChanged() {
                    changeChannel(service.currentChannel)
                }

                override fun onChannelUpdated(channel: Channel) {
                    initialList(service.channelList, service.listenChannels)
                }

                override fun onChannelAdded(channel: Channel) {
                    initialList(service.channelList, service.listenChannels)
                }

                override fun onChannelRemoved(channel: Channel) {
                    initialList(service.channelList, service.listenChannels)
                }

                override fun onConnectionStateChanged(state: InterpttService.ConnState) {
                    if (state == InterpttService.ConnState.CONNECTION_STATE_DISCONNECTED) {
                        initialList(arrayListOf(), arrayListOf())
                    } else {
                        initialList(service.channelList, service.listenChannels)
                    }
                }

                override fun onListenChanged(listen: Boolean) {
                    super.onListenChanged(listen)

                }
            })
        }

    }

    private fun changeChannel(currentChannel: Channel?) {
        log(currentChannel)
        if (currentChannel != null && recyclerViewGroupList != null) {
            (recyclerViewGroupList.adapter as GroupListAdapter).changeChannel(currentChannel)
        }
    }

    private fun initialList(groups: ArrayList<Channel>?, listenChannels: MutableList<Int>?) {
        if (groups != null && recyclerViewGroupList != null) {
            recyclerViewGroupList.layoutManager = LinearLayoutManager(requireContext())
            if (recyclerViewGroupList.adapter == null) {
                recyclerViewGroupList.adapter =
                    GroupListAdapter(
                        groups,
                        layoutInflater,
                        ::onGroupRoute,
                        ::onCurrentChannelListenChange,
                        ::onCurrentChannelSpeakChange,
                        listenChannels
                    )
            } else {
                (recyclerViewGroupList.adapter as GroupListAdapter).newList(groups, listenChannels)
            }
        }
    }

    private fun onGroupRoute(channel: Channel, view: View) {
        launch {
            val service = requirePttService()
            if (service.currentChannel.id == channel.id) {
                navigateToChannel(channel, service)
            } else {
                AlertDialog.Builder(view.context)
                    .setTitle("提示")
                    .setMessage("切换到${channel.name}吗?")
                    .setPositiveButton("切换") { dialogInterface: DialogInterface, which: Int ->
                        dialogInterface.dismiss()
                        navigateToChannel(channel, service)
                    }
                    .setNegativeButton("取消") { dialog, which ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun navigateToChannel(
        channel: Channel,
        service: InterpttService
    ) {
        service.enterChannel(channel.id)
        navigate(
            R.id.action_mainFragment_to_groupFragment,
            GroupFragmentArgs.Builder(channel.id.toString()).build().toBundle()
        )
    }

    private fun onCurrentChannelListenChange(channel: Channel, checked: Boolean, view: View) {
        launch {
            val service = requirePttService()
            service.setListen(channel.id, !service.isListen(channel.id))
        }
    }

    private fun onCurrentChannelSpeakChange(channel: Channel, checked: Boolean, view: View) {
        launch {
            val service = requirePttService()
            if (checked) {
                service.joinChannel(channel.id, "", "")
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addGroup) {
            navigate(R.id.action_mainFragment_to_editGroupFragment)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}