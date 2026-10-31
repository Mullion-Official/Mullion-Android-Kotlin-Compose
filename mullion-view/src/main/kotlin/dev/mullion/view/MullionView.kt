package dev.mullion.view

import dev.mullion.core.Mullion

/** View entry point retained for binary-safe builds during early bootstrap. */
public object MullionView {
    /** Framework version exposed to View consumers. */
    public const val VERSION: String = Mullion.VERSION
}
