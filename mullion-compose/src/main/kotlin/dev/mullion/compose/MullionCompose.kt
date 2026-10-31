package dev.mullion.compose

import dev.mullion.core.Mullion

/** Compose entry point retained for binary-safe builds during early bootstrap. */
public object MullionCompose {
    /** Framework version exposed to Compose consumers. */
    public const val VERSION: String = Mullion.VERSION
}
