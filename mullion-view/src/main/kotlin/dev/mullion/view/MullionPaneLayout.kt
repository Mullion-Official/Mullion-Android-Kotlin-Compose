package dev.mullion.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import dev.mullion.core.Bp
import dev.mullion.core.PaneStrategy
import kotlin.math.roundToInt

/**
 * A one / two / three pane ViewGroup driven by the shared width policy.
 *
 * Children are placed left to right with a gutter; the number of panes comes
 * from [PaneStrategy]. Extra children are measured to zero rather than removed,
 * so their state survives a resize. Useful for porting an existing View screen
 * to adaptive layouts without rewriting it in Compose.
 */
public open class MullionPaneLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {

    /** Number of panes laid out in the last measure. */
    public var paneCount: Int = 1
        private set

    /** The width class used by the last measure. */
    public var breakpoint: Bp = Bp.Compact
        private set

    /** Called when the pane count changes. */
    public var onPaneCountChanged: ((Int) -> Unit)? = null

    private var minPaneWidthPx: Int = (280 * resources.displayMetrics.density).roundToInt()
    private var gutterPx: Int = (24 * resources.displayMetrics.density).roundToInt()
    private var maxPanes: Int = 3

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MullionPaneLayout)
            minPaneWidthPx = a.getDimensionPixelSize(
                R.styleable.MullionPaneLayout_mullionMinPaneWidth,
                minPaneWidthPx,
            )
            gutterPx = a.getDimensionPixelSize(
                R.styleable.MullionPaneLayout_mullionPaneGutter,
                gutterPx,
            )
            maxPanes = a.getInt(R.styleable.MullionPaneLayout_mullionMaxPanes, maxPanes)
            a.recycle()
        }
    }

    /**
     * Sets the minimum width a pane may have before the layout drops a pane.
     */
    public fun setMinPaneWidthDp(dp: Int) {
        minPaneWidthPx = (dp * resources.displayMetrics.density).roundToInt()
        requestLayout()
    }

    /** Sets the gutter between panes. */
    public fun setPaneGutterDp(dp: Int) {
        gutterPx = (dp * resources.displayMetrics.density).roundToInt()
        requestLayout()
    }

    /** Caps the number of panes regardless of width class. */
    public fun setMaxPanes(panes: Int) {
        maxPanes = panes.coerceIn(1, 3)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val widthDp = (width / resources.displayMetrics.density).roundToInt()
        breakpoint = Bp.of(widthDp)

        val childCount = childCount
        val innerWidth = (width - paddingLeft - paddingRight).coerceAtLeast(0)
        val strategy = PaneStrategy(maxPanes = maxPanes)
        var count = strategy.countFor(breakpoint).coerceAtMost(childCount)
            .coerceAtLeast(if (childCount > 0) 1 else 0)

        // Drop a pane rather than squeeze below the minimum readable width.
        while (count > 1) {
            val candidate = (innerWidth - gutterPx * (count - 1)).coerceAtLeast(0) / count
            if (candidate >= minPaneWidthPx) break
            count--
        }

        val changed = paneCount != count
        paneCount = count

        val available = (innerWidth - gutterPx * (count - 1).coerceAtLeast(0))
            .coerceAtLeast(0)
        val paneWidth = if (count == 0) 0 else available / count
        val paneHeight = (height - paddingTop - paddingBottom).coerceAtLeast(0)

        val childWidthSpec = MeasureSpec.makeMeasureSpec(paneWidth, MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(paneHeight, MeasureSpec.EXACTLY)

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (i < count) {
                child.measure(childWidthSpec, childHeightSpec)
            } else {
                child.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
                )
            }
        }

        setMeasuredDimension(width, height)
        if (changed) onPaneCountChanged?.invoke(count)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val innerWidth = (r - l - paddingLeft - paddingRight).coerceAtLeast(0)
        val available = (innerWidth - gutterPx * (paneCount - 1).coerceAtLeast(0))
            .coerceAtLeast(0)
        val paneWidth = if (paneCount == 0) 0 else available / paneCount
        var x = paddingLeft

        for (i in 0 until paneCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            child.layout(
                x,
                paddingTop,
                x + paneWidth,
                b - t - paddingBottom,
            )
            x += paneWidth + gutterPx
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams =
        LayoutParams(context, attrs)

    override fun checkLayoutParams(p: LayoutParams?): Boolean = p is LayoutParams
}
