package dev.mullion.core

/**
 * Semantic preview widths for design tools, screenshot tests and sample
 * trays.
 *
 * Product code should not pass raw dp numbers around to trigger adaptation.
 * It should either use the real window, or choose one of these named contexts.
 * The numbers live here, once, next to the breakpoint ruler.
 */
public enum class MullionViewport(
    public val label: String,
    public val widthDp: Int,
    public val heightDp: Int,
) {
    /** A narrow phone. Compact / BottomBar / one pane. */
    Phone(label = "Phone", widthDp = 375, heightDp = 812),

    /** An unfolded phone or small tablet. Expanded / Rail / two panes. */
    Fold(label = "Fold", widthDp = 760, heightDp = 1024),

    /** A large tablet or desktop window. XL / Drawer / three panes. */
    Desktop(label = "Desktop", widthDp = 1180, heightDp = 900),
    ;

    public val bp: Bp
        get() = Bp.of(widthDp)

    public val metrics: MullionMetrics
        get() = MullionMetrics.forBp(bp)
}
