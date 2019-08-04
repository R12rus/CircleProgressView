package com.r12

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import android.widget.FrameLayout
import com.r12.circleprogressview.R
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private const val START_ANGLE = 270.0
private const val DEFAULT_DURATION_IN_MILLIS = 2000L

class CircleProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Runnable {

    var circleDiameter = context.resources.getDimensionPixelSize(R.dimen.progressview_circle_radius).toDouble()
        set(value) {
            if (field != value && value > 0) {
                field = value
                circlesList.clear()
                requestLayout()
            }
        }
    val circleRadius: Double
        get() = (circleDiameter / 2)
    var circleColor: Int = Color.BLUE
        set(value) {
            if (field != value) {
                field = value
                updateColors()
            }
        }
    var durationInMillis = DEFAULT_DURATION_IN_MILLIS
        set(value) {
            if (field != value && value > 0) {
                field = value
                oneFractionTime = value / getMaxCirclesCount()
                run()
            }
        }
    var colorChooser: ColorChooser? = null
    var isInfinite = true
    set(value) {
        if (field != value) {
            field = value
            run()
        }
    }
    private var progress: Int = 0
        set(value) {
            if (field != value && value >= 0 && value <= 100) {
                field = value
                isInfinite = false
                run()
            }
        }
    private val circlesList = mutableListOf<Circle>()
    private var isReverse = false
    private var oneFractionTime = 0L

    init {
        setWillNotDraw(false)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CircleProgressView)
            circleDiameter = typedArray.getDimensionPixelSize(
                    R.styleable.CircleProgressView_circleprogressview_circle_diameter, circleDiameter.toInt()).toDouble()
            circleColor = typedArray.getColor(R.styleable.CircleProgressView_circleprogressview_circle_color, circleColor)
            durationInMillis = typedArray.getInt(R.styleable.CircleProgressView_circleprogressview_duration_in_millis,
                    durationInMillis.toInt()).toLong()
            isInfinite = typedArray.getBoolean(R.styleable.CircleProgressView_circleprogressview_infinite, true)
            progress = typedArray.getInt(R.styleable.CircleProgressView_circleprogressview_progress, 0)

            typedArray.recycle()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredHeight != measuredWidth) {
            val size = max(measuredHeight, measuredWidth)
            val measureSpec = makeMeasureSpec(size, EXACTLY)
            super.onMeasure(measureSpec, measureSpec)
        }

        val centerX = measuredWidth / 2
        val centerY = measuredHeight / 2

        val maxCirclesCount = getMaxCirclesCount()
        val xBias = centerX - circleRadius
        val yBias = centerY - circleRadius
        circlesList.clear()

        val circleAngle = Math.toDegrees(getCircleAngle())
        val space = (360 - circleAngle * maxCirclesCount) / maxCirclesCount

        val startAngle = START_ANGLE
        for (i in 0 until maxCirclesCount) {
            var angle = startAngle
            if (i != 0) {
                angle = startAngle + (circleAngle * i) + space * i
            }
            angle = Math.toRadians(angle)

            circlesList.add(Circle(circleDiameter).apply {
                this.centerX = (centerX + cos(angle) * xBias).toFloat()
                this.centerY = (centerY + sin(angle) * yBias).toFloat()
            })
        }
        oneFractionTime = durationInMillis / maxCirclesCount
        updateColors()
        run()
    }

    private fun updateColors() {
        if (colorChooser == null) {
            circlesList.forEach { it.color = circleColor }
        } else {
            circlesList.forEachIndexed { index, circle ->
                circle.color = colorChooser!!.getColor(index)
            }
        }
    }

    private fun getMaxCirclesCount(): Int {
        val circleAngle = getCircleAngle()
        val space = circleAngle / 2
        return ((2 * Math.PI) / (circleAngle + space)).toInt()
    }

    private fun getCircleAngle() = 2 * sin(circleRadius / ((measuredWidth / 2) - circleRadius))

    override fun run() {
        removeCallbacks(this)
        if (circlesList.isEmpty()) return

        if (isInfinite) {
            updateInfiniteModeVisibility()
        } else {
            updateProgressModeVisibility()
        }
        postInvalidate()
        postDelayed(this, oneFractionTime)
    }

    private fun updateInfiniteModeVisibility() {
        val lastCircle = circlesList.last()

        if (lastCircle.isVisible) {
            val index = circlesList.indexOfLast {
                !it.isVisible
            }
            when (index) {
                -1 -> circlesList.secondOrNull()
                else -> circlesList[index + 1]
            }?.isVisible = false
        } else {
            val index = circlesList.indexOfLast {
                it.isVisible
            }
            when (index) {
                -1 -> circlesList.firstOrNull()
                (circlesList.size - 1) -> circlesList.lastOrNull()
                else -> circlesList[index + 1]
            }?.isVisible = true
        }
    }


    private fun updateProgressModeVisibility() {
        if (progress == 100) {
            circlesList.forEach { it.isVisible = true }
            return
        }

        val maxCirclesCount = getMaxCirclesCount()
        val progressIndex: Int = maxCirclesCount * progress / 100
        if (progressIndex > circlesList.size - 1) return

        for (i in 0 until progressIndex) {
            circlesList[i].isVisible = true
        }

        if (isReverse) {
            val index = circlesList.indexOfLast {
                it.isVisible
            }
            circlesList[index].isVisible = false
            isReverse = index > progressIndex
        } else {
            val index = circlesList.indexOfFirst {
                !it.isVisible
            }
            circlesList[index].isVisible = true
            isReverse = index == circlesList.size - 1
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        circlesList.forEach {
            it.draw(canvas)
        }
    }

}

private fun <T> List<T>.secondOrNull(): T? = if (size < 2) null else this[1]