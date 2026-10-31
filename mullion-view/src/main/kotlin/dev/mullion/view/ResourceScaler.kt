package dev.mullion.view

import android.content.Context
import android.util.TypedValue
import androidx.annotation.DimenRes
import dev.mullion.core.MullionMetrics
import dev.mullion.core.ResourceBucket
import dev.mullion.core.TypeScale

/**
 * Resource resolution that does not need a new `values-wXXXdp` directory per
 * design decision.
 *
 * There are two layers:
 *
 *  1. [scaled] reads a dimension from the `values/`, `values-w600dp/` or
 *     `values-w840dp/` bucket. The platform picks the bucket at configuration
 *     time; there is no reflection.
 *  2. [metricsFor] resolves the same values from a *width number*. This is what
 *     previews, simulated viewports and tests use, and it produces identical
 *     numbers to the bucket files.
 *
 * Existing projects can migrate one dimension at a time:
 * `values-w820dp/dimens.xml` → [metricsFor] or the MULLION bucket resources.
 */
public object ResourceScaler {

    /** The bucket a width belongs to. */
    public fun bucketFor(widthDp: Int): ResourceBucket = ResourceBucket.forWidth(widthDp)

    /** The `values-…` qualifier for a width, useful in diagnostics. */
    public fun qualifierFor(widthDp: Int): String = bucketFor(widthDp).qualifier

    /** The full ruler for a width, without a Context. */
    public fun metricsFor(widthDp: Int): MullionMetrics = MullionMetrics.forWidth(widthDp)

    /** The type step for a width, without a Context. */
    public fun typeScaleFor(widthDp: Int): TypeScale = TypeScale.forWidth(widthDp)

    /**
     * Reads a dimension that already has a `values-w600dp` / `values-w840dp`
     * variant, in pixels.
     */
    public fun scaled(context: Context, @DimenRes resId: Int): Float =
        context.resources.getDimension(resId)

    /** Section spacing in pixels for a simulated width. */
    public fun sectionSpacingPx(context: Context, widthDp: Int): Float =
        dpToPx(context, metricsFor(widthDp).sectionSpacingDp)

    /** Display type size in pixels for a simulated width. */
    public fun displayTypePx(context: Context, widthDp: Int): Float =
        spToPx(context, typeScaleFor(widthDp).fontSizeSp)

    /** Pane gutter in pixels for a simulated width. */
    public fun paneGutterPx(context: Context, widthDp: Int): Float =
        dpToPx(context, metricsFor(widthDp).paneGutterDp)

    /** Pane margin in pixels for a simulated width. */
    public fun paneMarginPx(context: Context, widthDp: Int): Float =
        dpToPx(context, metricsFor(widthDp).paneMarginDp)

    /** Converts dp to pixels. */
    public fun dpToPx(context: Context, dp: Int): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics,
        )

    /** Converts sp to pixels, honoring the user's font scale. */
    public fun spToPx(context: Context, sp: Float): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics,
        )

    /**
     * Picks a dimension value by width class without a resource lookup.
     */
    public fun chooseDp(
        widthDp: Int,
        compact: Int,
        expanded: Int = compact,
        large: Int = expanded,
    ): Int = when (bucketFor(widthDp)) {
        ResourceBucket.Default -> compact
        ResourceBucket.W600 -> expanded
        ResourceBucket.W840 -> large
    }
}
