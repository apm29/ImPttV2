package com.imptt.v2.core.struct

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.transition.AutoTransition
import androidx.transition.Slide
import com.imptt.v2.R
import com.imptt.v2.utils.LocalStorage
import com.imptt.v2.utils.findPrimaryNavController
import com.imptt.v2.utils.observe
import com.imptt.v2.view.HostActivity
import com.kylindev.pttlib.service.InterpttService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


abstract class BaseFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val ioContext: CoroutineContext
        get() = Dispatchers.IO

    val tagFragment: String
        get() = this::class.java.simpleName

    open val showToolBar: Boolean = true
    open val useTransitions: Boolean = true
    open val showBackArrow: Boolean = true
    val mHandler: Handler = Handler(Looper.getMainLooper())
    val localStorage: LocalStorage by lazy {
        LocalStorage.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(setupViewLayout(savedInstanceState), container, false)
    }


    abstract fun setupViewLayout(savedInstanceState: Bundle?): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useTransitions) {
            setTransitions()
        }
        initData(savedInstanceState)
        println("$tagFragment.onCreate")
        println(lifecycle.currentState)
    }

    override fun onStart() {
        super.onStart()
        println("$tagFragment.onStop")
        println(lifecycle.currentState)
    }

    override fun onResume() {
        super.onResume()
        println("$tagFragment.onResume")
        println(lifecycle.currentState)
    }

    override fun onPause() {
        super.onPause()
        println("$tagFragment.onPause")
        println(lifecycle.currentState)
    }

    override fun onStop() {
        super.onStop()
        println("$tagFragment.onStop")
        println(lifecycle.currentState)
    }

    override fun onDestroy() {
        super.onDestroy()
        println("$tagFragment.onDestroy")
        println(lifecycle.currentState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (showToolBar) {
            val activity = requireActivity()
            if (activity is AppCompatActivity) {
                val toolBar = view.findViewById<Toolbar>(R.id.toolBar)
                if (toolBar != null) {
                    activity.setSupportActionBar(toolBar)
                    activity.supportActionBar?.setDisplayHomeAsUpEnabled(showBackArrow)
                    val appBarConfiguration = AppBarConfiguration(findPrimaryNavController().graph)
                    NavigationUI.setupWithNavController(
                        toolBar,
                        findPrimaryNavController(),
                        appBarConfiguration
                    )

                }
            }
        }
        setupViews(view, savedInstanceState)
    }

    abstract fun setupViews(view: View, savedInstanceState: Bundle?)

    private fun hideToolBarArrow() {
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    protected fun hideIme() {
        val inputMethodManager: InputMethodManager? =
            getSystemService(requireContext(), InputMethodManager::class.java)
        val windowToken = requireActivity().window?.decorView?.findFocus()?.windowToken
        if (windowToken != null) {
            inputMethodManager?.hideSoftInputFromWindow(
                windowToken, 0
            )
        }
    }

    val mService: InterpttService?
        get() {
            val hostActivity = requireActivity() as HostActivity
            return hostActivity.mService
        }


    fun setToolbarTitle(title: String?) {
        val toolBar = view?.findViewById<Toolbar>(R.id.toolBar)
        toolBar?.title = title
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
//        val transitionSet = TransitionSet()
//        transitionSet.ordering = TransitionSet.ORDERING_TOGETHER
//        transitionSet.addTransition(Explode().apply { this.duration = duration })
//        transitionSet.addTransition(ChangeBounds().apply { this.duration = duration })
        enterTransition = Slide(Gravity.END).apply { this.mode = Slide.MODE_IN }
        exitTransition = Slide(Gravity.START).apply { this.mode = Slide.MODE_OUT }
        reenterTransition = Slide(Gravity.START).apply { this.mode = Slide.MODE_IN }
        returnTransition = Slide(Gravity.END).apply { this.mode = Slide.MODE_OUT }
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

    //设置result,pop后前一fragment可以通过observeResult监听result
    protected fun <T> setResult(key: String, value: T, finish: Boolean = true): Boolean {
        val findNavController = findPrimaryNavController()
        val previousBackStackEntry = findNavController.previousBackStackEntry
        previousBackStackEntry?.savedStateHandle?.set(key, value)
        return if (finish) findNavController.popBackStack() else false
    }

    protected fun <T> observeResult(key: String, observer: Observer<T>) {
        val data =
            findPrimaryNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
        //先移除该observer,否则相同observer绑定到不同的lifecycleOwner会报错
        data?.removeObserver(observer)
        observe<T>(
            data, observer
        )
    }

}