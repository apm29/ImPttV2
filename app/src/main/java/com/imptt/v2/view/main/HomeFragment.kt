package com.imptt.v2.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.imptt.v2.R
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