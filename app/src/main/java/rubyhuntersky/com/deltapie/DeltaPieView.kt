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

    private val fillColor = Color.parseColor("#aabbcc")
    private var fillInsetPx = 8.toPixels()

    private fun Int.toPixels(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fillColor }
    private val fillRectF = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        with(fillRectF) {
            set(0f, 0f, w.toFloat(), h.toFloat())
            inset(fillInsetPx, fillInsetPx)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawOval(fillRectF, fillPaint)
    }
}