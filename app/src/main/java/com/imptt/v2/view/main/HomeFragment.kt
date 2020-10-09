package com.imptt.v2.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.imptt.v2.R
import com.imptt.v2.core.messenger.connections.MESSAGE_DATA_KEY_GROUP_LIST
import com.imptt.v2.core.messenger.connections.MESSAGE_TYPE_GROUP_LIST
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.websocket.Group
import com.imptt.v2.vm.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var homeViewModelAssistedFactory: HomeViewModel.AssistedFactory
    private  val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.provideFactory(
            homeViewModelAssistedFactory,
            100L
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ViewMessenger.on(MESSAGE_TYPE_GROUP_LIST){
            //获取group列表
            val groups =
                it.data.getParcelableArrayList<Group>(MESSAGE_DATA_KEY_GROUP_LIST)?: arrayListOf()
            Toast.makeText(requireContext(), "$groups", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        textView.setOnClickListener {
            homeViewModel.say()
        }
        return root
    }
}