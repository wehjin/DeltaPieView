package rubyhuntersky.com.deltapie

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.properties.Delegates

class DeltaPieView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRed: Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRed) {

    private var endMarkPixels = 16.toPixels()

    private val fillInsetPixels get() = endMarkPixels / 2
    private val arrowWingPixels get() = endMarkPixels / 2.5f
    private val arrowWingDeltaX get() = arrowWingPixels * 1.25f

    private var centerX: Float = 0f
    private var centerY: Float = 0f

    var neutralColor: Int by Delegates.observable(Color.parseColor("#3f51b5")) { _, _, new ->
        fillPaint.color = new
        invalidate()
    }

    var darkColor: Int by Delegates.observable(Color.parseColor("#f50057")) { _, _, new ->
        investmentStrokePaint.color = new
        divestmentFillPaint.color = new
        invalidate()
    }

    var lightColor: Int by Delegates.observable(Color.parseColor("#ff80ab")) { _, _, new ->
        divestmentStrokePaint.color = new
        investmentFillPaint.color = new
        invalidate()
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = neutralColor
            }

    private val investmentStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = darkColor
                strokeWidth = 2.toPixels()
                style = Paint.Style.STROKE
            }

    private val divestmentStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = lightColor
                strokeWidth = 2.toPixels()
                style = Paint.Style.STROKE
            }

    private val investmentFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = lightColor
            }

    private val divestmentFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = darkColor
            }

    var delta: Float = 0f
        set(value) {
            field = Math.max(-1f, Math.min(1f, value))
            updatePaths()
            invalidate()
        }

    private val strokePaint
        get() = if (delta > 0) {
            investmentStrokePaint
        } else {
            divestmentStrokePaint
        }

    private val vestmentPaint
        get() = if (delta > 0) {
            investmentFillPaint
        } else {
            divestmentFillPaint
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
                arcTo(boundsRect, -90f - absDeltaDegrees, absDeltaDegrees)
            } else {
                addOval(boundsRect, Path.Direction.CCW)
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        endMarkPixels = Math.min(w, h) * 0.5f * .4f
        boundsRect.set(0f, 0f, w.toFloat(), h.toFloat())
        with(fillRect) {
            set(boundsRect)
            inset(fillInsetPixels, fillInsetPixels)
        }
        with(arcRect) {
            set(boundsRect)
            inset(fillInsetPixels - 1, fillInsetPixels - 1)
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
        drawOval(fillRect, fillPaint)
        drawPath(wedgePath, vestmentPaint)

        save()
        rotate(-deltaDegrees, centerX, centerY)
        drawLine(centerX, centerY, centerX, 0f, strokePaint)
        restore()

        drawArc(arcRect, 270f - deltaDegrees, deltaDegrees, false, strokePaint)

        save()
        if (Math.abs(deltaDegrees) < 45f) {
            clipPath(arrowClipPath)
        }
        val arrowTipY = fillInsetPixels + 1
        val arrowTipX = centerX - 2
        val arrowWingX = centerX - arrowWingDeltaX
        val arrowWingY1 = arrowTipY - arrowWingPixels
        val arrowWingY2 = arrowTipY + arrowWingPixels
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY1, strokePaint)
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY2, strokePaint)
        restore()
    }

    private fun Canvas.drawDivestmentPie() {
        drawOval(fillRect, fillPaint)
        drawPath(wedgePath, vestmentPaint)

        drawArc(arcRect, -90f, deltaDegrees, false, strokePaint)
        drawLine(centerX, centerY, centerX, 0f, strokePaint)

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
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY1, strokePaint)
        drawLine(arrowTipX, arrowTipY, arrowWingX, arrowWingY2, strokePaint)
        restore()
        restore()
    }

    private fun Int.toPixels(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)
    }
}