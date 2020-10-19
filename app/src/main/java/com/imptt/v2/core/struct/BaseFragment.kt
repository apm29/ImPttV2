package com.imptt.v2.core.struct

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.transition.*
import com.imptt.v2.R

abstract class BaseFragment : Fragment() {

    val tagFragment: String
        get() = this::class.java.simpleName

    open val showToolBar: Boolean = true
    open val useTransitions: Boolean = true
    val mHandler: Handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(setupViewLayout(savedInstanceState), container, false)
    }

    abstract fun setupViewLayout(savedInstanceState: Bundle?): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (showToolBar) {
            val activity = requireActivity()
            if (activity is AppCompatActivity) {
                val toolBar = view.findViewById<Toolbar>(R.id.toolBar)
                if (toolBar != null) {
                    activity.setSupportActionBar(toolBar)
                    activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    NavigationUI.setupWithNavController(toolBar, findNavController())
                }
            }
        }
        setupViews(view, savedInstanceState)
    }

    abstract fun setupViews(view: View, savedInstanceState: Bundle?)

    fun hideToolBarArrow() {
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    fun setToolbarTitle(title:String?){
        val toolBar = view?.findViewById<Toolbar>(R.id.toolBar)
        toolBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useTransitions) {
            setTransitions()
        }
        initData(savedInstanceState)
    }

    /**
     * called just after onCreate
     * 1. liveData should be observed here
     * 2. network init request should be placed here for the sake of avoiding being the request called twice while returning to this fragment
     */
    protected open fun initData(savedInstanceState: Bundle?) {

    }

    protected open fun setTransitions() {
        setSharedElementTransitions()
        val transitionSet = TransitionSet()
        transitionSet.ordering = TransitionSet.ORDERING_TOGETHER
        transitionSet.addTransition(Explode().apply { this.duration = duration })
        transitionSet.addTransition(ChangeBounds().apply { this.duration = duration })
        enterTransition = transitionSet
        exitTransition = transitionSet
        reenterTransition = transitionSet
        returnTransition = transitionSet
    }

    protected open fun setSharedElementTransitions(duration: Long = 500) {
        sharedElementReturnTransition = AutoTransition().apply {
            this.duration = duration
            interpolator = BounceInterpolator()
        }
        sharedElementEnterTransition = AutoTransition().apply {
            this.duration = duration
            interpolator = BounceInterpolator()
        }
    }


}