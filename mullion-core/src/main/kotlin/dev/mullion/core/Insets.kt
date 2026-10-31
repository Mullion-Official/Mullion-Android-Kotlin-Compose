package dev.mullion.core

/**
 * Window insets expressed in dp so they can be reasoned about without a
 * `Density`. Compose converts px to dp at the edge of the framework.
 */
public data class MullionInsets(
    public val leftDp: Int = 0,
    public val topDp: Int = 0,
    public val rightDp: Int = 0,
    public val bottomDp: Int = 0,
    public val imeDp: Int = 0,
) {
    /** Sum of the horizontal insets. */
    public val horizontalDp: Int
        get() = leftDp + rightDp

    /** Sum of the vertical insets, including the IME. */
    public val verticalDp: Int
        get() = topDp + bottomDp + imeDp

    /** The effective bottom padding once the keyboard has claimed space. */
    public val effectiveBottomDp: Int
        get() = maxOf(bottomDp, imeDp)

    /** `true` when every inset is zero. */
    public val isEmpty: Boolean
        get() = leftDp == 0 && topDp == 0 && rightDp == 0 && bottomDp == 0 && imeDp == 0

    /** Returns insets with the IME replaced. */
    public fun withIme(imeDp: Int): MullionInsets = copy(imeDp = imeDp.coerceAtLeast(0))

    public companion object {
        public val Zero: MullionInsets = MullionInsets()
    }
}

/**
 * The raw measurements a layout needs to adapt. Immutable by design: a window
 * change produces a new value and Compose recomposes exactly once.
 */
public data class MullionWindowState(
    public val widthDp: Int,
    public val heightDp: Int,
    public val insets: MullionInsets = MullionInsets.Zero,
    public val hinge: HingeInfo = HingeInfo.None,
    public val fontScale: Float = 1f,
) {
    init {
        require(widthDp >= 0) { "widthDp must be >= 0, was $widthDp" }
        require(heightDp >= 0) { "heightDp must be >= 0, was $heightDp" }
        require(fontScale > 0f) { "fontScale must be positive, was $fontScale" }
    }

    /** Width class of the full window. */
    public val bp: Bp
        get() = Bp.of(widthDp)

    /** Navigation morphology for this window. */
    public val navigationMode: NavigationMode
        get() = NavigationMode.forBp(bp)

    /** Width left after horizontal system insets. */
    public val contentWidthDp: Int
        get() = (widthDp - insets.horizontalDp).coerceAtLeast(0)

    /** Height left after the top inset and the effective bottom inset. */
    public val contentHeightDp: Int
        get() = (heightDp - insets.topDp - insets.effectiveBottomDp).coerceAtLeast(0)

    /** Resolves a plan using [strategy] and this window's width class. */
    public fun plan(strategy: PaneStrategy, slots: List<PaneSlot>): PanePlan =
        strategy.plan(bp, slots)

    /** Pane count under [strategy]. */
    public fun paneCount(strategy: PaneStrategy = PaneStrategy()): Int =
        strategy.countFor(bp)

    public companion object {
        /** A state for a window with no insets and no hinge. */
        public fun of(widthDp: Int, heightDp: Int): MullionWindowState =
            MullionWindowState(widthDp = widthDp, heightDp = heightDp)
    }
}
