package com.nasahacker.nasacolorpicker.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class PickerImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var circleX: Float = -1F
    private var circleY: Float = -1F
    private val circlePaint: Paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    fun updateCache(x: Float, y: Float) {
        val validX = x.takeIf { it >= 0 && it <= width.toFloat() } ?: -1F
        val validY = y.takeIf { it >= 0 && it <= height.toFloat() } ?: -1F

        circleX = validX
        circleY = validY
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (circleX >= 0 && circleY >= 0) {
            canvas.drawCircle(circleX, circleY, 50f, circlePaint)
        }
    }
}
