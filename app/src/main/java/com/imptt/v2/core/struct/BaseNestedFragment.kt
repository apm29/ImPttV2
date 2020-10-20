package com.imptt.v2.core.struct

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.transition.*
import com.imptt.v2.R
import com.imptt.v2.utils.findPrimaryNavController
import com.imptt.v2.utils.observe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

abstract class BaseNestedFragment : Fragment() ,CoroutineScope {

    /**
     * The context of this scope.
     * Context is encapsulated by the scope and used for implementation of coroutine builders that are extensions on the scope.
     * Accessing this property in general code is not recommended for any purposes except accessing the [Job] instance for advanced usages.
     *
     * By convention, should contain an instance of a [job][Job] to enforce structured concurrency.
     */
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val tagFragment: String
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
        enterTransition = Explode().apply {
            this.duration = duration
            this.mode = Explode.MODE_IN
        }
        exitTransition = Explode().apply {
            this.duration = duration
            this.mode = Explode.MODE_OUT
        }
        reenterTransition = Explode().apply {
            this.duration = duration
            this.mode = Explode.MODE_IN
        }
        returnTransition = Explode().apply {
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

    //设置result,pop后前一fragment可以通过observeResult监听result
    protected fun <T> setResult(key: String, value: T, finish: Boolean = true): Boolean {
        findPrimaryNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)?:Log.e(tagFragment,"保存数据失败:key=$key,value=$value")
        return if (finish) findPrimaryNavController().popBackStack() else false
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