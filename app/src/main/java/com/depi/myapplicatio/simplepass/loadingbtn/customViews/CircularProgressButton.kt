package com.depi.myapplicatio.simplepass.loadingbtn.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CircularProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var paint: Paint
    private lateinit var rectF: RectF
    private var progress: Float = 0f
    private var color: Int = Color.BLUE // default color

    init {
        initPaint()
    }

    private fun initPaint() {
        paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        rectF = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(rectF, 0f, 360f, false, paint)
        paint.color = color
        canvas.drawArc(rectF, 0f, progress * 360, false, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        rectF.set(10f, 10f, width - 10f, height - 10f)
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun setColor(color: Int) {
        this.color = color
        invalidate()
    }
}