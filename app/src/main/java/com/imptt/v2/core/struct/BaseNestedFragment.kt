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
import androidx.transition.*
import com.imptt.v2.R

abstract class BaseNestedFragment : Fragment() {

    val tagFragment: String
        get() = this::class.java.simpleName

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
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            val toolBar = view.findViewById<Toolbar>(R.id.toolBar)
            if (toolBar != null) {
                activity.setSupportActionBar(toolBar)
                activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
        setupViews(view, savedInstanceState)
    }

    abstract fun setupViews(view: View, savedInstanceState: Bundle?)


    fun setToolbarTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransitions()
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
        val duration: Long = 600
        enterTransition    = Explode().apply {
            this.duration = duration
            this.mode = Explode.MODE_IN
        }
        exitTransition     = Explode().apply {
            this.duration = duration
            this.mode = Explode.MODE_OUT
        }
        reenterTransition  = Explode().apply {
            this.duration = duration
            this.mode = Explode.MODE_IN
        }
        returnTransition   = Explode().apply {
            this.duration = duration
            this.mode = Explode.MODE_OUT
        }
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