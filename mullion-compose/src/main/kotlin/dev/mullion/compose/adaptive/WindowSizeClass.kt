package dev.mullion.compose.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.window.currentBreakpoint
import dev.mullion.core.Bp
import dev.mullion.core.MullionMetrics
import dev.mullion.core.ResourceBucket
import dev.mullion.core.TypeScale

/**
 * A zero-cost slot that exposes the current width class.
 *
 * Screens that need a custom decision can use this instead of reading the window
 * directly, which keeps every breakpoint read in one place.
 */
@Composable
public fun WindowSizeClass(content: @Composable (Bp) -> Unit) {
    content(currentBreakpoint())
}

/** Remembers nothing: the class is already a value in the composition local. */
@Composable
public fun rememberBreakpoint(): Bp = currentBreakpoint()

/**
 * The shared ruler for Compose code.
 *
 * These values mirror the `values-w600dp` / `values-w840dp` buckets shipped by
 * `mullion-view`, so a Compose screen and an XML screen resolve identically.
 */
@Composable
public fun mullionMetrics(): MullionMetrics = MullionMetrics.forBp(currentBreakpoint())

/** The current resource bucket, useful for analytics and debugging. */
@Composable
public fun mullionResourceBucket(): ResourceBucket =
    ResourceBucket.forBp(currentBreakpoint())

/** The current type step. */
@Composable
public fun mullionTypeScale(): TypeScale = TypeScale.forBp(currentBreakpoint())

/**
 * Picks a dp value for the current width class.
 *
 * ```
 * val gap = scaledDp(compact = 12.dp, expanded = 24.dp, large = 32.dp)
 * ```
 */
@Composable
public fun scaledDp(
    compact: Dp,
    medium: Dp = compact,
    expanded: Dp = medium,
    large: Dp = expanded,
    xl: Dp = large,
): Dp = when (currentBreakpoint()) {
    Bp.Compact -> compact
    Bp.Medium -> medium
    Bp.Expanded -> expanded
    Bp.Large -> large
    Bp.XL -> xl
}

/** Picks an sp value for the current width class. */
@Composable
public fun scaledSp(
    compact: TextUnit,
    medium: TextUnit = compact,
    expanded: TextUnit = medium,
    large: TextUnit = expanded,
    xl: TextUnit = large,
): TextUnit = when (currentBreakpoint()) {
    Bp.Compact -> compact
    Bp.Medium -> medium
    Bp.Expanded -> expanded
    Bp.Large -> large
    Bp.XL -> xl
}

/** Convenience aliases used by documentation samples. */
public object MullionResources {
    public fun metrics(bp: Bp): MullionMetrics = MullionMetrics.forBp(bp)

    public fun typeScale(bp: Bp): TypeScale = TypeScale.forBp(bp)

    public fun sectionSpacing(bp: Bp): Dp = metrics(bp).sectionSpacingDp.dp

    public fun displayType(bp: Bp): TextUnit = metrics(bp).displayTypeSp.sp
}
