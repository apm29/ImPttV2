package com.imptt.v2.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout


class FitSystemConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val mInsets = IntArray(4)
    override fun fitSystemWindows(insets: Rect): Boolean {
        // Intentionally do not modify the bottom inset. For some reason,
        // if the bottom inset is modified, window resizing stops working.
        mInsets[0] = insets.left
        mInsets[1] = insets.top
        mInsets[2] = insets.right
        insets.left = 0
        insets.top = 0
        insets.right = 0
        return super.fitSystemWindows(insets)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        mInsets[0] = insets.systemWindowInsetLeft
        mInsets[1] = insets.systemWindowInsetTop
        mInsets[2] = insets.systemWindowInsetRight
        return super.onApplyWindowInsets(
            insets.replaceSystemWindowInsets(
                0, 0, 0,
                insets.systemWindowInsetBottom
            )
        )
    }
}