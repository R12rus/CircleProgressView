package com.r12

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

@Suppress("MemberVisibilityCanBePrivate")
internal class Circle(var diameter: Double) {

    val radius: Double
        get() = diameter / 2

    var centerX: Float = 0F
    var centerY: Float = 0F
    var color: Int
        get() = paint.color
        set(value) {
            if (paint.color != value) {
                paint.color = value
            }
        }
    var isVisible = false

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE
        strokeWidth = diameter.toFloat()
    }

    fun draw(canvas: Canvas) {
        if (isVisible) canvas.drawCircle(centerX, centerY, radius.toFloat(), paint)
    }

}