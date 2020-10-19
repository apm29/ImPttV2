package com.imptt.v2.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class PttButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int =0) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes){

    var pttButtonState: PttButtonState? = null


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN){
            isPressed = true
            pttButtonState?.onPressDown()
        } else if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL){
            pttButtonState?.onPressUp()
            isPressed = false
        }
        super.onTouchEvent(event)
        return true
    }


    interface PttButtonState{
        fun onPressDown(){
            println("PttButtonState.onPressDown")
        }
        fun onPressUp(){
            println("PttButtonState.onPressUp")
        }
    }

}
