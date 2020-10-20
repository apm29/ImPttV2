package com.imptt.v2.view.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.imptt.v2.R
import com.imptt.v2.core.messenger.connections.MESSAGE_DATA_KEY_GROUP_LIST
import com.imptt.v2.core.messenger.connections.MESSAGE_TYPE_GROUP_LIST
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.struct.BaseNestedFragment
import com.imptt.v2.data.entity.Group
import com.imptt.v2.utils.navigate
import com.imptt.v2.utils.observe
import com.imptt.v2.view.adapter.GroupListAdapter
import com.imptt.v2.view.group.EditGroupFragment
import com.imptt.v2.view.group.GroupFragmentArgs
import com.imptt.v2.vm.HomeViewModel
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

        ViewMessenger.on(MESSAGE_TYPE_GROUP_LIST) {
            //获取group列表
            val groups =
                it.data.getParcelableArrayList<com.imptt.v2.core.websocket.Group>(MESSAGE_DATA_KEY_GROUP_LIST) ?: arrayListOf()
            if (this@HomeFragment.isAdded) {
                Toast.makeText(requireContext(), "$groups", Toast.LENGTH_SHORT).show()
                homeViewModel.setImGroups(groups)
            }
        }
        //监听群组变化
        observe(homeViewModel.groups) {
            initialList(it)
        }

        observeResult<Boolean>(EditGroupFragment.KEY_SAVE_GROUP_RESULT){
            println("${EditGroupFragment.KEY_SAVE_GROUP_RESULT}:$it")
        }
        println((requireActivity() as AppCompatActivity).supportActionBar?.title)
        setToolbarTitle("首页")
        setHasOptionsMenu(true)

        launch {
            homeViewModel.loadGroups()
        }

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
            R.id.action_mainFragment_to_groupFragment,
            GroupFragmentArgs.Builder(group.id.toString()).build().toBundle()
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        println("HomeFragment.onCreateOptionsMenu")
        println("menu = [${menu}], inflater = [${inflater}]")
        inflater.inflate(R.menu.home_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.addGroup){
            navigate(R.id.action_mainFragment_to_editGroupFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}