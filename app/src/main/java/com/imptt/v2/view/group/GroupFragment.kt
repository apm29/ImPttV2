package com.imptt.v2.view.group

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imptt.v2.R
import com.imptt.v2.core.ptt.PttObserver
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.data.api.BaseResp
import com.imptt.v2.data.api.SignalServerApi
import com.imptt.v2.utils.*
import com.imptt.v2.view.HostActivity
import com.imptt.v2.view.adapter.MessageListAdapter
import com.imptt.v2.view.user.UserInfoFragmentArgs
import com.imptt.v2.vm.GroupViewModel
import com.imptt.v2.widget.PttButton
import com.kylindev.pttlib.db.ChatMessageBean
import com.kylindev.pttlib.service.InterpttService
import kotlinx.android.synthetic.main.fragment_group.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GroupFragment : BaseFragment() {


    private val groupViewModel: GroupViewModel by viewModel()
    private val groupId: String by lazy {
        GroupFragmentArgs.fromBundle(requireArguments()).groupId
            ?: throw IllegalArgumentException("群组id为空")
    }

    private val api: SignalServerApi by inject()


    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_group
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println("context = [${context}], intent = [${intent}]")
            launch {
                if (recyclerViewMessages != null) {

                    groupViewModel.loadMoreMessages(
                        groupId.toInt(), requirePttService(), true,
                        recyclerViewMessages.adapter as MessageListAdapter,
                        recyclerViewMessages.layoutManager as RecyclerView.LayoutManager,
                        mHandler
                    )
                } else {
                    Toast.makeText(requireContext(), "收到消息", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {

        setHasOptionsMenu(true)



        launch {
            val pttService = requirePttService()
            observe<MutableList<Any>>(groupViewModel.mergeMessage) {
                initialList(it, pttService.currentUser.iId)
            }
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
            toggleButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    buttonPtt.gone()
                    editTextMessage.visible()
                } else {
                    buttonPtt.visible()
                    editTextMessage.gone()
                    hideIme()
                }
            }
            observe<MutableList<Any>>(groupViewModel.mergeMessage) {
                initialList(it, pttService.currentUser.iId)
            }
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
            toggleButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    buttonPtt.gone()
                    editTextMessage.visible()
                } else {
                    buttonPtt.visible()
                    editTextMessage.gone()
                }
            }
            editTextMessage.setOnEditorActionListener { v, actionId, event ->
                sendTextMessage(pttService,v.text)
                true
            }
            imageAddGallery.callback = { uri: MutableList<Uri>, path: MutableList<String> ->
                sendFileMessage(pttService, uri, path)
            }
            imageAddPhoto.callback = { uri: MutableList<Uri>, path: MutableList<String> ->
                sendFileMessage(pttService, uri, path)
            }
            imageAddFile.callback = { uri: MutableList<Uri>, path: MutableList<String> ->
                sendFileMessage(pttService, uri, path)
            }
            setToolbarTitle(pttService.getChannelByChanId(groupId.toInt()).name)
            pttService.registerObserverWithLifecycle(this@GroupFragment, object : PttObserver() {
                override fun onRecordFinished(messageBean: ChatMessageBean) {
                    super.onRecordFinished(messageBean)
                    if (messageBean.cid != null) {
                        val messageListAdapter = recyclerViewMessages.adapter as MessageListAdapter
                        messageListAdapter.prependData(arrayListOf(messageBean))
                        recyclerViewMessages.post {
                            recyclerViewMessages.scrollToPosition(0)
                        }
                    }
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
                groupViewModel.loadMoreMessages(
                    groupId.toInt(), pttService, false,
                    recyclerViewMessages.adapter as MessageListAdapter,
                    recyclerViewMessages.layoutManager as RecyclerView.LayoutManager,
                    mHandler
                )
                layoutSwipeRefresh.isRefreshing = false
            }
            btnShowActions.setOnClickListener {
                layoutActions.visibility = if(layoutActions.visibility == View.GONE) View.VISIBLE else View.GONE
            }
            groupViewModel.loadMoreMessages(
                groupId.toInt(), pttService, true,
                recyclerViewMessages.adapter as MessageListAdapter,
                recyclerViewMessages.layoutManager as RecyclerView.LayoutManager,
                mHandler
            )
            groupViewModel.loading.observe(this@GroupFragment) {
                println("loading==========> = $it")
                if (it) {
                    progressLoading.visible()
                } else {
                    progressLoading.gone(200)
                }
            }
            LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(receiver, IntentFilter(HostActivity.ACTION_FILE_MESSAGE))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }

    private fun sendTextMessage(pttService: InterpttService, text: CharSequence) {
        launch {
            val currentUser = pttService.currentUser
            groupViewModel.loading.value = true
            val response = api.sendFileOrTextAync(
                userId = currentUser.iId.toString().toRequestBody(),
                nickname = currentUser.nick.toRequestBody(),
                channelId = currentUser.channel.id.toString().toRequestBody(),
                contentType = "1".toRequestBody(),
                content = text.toString().toRequestBody()
            )
            groupViewModel.loading.value = false
            println("response.body() = $response")
            Toast.makeText(
                requireContext(),
                response.text,
                Toast.LENGTH_LONG
            ).show()
            if(response.success){
                editTextMessage.text = null
            }
        }
    }

    private fun sendFileMessage(
        pttService: InterpttService,
        uri: MutableList<Uri>,
        path: MutableList<String>
    ) {
        val currentUser = pttService.currentUser
        groupViewModel.loading.value = true
        val file = FileUtils.getFile(requireContext(), uri.first())

        //userId = currentUser.iId.toString().toRequestBody(),
        //nickname = currentUser.nick.toRequestBody(),
        //channelId = currentUser.channel.id.toString().toRequestBody(),
        //contentType = "2".toRequestBody(),
        //file = file.asRequestBody()
        println("currentUser = ${currentUser.iId}")

        val response = api.sendFileOrText(
//            MultipartBody.Builder()
//                .addFormDataPart(
//                    "upfile",
//                    file.name,
//                    RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
//                )
//                .addFormDataPart("t_uid", currentUser.iId.toString())
//                .addFormDataPart("nick_name", currentUser.nick)
//                .addFormDataPart("c_id", currentUser.channel.id.toString())
//                .addFormDataPart("content_type", "2")
//                .build()
            userId = currentUser.iId.toString().toRequestBody(),
            nickname = currentUser.nick.toRequestBody(),
            channelId = currentUser.channel.id.toString().toRequestBody(),
            extension = file.name.substring(file.name.lastIndexOf(".") + 1).toRequestBody(),
            contentType = "2".toRequestBody(),
            file = file.asRequestBody(),
            fileName = file.name.toRequestBody()
        )
        response.enqueue(object : Callback<BaseResp<Any>> {

            override fun onResponse(
                call: Call<BaseResp<Any>>,
                response: Response<BaseResp<Any>>
            ) {
                groupViewModel.loading.value = false
                println("response.body() = ${response.body()}")
                Toast.makeText(
                    requireContext(),
                    response.body()?.text,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<BaseResp<Any>>, t: Throwable) {
                groupViewModel.loading.value = false
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initialList(messages: MutableList<Any>, myId: Int) {
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
        }
    }

    private fun onMessageClicked(message: Any, view: View, position: Int) {
        if (message is ChatMessageBean) {
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
    }

    private fun onUserClicked(message: Any, view: View) {
        if (message is ChatMessageBean) {
            navigate(
                R.id.userInfoFragment,
                UserInfoFragmentArgs.Builder(message.uid.toString()).build().toBundle()
            )
        }
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