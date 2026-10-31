package dev.mullion.view

import androidx.annotation.ColorInt

/** The MULLION palette as ARGB ints for View drawing code. */
public object MullionPalette {
    @ColorInt public const val Background: Int = 0xFF04070C.toInt()
    @ColorInt public const val BackgroundLift: Int = 0xFF081320.toInt()
    @ColorInt public const val Volt: Int = 0xFF35E8FF.toInt()
    @ColorInt public const val VoltHigh: Int = 0xFF84F2FF.toInt()
    @ColorInt public const val Signal: Int = 0xFFFF8354.toInt()
    @ColorInt public const val Mint: Int = 0xFF3EE8A8.toInt()
    @ColorInt public const val Amber: Int = 0xFFFFD166.toInt()
    @ColorInt public const val Ink: Int = 0xFFEDF4FA.toInt()
    @ColorInt public const val Muted: Int = 0xFF94A9BB.toInt()
    @ColorInt public const val Faint: Int = 0xFF5F7688.toInt()
    @ColorInt public const val OnVolt: Int = 0xFF04222B.toInt()
}
