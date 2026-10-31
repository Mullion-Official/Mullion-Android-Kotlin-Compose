package dev.mullion.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView

/** Button variants for the View tier. */
public enum class MullionButtonVariant {
    Filled,
    Tonal,
    Outlined,
    Text,
}

/**
 * The View-compatible mullion button.
 *
 * It is a single TextView with no background drawable, which means it can be
 * dropped into an existing XML screen without a theme migration. The pill,
 * border and press wash are drawn on the canvas; text remains a normal TextView
 * and keeps all accessibility behavior.
 */
public open class MullionButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : TextView(context, attrs, defStyleAttr) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1f)
    }
    private val pressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(26, 255, 255, 255)
    }
    private val focusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(3f)
        color = Color.argb(46, 53, 232, 255)
    }
    private val rect = RectF()
    private var shader: LinearGradient? = null

    /** Current variant. */
    public var variant: MullionButtonVariant = MullionButtonVariant.Filled
        set(value) {
            field = value
            applyTextColor()
            invalidate()
        }

    init {
        gravity = Gravity.CENTER
        typeface = Typeface.MONOSPACE
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        isClickable = true
        isFocusable = true
        setPadding(dp(18), dp(10), dp(18), dp(10))
        minimumHeight = dp(44)
        minimumWidth = dp(48)
        setWillNotDraw(false)
        includeFontPadding = false

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MullionButtonView)
            variant = when (a.getInt(R.styleable.MullionButtonView_mullionButtonVariant, 0)) {
                1 -> MullionButtonVariant.Tonal
                2 -> MullionButtonVariant.Outlined
                3 -> MullionButtonVariant.Text
                else -> MullionButtonVariant.Filled
            }
            a.recycle()
        }
        applyTextColor()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shader = if (w > 0 && h > 0) {
            LinearGradient(
                0f,
                0f,
                0f,
                h.toFloat(),
                MullionPalette.VoltHigh,
                MullionPalette.Volt,
                Shader.TileMode.CLAMP,
            )
        } else {
            null
        }
    }

    override fun draw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w > 0f && h > 0f) {
            rect.set(0f, 0f, w, h)
            val radius = h / 2f
            when (variant) {
                MullionButtonVariant.Filled -> {
                    fillPaint.shader = shader
                    canvas.drawRoundRect(rect, radius, radius, fillPaint)
                }

                MullionButtonVariant.Tonal -> {
                    fillPaint.shader = null
                    fillPaint.color = Color.argb(33, 255, 255, 255)
                    canvas.drawRoundRect(rect, radius, radius, fillPaint)
                    borderPaint.color = Color.argb(33, 255, 255, 255)
                    rect.inset(0.5f, 0.5f)
                    canvas.drawRoundRect(rect, radius, radius, borderPaint)
                    rect.inset(-0.5f, -0.5f)
                }

                MullionButtonVariant.Outlined -> {
                    borderPaint.color = Color.argb(140, 53, 232, 255)
                    rect.inset(0.5f, 0.5f)
                    canvas.drawRoundRect(rect, radius, radius, borderPaint)
                    rect.inset(-0.5f, -0.5f)
                }

                MullionButtonVariant.Text -> Unit
            }

            if (isPressed) {
                canvas.drawRoundRect(rect, radius, radius, pressPaint)
            }
            if (isFocused) {
                rect.inset(-1f, -1f)
                canvas.drawRoundRect(rect, radius, radius, focusPaint)
                rect.inset(1f, 1f)
            }
        }
        super.draw(canvas)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        invalidate()
    }

    private fun applyTextColor() {
        setTextColor(
            when (variant) {
                MullionButtonVariant.Filled -> MullionPalette.OnVolt
                MullionButtonVariant.Tonal -> MullionPalette.Ink
                MullionButtonVariant.Outlined,
                MullionButtonVariant.Text,
                -> MullionPalette.Volt
            },
        )
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
