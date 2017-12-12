package rubyhuntersky.com.deltapie

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class DeltaPieView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRed: Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRed) {

    private var fillInsetPixels = 16.toPixels()

    private val arrowWingPixels = 4.toPixels()
    private val arrowWingDeltaX = arrowWingPixels * 1.25f
    private var centerX: Float = 0f
    private var centerY: Float = 0f


    var delta: Float = 0f
        set(value) {
            field = Math.max(-1f, Math.min(1f, value))
            updateArrowClipPath()
            invalidate()
        }

    private fun updateArrowClipPath() {
        with(arrowClipPath) {
            reset()
            moveTo(centerX, centerY)
            val absDeltaDegrees = Math.abs(deltaDegrees)
            arcTo(boundsRect, -90f - absDeltaDegrees, absDeltaDegrees)
            close()
        }
    }

    private val deltaDegrees get() = 360f * delta

    private val boundsRect = RectF()
    private val fillRect = RectF()
    private val arcRect = RectF()
    private val arrowClipPath = Path()

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
            val halfInset = fillInsetPixels / 2 - 1
            set(boundsRect)
            inset(halfInset, halfInset)
        }
        centerX = boundsRect.centerX()
        centerY = boundsRect.centerY()
        updateArrowClipPath()
    }

    override fun onDraw(canvas: Canvas) {
        with(canvas) {
            if (deltaDegrees > 0) {
                drawEnlargementPie()
            } else {
                drawReductionPie()
            }
        }
    }

    private fun Canvas.drawEnlargementPie() {
        drawArc(fillRect, -90f, 360f - deltaDegrees, true, fillPaint)

        save()
        rotate(-deltaDegrees, centerX, centerY)
        drawLine(centerX, centerY, centerX, 0f, linePaint)
        restore()

        drawLine(centerX, fillInsetPixels, centerX, 0f, linePaint)
        drawArc(arcRect, 270f - deltaDegrees, deltaDegrees, false, linePaint)

        save()
        if (Math.abs(deltaDegrees) < 45f) {
            clipPath(arrowClipPath)
        }
        val arrowTipY = fillInsetPixels / 2
        val arrowTipX = centerX - 2
        val arrowWingX = centerX - arrowWingDeltaX
        val arrowWingY1 = arrowTipY - arrowWingPixels
        val arrowWingY2 = arrowTipY + arrowWingPixels
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY1, linePaint)
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY2, linePaint)
        restore()
    }

    private fun Canvas.drawReductionPie() {
        drawOval(fillRect, fillPaint)
        drawArc(arcRect, -90f, deltaDegrees, false, linePaint)
        drawLine(centerX, centerY, centerX, 0f, linePaint)

        save()
        rotate(deltaDegrees, centerX, centerY)
        drawLine(centerX, fillInsetPixels, centerX, 0f, linePaint)
        restore()

        save()
        if (Math.abs(deltaDegrees) < 45f) {
            clipPath(arrowClipPath)
        }
        rotate(deltaDegrees, centerX, centerY)
        save()
        val arrowTipY = fillInsetPixels / 2
        val arrowTipX = centerX + 2
        val arrowWingX = centerX + arrowWingDeltaX
        val arrowWingY1 = arrowTipY - arrowWingPixels
        val arrowWingY2 = arrowTipY + arrowWingPixels
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY1, linePaint)
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY2, linePaint)
        restore()
        restore()
    }

    private fun Int.toPixels(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)
    }
}