package dev.mullion.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import kotlin.math.roundToInt

/**
 * A ViewGroup that paints the MULLION glass recipe.
 *
 * Gradient fill, 1dp edge, top hairline and an optional selected volt border.
 * Children are clipped to the rounded outline on API 21+. No bitmap capture is
 * used, so glass stays crisp through rotation and density changes.
 */
public open class MullionGlassLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1f)
    }
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(66, 255, 255, 255)
        strokeWidth = dp(1f)
    }
    private val rect = RectF()

    private var cornerRadiusPx: Float = dp(18f)
    private var glassAlpha: Float = 0.07f
    private var borderColor: Int = Color.argb(41, 255, 255, 255)
    private var selected: Boolean = false
    private var shader: LinearGradient? = null

    init {
        setWillNotDraw(false)
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadiusPx)
            }
        }
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MullionGlassLayout)
            cornerRadiusPx = a.getDimension(
                R.styleable.MullionGlassLayout_mullionCornerRadius,
                cornerRadiusPx,
            )
            glassAlpha = a.getFloat(
                R.styleable.MullionGlassLayout_mullionGlassAlpha,
                glassAlpha,
            )
            borderColor = a.getColor(
                R.styleable.MullionGlassLayout_mullionBorderColor,
                borderColor,
            )
            selected = a.getBoolean(
                R.styleable.MullionGlassLayout_mullionSelected,
                false,
            )
            a.recycle()
        }
    }

    /** Sets the corner radius in dp. */
    public fun setCornerRadiusDp(dp: Float) {
        cornerRadiusPx = dp(dp)
        shader = null
        invalidateOutline()
        invalidate()
    }

    /** Sets the glass fill intensity, roughly `0.02..0.16`. */
    public fun setGlassAlpha(alpha: Float) {
        glassAlpha = alpha.coerceIn(0f, 0.4f)
        shader = null
        invalidate()
    }

    /** Overrides the 1dp border color. */
    public fun setMullionBorderColor(color: Int) {
        borderColor = color
        invalidate()
    }

    /** Draws a volt border for selected cards. */
    public fun setMullionSelected(isSelected: Boolean) {
        selected = isSelected
        invalidate()
    }

    /** Current corner radius in dp. */
    public fun getCornerRadiusDp(): Float = cornerRadiusPx / resources.displayMetrics.density

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shader = if (w > 0 && h > 0) {
            LinearGradient(
                0f,
                0f,
                w.toFloat(),
                h.toFloat(),
                intArrayOf(
                    Color.argb((glassAlpha * 1.4f * 255).roundToInt().coerceIn(0, 255), 255, 255, 255),
                    Color.argb((glassAlpha * 0.5f * 255).roundToInt().coerceIn(0, 255), 255, 255, 255),
                    Color.argb((glassAlpha * 1.1f * 255).roundToInt().coerceIn(0, 255), 255, 255, 255),
                ),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP,
            )
        } else {
            null
        }
        invalidateOutline()
    }

    override fun draw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w > 0f && h > 0f) {
            rect.set(0f, 0f, w, h)
            fillPaint.shader = shader
            canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, fillPaint)

            borderPaint.color = if (selected) {
                Color.argb(168, 53, 232, 255)
            } else {
                borderColor
            }
            rect.inset(0.5f, 0.5f)
            canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, borderPaint)
            rect.inset(-0.5f, -0.5f)

            canvas.drawLine(
                cornerRadiusPx,
                0.5f,
                w - cornerRadiusPx,
                0.5f,
                highlightPaint,
            )
        }
        super.draw(canvas)
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}

/**
 * A glass card: 12dp radius, 18dp content padding and a 120dp minimum height.
 */
public open class MullionCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MullionGlassLayout(context, attrs, defStyleAttr) {

    init {
        setCornerRadiusDp(12f)
        setPadding(dp(18), dp(18), dp(18), dp(18))
        minimumHeight = dp(120)
        isClickable = true
        isFocusable = true
        elevation = dp(10).toFloat()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()
}
