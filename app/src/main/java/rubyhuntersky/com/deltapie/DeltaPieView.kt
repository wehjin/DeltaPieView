package rubyhuntersky.com.deltapie

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class DeltaPieView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRed: Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRed) {

    private var endMarkPixels = 16.toPixels()
    private val fillInsetPixels get() = endMarkPixels / 2

    private val arrowWingPixels = 4.toPixels()
    private val arrowWingDeltaX = arrowWingPixels * 1.25f
    private var centerX: Float = 0f
    private var centerY: Float = 0f


    var delta: Float = 0f
        set(value) {
            field = Math.max(-1f, Math.min(1f, value))
            updatePaths()
            invalidate()
        }

    private fun updatePaths() {
        with(arrowClipPath) {
            reset()
            moveTo(centerX, centerY)
            val absDeltaDegrees = Math.abs(deltaDegrees)
            arcTo(boundsRect, -90f - absDeltaDegrees, absDeltaDegrees)
            close()
        }
        with(wedgePath) {
            reset()
            val absDeltaDegrees = Math.abs(deltaDegrees)
            if (absDeltaDegrees < 360f) {
                moveTo(centerX, centerY)
                arcTo(fillRect, -90f - absDeltaDegrees, absDeltaDegrees)
            } else {
                addOval(fillRect, Path.Direction.CCW)
            }
            close()
        }
    }

    private val deltaDegrees get() = 360f * delta

    private val boundsRect = RectF()
    private val fillRect = RectF()
    private val arcRect = RectF()
    private val arrowClipPath = Path()
    private val wedgePath = Path()

    private val occupiedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = Color.parseColor("#aabbcc")
            }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = Color.parseColor("#667788")
                strokeWidth = 2.toPixels()
                style = Paint.Style.STROKE
            }

    private val investmentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply { color = Color.parseColor("#e0ffffff") }

    private val divestmentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply { color = Color.parseColor("#30000000") }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        endMarkPixels = Math.min(w, h) * 0.5f / 4f
        boundsRect.set(0f, 0f, w.toFloat(), h.toFloat())
        with(fillRect) {
            set(boundsRect)
            inset(fillInsetPixels, fillInsetPixels)
        }
        with(arcRect) {
            set(boundsRect)
            inset(fillInsetPixels, fillInsetPixels)
        }
        centerX = boundsRect.centerX()
        centerY = boundsRect.centerY()
        updatePaths()
    }

    override fun onDraw(canvas: Canvas) {
        with(canvas) {
            if (deltaDegrees > 0) {
                drawInvestmentPie()
            } else {
                drawDivestmentPie()
            }
        }
    }

    private fun Canvas.drawInvestmentPie() {
        drawOval(fillRect, occupiedPaint)
        drawPath(wedgePath, investmentPaint)
        //drawArc(fillRect, -90f, 360f - deltaDegrees, true, occupiedPaint)

        save()
        rotate(-deltaDegrees, centerX, centerY)
        drawLine(centerX, centerY, centerX, 0f, linePaint)
        restore()

        drawLine(centerX, endMarkPixels, centerX, 0f, linePaint)
        drawArc(arcRect, 270f - deltaDegrees, deltaDegrees, false, linePaint)

        save()
        if (Math.abs(deltaDegrees) < 45f) {
            clipPath(arrowClipPath)
        }
        val arrowTipY = fillInsetPixels
        val arrowTipX = centerX - 2
        val arrowWingX = centerX - arrowWingDeltaX
        val arrowWingY1 = arrowTipY - arrowWingPixels
        val arrowWingY2 = arrowTipY + arrowWingPixels
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY1, linePaint)
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY2, linePaint)
        restore()
    }

    private fun Canvas.drawDivestmentPie() {
        drawOval(fillRect, occupiedPaint)
        drawPath(wedgePath, divestmentPaint)
        drawArc(arcRect, -90f, deltaDegrees, false, linePaint)
        drawLine(centerX, centerY, centerX, 0f, linePaint)

        save()
        rotate(deltaDegrees, centerX, centerY)
        drawLine(centerX, endMarkPixels, centerX, 0f, linePaint)
        restore()

        save()
        if (Math.abs(deltaDegrees) < 45f) {
            clipPath(arrowClipPath)
        }
        rotate(deltaDegrees, centerX, centerY)
        save()
        val arrowTipY = fillInsetPixels
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