package rubyhuntersky.com.deltapie

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class DeltaPieView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRed: Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRed) {

    private var fillInsetPixels = 16.toPixels()

    private val arrowWingPixels = 4.toPixels()
    private val arrowWingDeltaX = arrowWingPixels * 1.25f

    private val startLineFloats = FloatArray(4)
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    private val reductionDegrees = 360f * 0.05f

    private val fillRect = RectF()
    private val arcRect = RectF()


    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = Color.parseColor("#aabbcc")
            }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = Color.parseColor("#667788")
                strokeWidth = 2.toPixels()
                style = Paint.Style.STROKE
            }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fillInsetPixels = Math.min(w, h) * 0.5f / 4f
        with(fillRect) {
            set(0f, 0f, w.toFloat(), h.toFloat())
            inset(fillInsetPixels, fillInsetPixels)
        }
        with(arcRect) {
            val halfInset = -(fillInsetPixels / 2 + 1)
            set(fillRect)
            inset(halfInset, halfInset)
        }
        centerX = fillRect.centerX()
        centerY = fillRect.centerY()
        with(startLineFloats) {
            set(0, centerX)
            set(1, centerY)
            set(2, centerX)
            set(3, 0f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        with(canvas) {
            drawOval(fillRect, fillPaint)
            drawArc(arcRect, -90f, -reductionDegrees, false, linePaint)
            drawLines(startLineFloats, linePaint)

            save()
            rotate(-reductionDegrees, centerX, centerY)
            drawLine(centerX, fillInsetPixels, centerX, 0f, linePaint)
            val arrowWingX = centerX + arrowWingDeltaX
            val arrowTipY = fillInsetPixels / 2
            val arrowWingY1 = arrowTipY - arrowWingPixels
            val arrowWingY2 = arrowTipY + arrowWingPixels
            drawLine(centerX, arrowTipY, arrowWingX, arrowWingY1, linePaint)
            drawLine(centerX, arrowTipY, arrowWingX, arrowWingY2, linePaint)
            restore()
        }
    }

    private fun Int.toPixels(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)
    }
}