package com.imptt.v2.view.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.imptt.v2.R
import com.imptt.v2.core.messenger.connections.*
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.websocket.Group
import com.imptt.v2.utils.observe
import com.imptt.v2.view.adapter.GroupListAdapter
import com.imptt.v2.vm.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val homeViewModel:HomeViewModel by viewModel(
        owner = { ViewModelOwner.from(this.requireActivity(), this.requireActivity()) }
    )

    /**
     * Called when a fragment is first attached to its context.
     * [.onCreate] will be called after this.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("HomeFragment.onAttach")
    }

    override fun onDetach() {
        super.onDetach()
        println("HomeFragment.onDetach")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("HomeFragment.onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("HomeFragment.onDestroyView")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("HomeFragment.onCreate")
        ViewMessenger.on(MESSAGE_TYPE_GROUP_LIST) {
            //获取group列表
            val groups =
                it.data.getParcelableArrayList<Group>(MESSAGE_DATA_KEY_GROUP_LIST) ?: arrayListOf()
            if(this@HomeFragment.isAdded) {
                Toast.makeText(requireContext(), "$groups", Toast.LENGTH_SHORT).show()
                homeViewModel.setImGroups(groups)
            }
        }.on(MESSAGE_TYPE_IN_CALL) {
            //获取group列表
            val groupId =
                it.data.getString(MESSAGE_DATA_KEY_GROUP_ID)
            val from =
                it.data.getString(MESSAGE_DATA_KEY_FROM_USER_ID)
            Toast.makeText(requireContext(), "收到 $from 在 $groupId 的呼叫", Toast.LENGTH_SHORT).show()
        }.on(MESSAGE_TYPE_MESSAGE){
            Toast.makeText(
                requireContext(),
                it.data.getString(MESSAGE_DATA_KEY_MESSAGE),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("HomeFragment.onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        toggleButtonCall.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ViewMessenger.send(
                    MessageFactory.createCallMessage("test01")
                )
            } else {
                ViewMessenger.send(
                    MessageFactory.createEndCallMessage()
                )
            }
        }

        observe(homeViewModel.groups){
            initialList(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("HomeFragment.onCreateView")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    private fun initialList(groups: ArrayList<Group>) {
        recyclerViewGroupList.layoutManager = LinearLayoutManager(requireContext())
        if (recyclerViewGroupList.adapter == null) {
            recyclerViewGroupList.adapter = GroupListAdapter(groups, layoutInflater)
        } else {
            (recyclerViewGroupList.adapter as GroupListAdapter).newList(groups)
        }
    }
}