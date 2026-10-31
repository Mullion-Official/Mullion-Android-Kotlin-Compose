package dev.mullion.core

/**
 * The five width classes MULLION is built around.
 *
 * The thresholds are ordinary compile-time constants. [of] is a pure `when`
 * expression, so the Kotlin compiler can inline and constant-fold it. There is
 * no reflection and no resource lookup on the hot path.
 *
 * ```
 * 0 ─────── 480 ─────── 600 ─────── 840 ─────── 1200 ──────▶ dp
 *   Compact   Medium     Expanded     Large        XL
 *   1 pane    1 pane     2 panes      3 panes      3 panes
 *   bottom    bottom     rail         drawer       drawer
 * ```
 *
 * [minWidthDp] is the lower bound of the class. [baselineWidthDp] is the design
 * width used by the cabinet rail; [Compact] starts at 0 but is tuned from 320dp.
 */
public enum class Bp(
    public val minWidthDp: Int,
    public val baselineWidthDp: Int,
    public val label: String,
) {
    Compact(minWidthDp = 0, baselineWidthDp = 320, label = "Compact"),
    Medium(minWidthDp = 480, baselineWidthDp = 480, label = "Medium"),
    Expanded(minWidthDp = 600, baselineWidthDp = 600, label = "Expanded"),
    Large(minWidthDp = 840, baselineWidthDp = 840, label = "Large"),
    XL(minWidthDp = 1200, baselineWidthDp = 1200, label = "XL"),
    ;

    /** `true` for phones held in portrait. */
    public val isCompact: Boolean
        get() = this == Compact || this == Medium

    /** `true` when more than one primary pane can be shown. */
    public val isExpanded: Boolean
        get() = this == Expanded || this == Large || this == XL

    /** The next class, or `null` for [XL]. */
    public val next: Bp?
        get() = entries.getOrNull(ordinal + 1)

    /** The previous class, or `null` for [Compact]. */
    public val previous: Bp?
        get() = entries.getOrNull(ordinal - 1)

    public companion object {
        /** Narrowest window the visual system is tuned for. */
        public const val MIN_DESIGN_WIDTH_DP: Int = 320

        /** Widest window the visual system is tuned for. */
        public const val MAX_DESIGN_WIDTH_DP: Int = 1200

        /** All lower-bound thresholds, ascending. */
        public val THRESHOLDS_DP: List<Int> = listOf(480, 600, 840, 1200)

        /** Rail ticks used by the sample and documentation. */
        public val DESIGN_TICKS: List<Int> = listOf(320, 480, 600, 840, 1200)

        /**
         * Classifies a window width.
         *
         * Negative widths are treated as zero so a surface that has not been
         * measured yet still resolves to a legal class.
         */
        public fun of(widthDp: Int): Bp = when {
            widthDp >= XL.minWidthDp -> XL
            widthDp >= Large.minWidthDp -> Large
            widthDp >= Expanded.minWidthDp -> Expanded
            widthDp >= Medium.minWidthDp -> Medium
            else -> Compact
        }

        /** Float convenience overload; rounds toward zero class boundaries. */
        public fun of(widthDp: Float): Bp = of(widthDp.toInt())

        /** The class immediately above [widthDp], or [XL]. */
        public fun ceiling(widthDp: Int): Bp = nextUp(of(widthDp)) ?: XL

        /** The class immediately below [widthDp], or [Compact]. */
        public fun floor(widthDp: Int): Bp = of(widthDp)

        /** Returns the next wider class, or `null` at [XL]. */
        public fun nextUp(bp: Bp): Bp? = bp.next
    }
}
