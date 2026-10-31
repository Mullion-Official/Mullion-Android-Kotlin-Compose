package dev.mullion.core

/** Physical orientation of a fold or hinge. */
public enum class HingeOrientation {
    /** The seam runs top-to-bottom; panes sit side by side. */
    Vertical,

    /** The seam runs left-to-right; panes stack. */
    Horizontal,
}

/** The three postures MULLION gives first-class layout semantics. */
public enum class HingePosture {
    /** Fully unfolded, no physical separator. */
    Flat,

    /** Partially open; a separator occupies real space. */
    HalfOpened,

    /** Tent mode; the display is propped up and the seam is still real. */
    Tent,
}

/** A dp rectangle, origin at the top-left of the window. */
public data class DpRect(
    public val left: Float,
    public val top: Float,
    public val right: Float,
    public val bottom: Float,
) {
    public val width: Float
        get() = (right - left).coerceAtLeast(0f)

    public val height: Float
        get() = (bottom - top).coerceAtLeast(0f)

    public val centerX: Float
        get() = (left + right) / 2f

    public val centerY: Float
        get() = (top + bottom) / 2f
}

/**
 * A physical hinge, in dp. [separatorWidthDp] and [separatorHeightDp] describe
 * the dead area that content must not cross.
 */
public data class HingeInfo(
    public val posture: HingePosture = HingePosture.Flat,
    public val orientation: HingeOrientation? = null,
    public val separatorWidthDp: Float = 0f,
    public val separatorHeightDp: Float = 0f,
    public val bounds: DpRect? = null,
) {
    init {
        require(separatorWidthDp >= 0f) { "separatorWidthDp must be >= 0" }
        require(separatorHeightDp >= 0f) { "separatorHeightDp must be >= 0" }
    }

    /** `true` when the hinge occupies physical space. */
    public val isSeparating: Boolean
        get() = orientation != null && posture != HingePosture.Flat

    /** `true` when the seam is vertical and separating. */
    public val isVerticalSeparator: Boolean
        get() = isSeparating && orientation == HingeOrientation.Vertical

    /** `true` when the seam is horizontal and separating. */
    public val isHorizontalSeparator: Boolean
        get() = isSeparating && orientation == HingeOrientation.Horizontal

    /** Returns this hinge with a new posture. */
    public fun withPosture(posture: HingePosture): HingeInfo = copy(posture = posture)

    public companion object {
        /** No hinge, or a fully flat foldable. */
        public val None: HingeInfo = HingeInfo()

        /** A vertical book-style fold. */
        public fun vertical(
            separatorWidthDp: Float = 0f,
            posture: HingePosture = HingePosture.HalfOpened,
        ): HingeInfo = HingeInfo(
            posture = posture,
            orientation = HingeOrientation.Vertical,
            separatorWidthDp = separatorWidthDp,
        )

        /** A horizontal clamshell fold. */
        public fun horizontal(
            separatorHeightDp: Float = 0f,
            posture: HingePosture = HingePosture.HalfOpened,
        ): HingeInfo = HingeInfo(
            posture = posture,
            orientation = HingeOrientation.Horizontal,
            separatorHeightDp = separatorHeightDp,
        )
    }
}

/**
 * The result of splitting a width around a vertical hinge.
 *
 * [paneWidthDp] is the width available to each of the two leaves. [gutterDp] is
 * the breathing room kept on each side of the physical seam. Panes are never
 * asked to draw over [hingeStartDp]..[hingeEndDp].
 */
public data class FoldSplit(
    public val totalWidthDp: Float,
    public val paneWidthDp: Float,
    public val hingeWidthDp: Float,
    public val gutterDp: Float,
) {
    /** Left edge of the first leaf. */
    public val startPaneStartDp: Float
        get() = 0f

    /** Right edge of the first leaf. */
    public val startPaneEndDp: Float
        get() = paneWidthDp

    /** Left edge of the separator (including its safe gutter when separating). */
    public val hingeStartDp: Float
        get() = paneWidthDp + if (hingeWidthDp > 0f) gutterDp else 0f

    /** Right edge of the separator. */
    public val hingeEndDp: Float
        get() = hingeStartDp + hingeWidthDp

    /** Left edge of the second leaf. */
    public val endPaneStartDp: Float
        get() = hingeEndDp + if (hingeWidthDp > 0f) gutterDp else 0f

    /** Right edge of the second leaf. */
    public val endPaneEndDp: Float
        get() = totalWidthDp

    /** `true` when both leaves are at least [minPaneDp] wide. */
    public fun fits(minPaneDp: Float): Boolean = paneWidthDp >= minPaneDp
}

/** Hinge-aware geometry helpers shared by every layout surface. */
public object Foldables {
    /** Default breathing room between content and a physical seam. */
    public const val DEFAULT_SAFE_GAP_DP: Float = 24f

    /**
     * Splits [availableWidthDp] into two equal leaves around a vertical hinge.
     *
     * A flat or absent hinge produces two equal panes with no dead seam, which
     * keeps content continuous when a device unfolds. A horizontal hinge also
     * yields equal panes; it is the caller's job to switch to a stacked layout.
     */
    public fun splitVertically(
        availableWidthDp: Float,
        hinge: HingeInfo?,
        safeGapDp: Float = DEFAULT_SAFE_GAP_DP,
    ): FoldSplit {
        val width = availableWidthDp.coerceAtLeast(0f)
        val gap = safeGapDp.coerceAtLeast(0f)
        if (hinge == null || !hinge.isVerticalSeparator) {
            return FoldSplit(
                totalWidthDp = width,
                paneWidthDp = width / 2f,
                hingeWidthDp = 0f,
                gutterDp = 0f,
            )
        }
        val seam = hinge.separatorWidthDp.coerceAtLeast(0f)
        val usable = (width - seam - gap * 2f).coerceAtLeast(0f)
        return FoldSplit(
            totalWidthDp = width,
            paneWidthDp = usable / 2f,
            hingeWidthDp = seam,
            gutterDp = gap,
        )
    }

    /**
     * The width a side sheet should occupy so it never crosses a vertical seam.
     * Returns `null` when a side dock would leave less than [minPaneDp], which
     * tells the caller to fall back to a bottom sheet.
     */
    public fun sideSheetWidth(
        availableWidthDp: Float,
        hinge: HingeInfo?,
        fraction: Float = 0.44f,
        minPaneDp: Float = 240f,
        safeGapDp: Float = DEFAULT_SAFE_GAP_DP,
    ): Float? {
        if (hinge == null || !hinge.isVerticalSeparator) return null
        val split = splitVertically(availableWidthDp, hinge, safeGapDp)
        val width = split.paneWidthDp * fraction.coerceIn(0.1f, 0.9f)
        return width.takeIf { it >= minPaneDp }
    }

    /**
     * `true` when a vertical seam leaves enough room for [minPaneDp] on both
     * sides. Used by DockedSheet, FoldablePane and MultiPane to decide between a
     * split layout and a single-pane fallback.
     */
    public fun canSplit(
        availableWidthDp: Float,
        hinge: HingeInfo?,
        minPaneDp: Float,
        safeGapDp: Float = DEFAULT_SAFE_GAP_DP,
    ): Boolean = splitVertically(availableWidthDp, hinge, safeGapDp).fits(minPaneDp)
}
