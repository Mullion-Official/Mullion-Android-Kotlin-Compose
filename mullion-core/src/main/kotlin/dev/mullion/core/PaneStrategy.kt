package dev.mullion.core

/**
 * The slots a multi-pane layout can show. Slots are intentionally generic so
 * the same strategy powers list/detail/preview, search/filters, or any custom
 * three-surface composition.
 */
public enum class PaneSlot {
    List,
    Detail,
    Preview,
    Filters,
}

/**
 * A resolved pane composition.
 *
 * @property slots the slots that are currently visible, in visual order.
 * @property gutterDp the resolved space between panes. It comes from the
 *   active width bucket unless the caller explicitly overrides it.
 */
public data class PanePlan(
    public val slots: List<PaneSlot>,
    public val gutterDp: Int,
) {
    /** Number of visible panes. */
    public val count: Int
        get() = slots.size

    /** `true` when only one pane is visible. */
    public val isSinglePane: Boolean
        get() = count == 1

    /** `true` when [slot] is part of this plan. */
    public fun shows(slot: PaneSlot): Boolean = slot in slots
}

/**
 * Decides how many panes a width can carry.
 *
 * The public [counts] map is the whole contract, which makes the policy easy to
 * test, document and override. Defaults reproduce the MULLION cabinet:
 *
 * | Bp | panes |
 * | --- | --- |
 * | Compact / Medium | 1 |
 * | Expanded | 2 |
 * | Large / XL | 3 |
 *
 * The count is clamped to `1..maxPanes` and to the number of slots supplied to
 * [plan]. A custom layout can therefore cap panes at two without touching any
 * screen code.
 */
public data class PaneStrategy(
    public val counts: Map<Bp, Int> = DEFAULT_COUNTS,
    public val maxPanes: Int = 3,
    public val gutterDp: Int? = null,
) {
    init {
        require(maxPanes >= 1) { "maxPanes must be at least 1, was $maxPanes" }
        require(gutterDp == null || gutterDp >= 0) { "gutterDp must be >= 0, was $gutterDp" }
    }

    /** Number of panes allowed by the class, before slot clamping. */
    public fun countFor(bp: Bp): Int =
        (counts[bp] ?: 1).coerceIn(1, maxPanes)

    /** Number of panes allowed at [widthDp]. */
    public fun countForWidth(widthDp: Int): Int = countFor(Bp.of(widthDp))

    /**
     * Resolves the visible slots for [bp], preserving the caller's order.
     *
     * When fewer slots are supplied than the class allows, all supplied slots
     * are shown. The first slot is always the "primary" pane.
     */
    public fun plan(bp: Bp, slots: List<PaneSlot>): PanePlan {
        require(slots.isNotEmpty()) { "at least one pane slot is required" }
        val visible = minOf(countFor(bp), slots.size)
        val resolvedGutter = gutterDp ?: MullionMetrics.forBp(bp).paneGutterDp
        return PanePlan(slots = slots.take(visible), gutterDp = resolvedGutter)
    }

    /** Resolves the visible slots for a raw window width. */
    public fun planForWidth(widthDp: Int, slots: List<PaneSlot>): PanePlan =
        plan(Bp.of(widthDp), slots)

    public companion object {
        /** Cabinet defaults: 1 / 1 / 2 / 3 / 3. */
        public val DEFAULT_COUNTS: Map<Bp, Int> = mapOf(
            Bp.Compact to 1,
            Bp.Medium to 1,
            Bp.Expanded to 2,
            Bp.Large to 3,
            Bp.XL to 3,
        )

        /** A reading-oriented strategy that never exceeds two panes. */
        public val TWO_PANE: PaneStrategy = PaneStrategy(maxPanes = 2)

        /** A single-pane strategy useful for focus modes. */
        public val SINGLE_PANE: PaneStrategy = PaneStrategy(
            counts = Bp.entries.associateWith { 1 },
            maxPanes = 1,
        )
    }
}
