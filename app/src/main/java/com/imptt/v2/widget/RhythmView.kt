package com.imptt.apm29.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.Nullable
import kotlin.math.pow
import kotlin.math.sin


/**
 *  author : ciih
 *  date : 2020/9/14 2:21 PM
 *  description :
 */
open class RhythmView(context: Context?, @Nullable attrs: AttributeSet?) :
    View(context, attrs) {
    private var mMaxHeight = 0.0F //最到点
    private var mPerHeight = 0.0F //最到点
    private var min //最小x
            = 0.0F
    private var max //最大x
            = 0.0F
    private var φ = 0.0 //初相
    private var A = mMaxHeight //振幅
    private var ω //角频率
            = 0.0
    private val mPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private var mPath //主路径
            : Path? = null
    private var mReflexPath //镜像路径
            : Path? = null
    private val mAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, (2 * Math.PI).toFloat())
    }
    private var mHeight = 0
    private var mWidth = 0

    constructor(context: Context?) : this(context, null) {}

    private fun init() {
        //初始化主画笔
        mPaint.color = Color.BLUE
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = dp(2f)
        //初始化主路径
        mPath = Path()
        mReflexPath = Path()
        //数字时间流
        mAnimator.duration = 1000
        mAnimator.repeatMode = ValueAnimator.RESTART
        mAnimator.interpolator = LinearInterpolator()
        mAnimator.addUpdateListener { a: ValueAnimator ->
            φ = (a.animatedValue as Float).toDouble()
            A =
                ((mMaxHeight * mPerHeight * (1 - a.animatedValue as Float / (2 * Math.PI))).toFloat())
            invalidate()
        }
    }

    fun setPerHeight(perHeight: Float) {
        mPerHeight = perHeight
        mAnimator.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mMaxHeight = mHeight / 2 * 0.9F
        min = -mWidth / 2F
        max = mWidth / 2F
        handleColor()
        setMeasuredDimension(mWidth, mHeight)
    }

    private fun handleColor() {
        val colors = intArrayOf(
            Color.parseColor("#33F60C0C"),  //红
            Color.parseColor("#F3B913"),  //橙
            Color.parseColor("#E7F716"),  //黄
            Color.parseColor("#3DF30B"),  //绿
            Color.parseColor("#0DF6EF"),  //青
            Color.parseColor("#0829FB"),  //蓝
            Color.parseColor("#33B709F4")
        )
        val pos = floatArrayOf(
            1f / 10, 2f / 7, 3f / 7, 4f / 7, 5f / 7, 9f / 10, 1f
        )
        mPaint.shader = LinearGradient(
            min, 0.0F, max, 0.0F,
            colors, pos,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        mPath?.reset()
        mReflexPath?.reset()
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(mWidth / 2F, mHeight / 2F)
        formPath()
        mPaint.alpha = 255
        mPath?.let { path ->
            canvas.drawPath(path, mPaint)
        }

        mPaint.alpha = 66
        mReflexPath?.let { path ->
            canvas.drawPath(path, mPaint)
        }
        canvas.restore()
    }

    /**
     * 对应法则
     *
     * @param x 原像(自变量)
     * @return 像(因变量)
     */
    private fun f(x: Number): Double {
        val len = max - min
        val a = 4 / (4 + rad(x.toDouble() / Math.PI * 800 / len).pow(4.0))
        val aa = a.pow(2.5)
        ω = 2 * Math.PI / (rad(len) / 2)
        return aa * A * sin(ω * rad(x) - φ)
    }

    private fun formPath() {
        mPath?.let { path ->
            mReflexPath?.let { reflexPath ->
                path.moveTo(min, f(min).toFloat())
                reflexPath.moveTo(min, f(min).toFloat())
                var x = min
                while (x <= max) {
                    val y = f(x)
                    path.lineTo(x, y.toFloat())
                    reflexPath.lineTo(x, (-y).toFloat())
                    x++
                }
            }
        }

    }

    private fun rad(deg: Number): Double {
        return deg.toDouble() / 180 * Math.PI
    }

    protected fun dp(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        )
    }

    init {
        init() //初始化
    }
}