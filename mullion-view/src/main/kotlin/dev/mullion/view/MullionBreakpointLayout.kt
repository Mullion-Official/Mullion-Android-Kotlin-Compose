package dev.mullion.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import dev.mullion.core.Bp
import kotlin.math.roundToInt

/**
 * A FrameLayout that publishes its width class and can gate children by width.
 *
 * Children declare `mullionMinWidthDp` and/or `mullionMaxWidthDp`; outside that
 * range they are `GONE`, inside it they return to their declared visibility.
 * This replaces a pile of `values-wXXXdp` layout duplicates with one XML tree.
 *
 * Listeners fire only when the class changes, not on every pixel of a resize.
 */
public open class MullionBreakpointLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    /** The last measured width class. */
    public var breakpoint: Bp = Bp.Compact
        private set

    /** Width in dp from the last measure pass. */
    public var widthDp: Int = 0
        private set

    private val listeners = mutableListOf<(Bp) -> Unit>()
    private val declaredVisibility = mutableMapOf<View, Int>()
    private var dispatching = false

    public fun addOnBreakpointChangedListener(listener: (Bp) -> Unit) {
        listeners += listener
        listener(breakpoint)
    }

    public fun removeOnBreakpointChangedListener(listener: (Bp) -> Unit) {
        listeners -= listener
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        declaredVisibility[child] = child.visibility
    }

    override fun onViewRemoved(child: View) {
        declaredVisibility.remove(child)
        super.onViewRemoved(child)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthPx = MeasureSpec.getSize(widthMeasureSpec)
        val nextDp = (widthPx / resources.displayMetrics.density).roundToInt()
        val nextBp = Bp.of(nextDp)
        widthDp = nextDp

        if (nextBp != breakpoint) {
            breakpoint = nextBp
            applyChildVisibility(nextDp)
            dispatchBreakpoint()
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun applyChildVisibility(widthDp: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val params = child.layoutParams as? LayoutParams ?: continue
            val visible = widthDp >= params.minWidthDp && widthDp <= params.maxWidthDp
            val original = declaredVisibility[child] ?: View.VISIBLE
            child.visibility = if (visible) original else GONE
        }
    }

    private fun dispatchBreakpoint() {
        if (dispatching) return
        dispatching = true
        try {
            listeners.toList().forEach { it(breakpoint) }
        } finally {
            dispatching = false
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams =
        LayoutParams(context, attrs)


    /**
     * Per-child width gates. Defaults keep every child visible at every width.
     */
    public class LayoutParams : FrameLayout.LayoutParams {
        public var minWidthDp: Int = 0
        public var maxWidthDp: Int = Int.MAX_VALUE

        public constructor(width: Int, height: Int) : super(width, height)

        public constructor(source: FrameLayout.LayoutParams) : super(source)

        public constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.MullionLayoutParams)
            minWidthDp = a.getInt(R.styleable.MullionLayoutParams_mullionMinWidthDp, 0)
            maxWidthDp = a.getInt(
                R.styleable.MullionLayoutParams_mullionMaxWidthDp,
                Int.MAX_VALUE,
            )
            a.recycle()
        }
    }
}
