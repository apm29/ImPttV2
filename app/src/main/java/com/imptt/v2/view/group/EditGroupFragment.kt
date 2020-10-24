package com.imptt.v2.view.group

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.utils.requirePttService
import com.imptt.v2.vm.EditGroupViewModel
import kotlinx.android.synthetic.main.fragment_edit_group.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditGroupFragment : BaseFragment() {

    companion object {
        const val KEY_SAVE_GROUP_RESULT = "KEY_SAVE_GROUP_RESULT"
    }

    private val groupId: String? by lazy {
        if (arguments != null) {
            EditGroupFragmentArgs.fromBundle(requireArguments()).groupId
        } else {
            null
        }
    }

    private val editGroupViewModel: EditGroupViewModel by viewModel()

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_edit_group
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        if (groupId == null) {
            setToolbarTitle("新建群组")
        } else {
            buttonSaveGroup.text = "保存"
        }
        buttonSaveGroup.setOnClickListener(::saveGroup)
    }

    private fun saveGroup(view: View?) {
        launch {
//            editGroupViewModel.addGroup(
//                editTextGroupName.text.toString(),
//                imageViewGroupIcon.imageUrl
//            )

            requirePttService().createChannel(
                editTextGroupName.text?.trim().toString(),
                "Null", "Null", true, false
            )
            delay(300)
            //保存群组信息
            setResult(KEY_SAVE_GROUP_RESULT, true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_group_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.saveGroup) {
            saveGroup(item.actionView)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.saveGroup).title = if (groupId == null) "创建群组" else "保存"
        super.onPrepareOptionsMenu(menu)
    }
}