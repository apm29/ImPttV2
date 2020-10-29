package com.imptt.v2.view.group

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.imptt.v2.R
import com.imptt.v2.core.ptt.PttObserver
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.utils.*
import com.imptt.v2.view.adapter.MessageListAdapter
import com.imptt.v2.view.user.UserInfoFragmentArgs
import com.imptt.v2.vm.GroupViewModel
import com.imptt.v2.widget.PttButton
import com.kylindev.pttlib.db.ChatMessageBean
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

        setHasOptionsMenu(true)



        launch {
            val pttService = requirePttService()
            observe<MutableList<ChatMessageBean>>(groupViewModel.data, Observer {
                initialList(it, pttService.currentUser.iId)
            })
            pttService.enterChannel(groupId.toInt())
            buttonPtt.pttButtonState = object : PttButton.PttButtonState {
                override fun onPressDown() {
                    super.onPressDown()
                    pttService.userPressDown()
                }

                override fun onPressUp() {
                    super.onPressUp()
                    pttService.userPressUp()
                }
            }
            setToolbarTitle(pttService.getChannelByChanId(groupId.toInt()).name)
            pttService.registerObserverWithLifecycle(this@GroupFragment, object : PttObserver() {
                override fun onRecordFinished(messageBean: ChatMessageBean) {
                    super.onRecordFinished(messageBean)
                    groupViewModel.loadMessages(groupId.toInt(), pttService)
                }

                override fun onPlaybackChanged(channelId: Int, resId: Int, play: Boolean) {
                    super.onPlaybackChanged(channelId, resId, play)
                    val messageListAdapter = recyclerViewMessages.adapter as MessageListAdapter
                    if (play) {
                        messageListAdapter.notifyPlaybackStart(resId)
                    } else {
                        messageListAdapter.notifyPlaybackStop()
                    }
                }
            })
            layoutSwipeRefresh.setOnRefreshListener {
                groupViewModel.loadMessages(groupId.toInt(), pttService, false)
                layoutSwipeRefresh.isRefreshing = false
            }
            groupViewModel.loadMessages(groupId.toInt(), pttService)
        }
    }

    private fun initialList(messages: MutableList<ChatMessageBean>, myId: Int) {
        recyclerViewMessages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        if (recyclerViewMessages.adapter == null) {
            recyclerViewMessages.adapter =
                MessageListAdapter(
                    messages,
                    layoutInflater,
                    myId,
                    -1,
                    ::onMessageClicked,
                    ::onUserClicked
                )
        } else {

            val messageListAdapter = recyclerViewMessages.adapter as MessageListAdapter
            val old = messageListAdapter.itemCount
            messageListAdapter.addData(messages)
            recyclerViewMessages.postDelayed({
                recyclerViewMessages.scrollToPosition(old)
            },0)
        }
    }

    private fun onMessageClicked(message: ChatMessageBean, view: View, position: Int) {
        if (message.voice != null && message.voice.isNotEmpty() && message.text == null)
            launch {
                val pttService = requirePttService()
                val messageListAdapter = recyclerViewMessages.adapter as MessageListAdapter
                val currentPlayPosition = messageListAdapter.getCurrentPlayPosition()
                if (currentPlayPosition == position) {
                    pttService.stopPlayback()
                    messageListAdapter.notifyPlaybackStop()
                } else {
                    pttService.playback(message.voice, groupId.toInt(), position)
                    messageListAdapter.notifyPlaybackStart(position)
                }
            }
    }

    private fun onUserClicked(message: ChatMessageBean, view: View) {
        navigate(
            R.id.userInfoFragment,
            UserInfoFragmentArgs.Builder(message.uid.toString()).build().toBundle()
        )
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