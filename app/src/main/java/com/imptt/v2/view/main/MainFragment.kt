package com.imptt.v2.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment() {

    override val showToolBar: Boolean = false

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_main
    }

    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        NavigationUI.setupWithNavController(
            bottomNavigator,
            Navigation.findNavController(requireActivity(), R.id.nav_main_fragment)
        )
    }

}