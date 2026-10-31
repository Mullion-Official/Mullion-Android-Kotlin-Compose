package dev.mullion.core

/** Which resource qualifier bucket a width belongs to. */
public enum class ResourceBucket(
    public val qualifier: String,
    public val label: String,
) {
    /** Base `values/` bucket: phones. */
    Default("values", "values-default"),

    /** `values-w600dp/`: small tablets and unfolded phones. */
    W600("values-w600dp", "values-w600dp"),

    /** `values-w840dp/`: large tablets and desktop windows. */
    W840("values-w840dp", "values-w840dp"),
    ;

    public companion object {
        /** Buckets a width without touching the resource system. */
        public fun forWidth(widthDp: Int): ResourceBucket = when {
            widthDp >= 840 -> W840
            widthDp >= 600 -> W600
            else -> Default
        }

        /** Buckets a width class. */
        public fun forBp(bp: Bp): ResourceBucket = when (bp) {
            Bp.Compact, Bp.Medium -> Default
            Bp.Expanded -> W600
            Bp.Large, Bp.XL -> W840
        }
    }
}

/**
 * The shared ruler every MULLION control snaps to.
 *
 * These values match the `values/`, `values-w600dp/` and `values-w840dp/`
 * buckets shipped by `mullion-view`, so Compose and XML resolve to the same
 * numbers.
 */
public data class MullionMetrics(
    /** Vertical space between major sections. */
    public val sectionSpacingDp: Int,
    /** Display type size in sp. */
    public val displayTypeSp: Int,
    /** Outer pane margin. */
    public val paneMarginDp: Int,
    /** Space between panes. */
    public val paneGutterDp: Int,
) {
    public companion object {
        public val Compact: MullionMetrics = MullionMetrics(
            sectionSpacingDp = 48,
            displayTypeSp = 32,
            paneMarginDp = 16,
            paneGutterDp = 12,
        )

        public val Medium: MullionMetrics = Compact

        public val Expanded: MullionMetrics = MullionMetrics(
            sectionSpacingDp = 80,
            displayTypeSp = 44,
            paneMarginDp = 24,
            paneGutterDp = 24,
        )

        public val Large: MullionMetrics = MullionMetrics(
            sectionSpacingDp = 120,
            displayTypeSp = 56,
            paneMarginDp = 32,
            paneGutterDp = 24,
        )

        public val XL: MullionMetrics = Large

        /** Metrics for a width class. */
        public fun forBp(bp: Bp): MullionMetrics = when (bp) {
            Bp.Compact -> Compact
            Bp.Medium -> Medium
            Bp.Expanded -> Expanded
            Bp.Large -> Large
            Bp.XL -> XL
        }

        /** Metrics for a raw window width. */
        public fun forWidth(widthDp: Int): MullionMetrics = forBp(Bp.of(widthDp))

        /** Metrics for a resource bucket. */
        public fun forBucket(bucket: ResourceBucket): MullionMetrics = when (bucket) {
            ResourceBucket.Default -> Compact
            ResourceBucket.W600 -> Expanded
            ResourceBucket.W840 -> Large
        }
    }
}

/**
 * Type steps used by `SizeClassText`. Sizes are a step function of the width
 * class, never a linear function of the window width: body copy stays legible,
 * and the measure stays inside the readable range.
 */
public data class TypeScale(
    public val fontSizeSp: Float,
    public val lineHeightSp: Float,
    public val maxMeasureCh: Int,
) {
    public companion object {
        public val Compact: TypeScale = TypeScale(15f, 27f, 62)
        public val Medium: TypeScale = Compact
        public val Expanded: TypeScale = TypeScale(17f, 30.6f, 68)
        public val Large: TypeScale = TypeScale(19f, 34.2f, 72)
        public val XL: TypeScale = Large

        public fun forBp(bp: Bp): TypeScale = when (bp) {
            Bp.Compact -> Compact
            Bp.Medium -> Medium
            Bp.Expanded -> Expanded
            Bp.Large -> Large
            Bp.XL -> XL
        }

        public fun forWidth(widthDp: Int): TypeScale = forBp(Bp.of(widthDp))
    }
}
