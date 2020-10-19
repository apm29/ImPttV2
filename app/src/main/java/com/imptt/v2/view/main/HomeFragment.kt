package com.imptt.v2.view.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.imptt.v2.R
import com.imptt.v2.core.messenger.connections.*
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.core.websocket.Group
import com.imptt.v2.utils.navigate
import com.imptt.v2.utils.observe
import com.imptt.v2.view.adapter.GroupListAdapter
import com.imptt.v2.view.group.GroupFragmentArgs
import com.imptt.v2.vm.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {

    private val homeViewModel: HomeViewModel by viewModel(
        owner = { ViewModelOwner.from(this.requireActivity(), this.requireActivity()) }
    )

    override fun setupViews(view: View, savedInstanceState: Bundle?) {

        ViewMessenger.on(MESSAGE_TYPE_GROUP_LIST) {
            //获取group列表
            val groups =
                it.data.getParcelableArrayList<Group>(MESSAGE_DATA_KEY_GROUP_LIST) ?: arrayListOf()
            if (this@HomeFragment.isAdded) {
                Toast.makeText(requireContext(), "$groups", Toast.LENGTH_SHORT).show()
                homeViewModel.setImGroups(groups)
            }
        }
        observe(homeViewModel.groups) {
            initialList(it)
        }
    }

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_home
    }

    private fun initialList(groups: ArrayList<Group>) {
        recyclerViewGroupList.layoutManager = LinearLayoutManager(requireContext())
        if (recyclerViewGroupList.adapter == null) {
            recyclerViewGroupList.adapter = GroupListAdapter(groups, layoutInflater, ::onGroupRoute)
        } else {
            (recyclerViewGroupList.adapter as GroupListAdapter).newList(groups)
        }
    }

    private fun onGroupRoute(group: Group, view: View) {
        navigate(
            R.id.groupFragment,
            GroupFragmentArgs.Builder(group.groupId).build().toBundle()
        )
    }
}