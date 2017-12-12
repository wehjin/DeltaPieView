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


    var delta: Float = 0f
        set(value) {
            field = Math.max(-1f, Math.min(1f, value))
            invalidate()
        }

    private val deltaDegrees get() = 360f * delta

    private val boundsRect = RectF()
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
        boundsRect.set(0f, 0f, w.toFloat(), h.toFloat())
        with(fillRect) {
            set(boundsRect)
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
            if (deltaDegrees > 0) {
                return
            }
            drawOval(fillRect, fillPaint)
            drawArc(arcRect, -90f, deltaDegrees, false, linePaint)
            drawLines(startLineFloats, linePaint)

            save()
            rotate(deltaDegrees, centerX, centerY)
            drawLine(centerX, fillInsetPixels, centerX, 0f, linePaint)
            restore()

            save()
            if (deltaDegrees > -90) {
                canvas.clipRect(0f, 0f, centerX, centerY)
            }
            rotate(deltaDegrees, centerX, centerY)
            save()
            val arrowWingX = centerX + arrowWingDeltaX
            val arrowTipY = fillInsetPixels / 2
            val arrowTipX = centerX + 2
            val arrowWingY1 = arrowTipY - arrowWingPixels
            val arrowWingY2 = arrowTipY + arrowWingPixels
            drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY1, linePaint)
            drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY2, linePaint)
            restore()
            restore()
        }
    }

    private fun Int.toPixels(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)
    }
}