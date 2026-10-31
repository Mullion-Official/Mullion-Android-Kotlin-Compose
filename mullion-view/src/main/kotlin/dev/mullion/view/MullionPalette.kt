package dev.mullion.view

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

/** Resolved View colors for the current configuration. */
public data class MullionViewColors(
    @ColorInt public val background: Int,
    @ColorInt public val backgroundLift: Int,
    @ColorInt public val backgroundDeep: Int,
    @ColorInt public val volt: Int,
    @ColorInt public val voltHigh: Int,
    @ColorInt public val signal: Int,
    @ColorInt public val mint: Int,
    @ColorInt public val amber: Int,
    @ColorInt public val ink: Int,
    @ColorInt public val muted: Int,
    @ColorInt public val faint: Int,
    @ColorInt public val onVolt: Int,
    @ColorInt public val edge: Int,
    @ColorInt public val edgeHigh: Int,
    @ColorInt public val tonal: Int,
    @ColorInt public val glassTint: Int,
    public val glassScale: Float,
)

/**
 * View-tier palette.
 *
 * Compose reads `MullionColors`; XML/View screens resolve the same semantic
 * tokens from `values` / `values-night` through [of].
 *
 * The constants remain as the legacy dark fallback for existing callers that
 * read them directly. New code should use [of] and react to
 * `onConfigurationChanged`.
 */
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

    /** Resolves the light or night palette for this context. */
    @JvmStatic
    public fun of(context: Context): MullionViewColors {
        val night = (context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return MullionViewColors(
            background = color(context, R.color.mullion_background),
            backgroundLift = color(context, R.color.mullion_background_lift),
            backgroundDeep = color(context, R.color.mullion_background_deep),
            volt = color(context, R.color.mullion_volt),
            voltHigh = color(context, R.color.mullion_volt_high),
            signal = color(context, R.color.mullion_signal),
            mint = color(context, R.color.mullion_mint),
            amber = color(context, R.color.mullion_amber),
            ink = color(context, R.color.mullion_ink),
            muted = color(context, R.color.mullion_muted),
            faint = color(context, R.color.mullion_faint),
            onVolt = color(context, R.color.mullion_on_volt),
            edge = color(context, R.color.mullion_edge),
            edgeHigh = color(context, R.color.mullion_edge_high),
            tonal = color(context, R.color.mullion_tonal),
            glassTint = color(context, R.color.mullion_glass_tint),
            glassScale = if (night) 1f else 0.55f,
        )
    }

    @ColorInt
    private fun color(context: Context, @ColorRes id: Int): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(id)
        } else {
            @Suppress("DEPRECATION")
            context.resources.getColor(id)
        }
}
