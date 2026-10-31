package dev.mullion.core

/** Coordinates and build metadata for the MULLION visual framework. */
public object Mullion {
    /** Semantic version of the framework. */
    public const val VERSION: String = "1.0.0"

    /** Maven group shared by every published artifact. */
    public const val GROUP: String = "dev.mullion"

    /** The documented minimum Android API level. */
    public const val MIN_SDK: Int = 21

    /** Design baseline: the narrowest window MULLION is tuned for, in dp. */
    public const val MIN_DESIGN_WIDTH_DP: Int = 320

    /** Design baseline: the widest window MULLION is tuned for, in dp. */
    public const val MAX_DESIGN_WIDTH_DP: Int = 1200
}
