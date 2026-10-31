package dev.mullion.core

/**
 * How primary navigation is presented.
 *
 * Navigation is a property of the window, not of a screen. Screens declare the
 * same destinations and MULLION decides whether they become a bottom bar, a rail
 * or a drawer.
 */
public enum class NavigationMode {
    /** Compact phones: a bottom bar. */
    BottomBar,

    /** Tablets and book-style foldables: a left rail. */
    Rail,

    /** Wide windows: a persistent drawer beside the content. */
    Drawer,
    ;

    public companion object {
        /**
         * The single source of truth for navigation morphology.
         *
         *  - `0..599dp` → [BottomBar]
         *  - `600..839dp` → [Rail]
         *  - `840dp+` → [Drawer]
         */
        public fun forBp(bp: Bp): NavigationMode = when (bp) {
            Bp.Compact, Bp.Medium -> BottomBar
            Bp.Expanded -> Rail
            Bp.Large, Bp.XL -> Drawer
        }

        /** Convenience overload that classifies [widthDp] first. */
        public fun forWidth(widthDp: Int): NavigationMode = forBp(Bp.of(widthDp))
    }
}
