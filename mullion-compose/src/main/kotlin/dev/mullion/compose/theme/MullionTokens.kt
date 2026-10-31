package dev.mullion.compose.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Semantic color tokens for one theme.
 *
 * Components never read a raw hex value; they read this palette through
 * [LocalMullionPalette] (via [MullionColors]). The same component therefore
 * works in light, dark and split-screen windows.
 */
@Immutable
public data class MullionPalette(
    public val background: Color,
    public val backgroundLift: Color,
    public val backgroundDeep: Color,
    public val volt: Color,
    public val voltHigh: Color,
    public val signal: Color,
    public val mint: Color,
    public val amber: Color,
    public val ink: Color,
    public val muted: Color,
    public val faint: Color,
    public val onVolt: Color,
    public val edge: Color,
    public val edgeHigh: Color,
    public val edgeTop: Color,
    public val scrim: Color,
    public val tonal: Color,
    public val tonalPressed: Color,
    public val glassTint: Color,
    public val glassAlphaScale: Float,
    public val avatarPalette: List<List<Color>>,
)

public val LightMullionPalette: MullionPalette = MullionPalette(
    background = Color(0xFFF5F7FA),
    backgroundLift = Color(0xFFFFFFFF),
    backgroundDeep = Color(0xFFE9EEF4),
    volt = Color(0xFF2563EB),
    voltHigh = Color(0xFF60A5FA),
    signal = Color(0xFFD9534F),
    mint = Color(0xFF0E9F6E),
    amber = Color(0xFFB45309),
    ink = Color(0xFF111827),
    muted = Color(0xFF4B5563),
    faint = Color(0xFF8A94A3),
    onVolt = Color(0xFFFFFFFF),
    edge = Color(0x140F172A),
    edgeHigh = Color(0x29111827),
    edgeTop = Color(0x40FFFFFF),
    scrim = Color(0x660F172A),
    tonal = Color(0x0A0F172A),
    tonalPressed = Color(0x140F172A),
    glassTint = Color(0xFF0F172A),
    glassAlphaScale = 0.55f,
    avatarPalette = listOf(
        listOf(Color(0xFF2563EB), Color(0xFF60A5FA)),
        listOf(Color(0xFFB45309), Color(0xFFF0B35A)),
        listOf(Color(0xFF0E9F6E), Color(0xFF6ED7B0)),
        listOf(Color(0xFF6D5BD0), Color(0xFFA99AF5)),
    ),
)

public val DarkMullionPalette: MullionPalette = MullionPalette(
    background = Color(0xFF0F141B),
    backgroundLift = Color(0xFF171D26),
    backgroundDeep = Color(0xFF202936),
    volt = Color(0xFF6EA8FE),
    voltHigh = Color(0xFFA7C4FF),
    signal = Color(0xFFE07A5F),
    mint = Color(0xFF55C7A3),
    amber = Color(0xFFD6A84B),
    ink = Color(0xFFE9EEF5),
    muted = Color(0xFFA6B0BE),
    faint = Color(0xFF75808F),
    onVolt = Color(0xFF0B1220),
    edge = Color(0x24FFFFFF),
    edgeHigh = Color(0x4DFFFFFF),
    edgeTop = Color(0x42FFFFFF),
    scrim = Color(0x99070C12),
    tonal = Color(0x14FFFFFF),
    tonalPressed = Color(0x24FFFFFF),
    glassTint = Color(0xFFFFFFFF),
    glassAlphaScale = 1f,
    avatarPalette = listOf(
        listOf(Color(0xFF6EA8FE), Color(0xFFA7C4FF)),
        listOf(Color(0xFFD6A84B), Color(0xFFE07A5F)),
        listOf(Color(0xFF55C7A3), Color(0xFF9BDFC8)),
        listOf(Color(0xFFA99AF5), Color(0xFF6D5BD0)),
    ),
)

public val LocalMullionPalette: androidx.compose.runtime.ProvidableCompositionLocal<MullionPalette> =
    staticCompositionLocalOf { LightMullionPalette }

/**
 * Backwards-compatible token facade.
 *
 * Existing component code keeps writing `MullionColors.Ink`; because these are
 * composable getters, each read resolves the palette of the current theme.
 */
public object MullionColors {
    public val Background: Color @Composable get() = LocalMullionPalette.current.background
    public val BackgroundLift: Color @Composable get() = LocalMullionPalette.current.backgroundLift
    public val BackgroundDeep: Color @Composable get() = LocalMullionPalette.current.backgroundDeep
    public val Volt: Color @Composable get() = LocalMullionPalette.current.volt
    public val VoltHigh: Color @Composable get() = LocalMullionPalette.current.voltHigh
    public val Signal: Color @Composable get() = LocalMullionPalette.current.signal
    public val Mint: Color @Composable get() = LocalMullionPalette.current.mint
    public val Amber: Color @Composable get() = LocalMullionPalette.current.amber
    public val Ink: Color @Composable get() = LocalMullionPalette.current.ink
    public val Muted: Color @Composable get() = LocalMullionPalette.current.muted
    public val Faint: Color @Composable get() = LocalMullionPalette.current.faint
    public val OnVolt: Color @Composable get() = LocalMullionPalette.current.onVolt
    public val Edge: Color @Composable get() = LocalMullionPalette.current.edge
    public val EdgeHigh: Color @Composable get() = LocalMullionPalette.current.edgeHigh
    public val EdgeTop: Color @Composable get() = LocalMullionPalette.current.edgeTop
    public val Scrim: Color @Composable get() = LocalMullionPalette.current.scrim
    public val GlassTop: Color @Composable get() = LocalMullionPalette.current.glassTint.copy(alpha = 0.17f)
    public val GlassMid: Color @Composable get() = LocalMullionPalette.current.glassTint.copy(alpha = 0.07f)
    public val GlassBottom: Color @Composable get() = LocalMullionPalette.current.glassTint.copy(alpha = 0.10f)
    public val Tonal: Color @Composable get() = LocalMullionPalette.current.tonal
    public val TonalPressed: Color @Composable get() = LocalMullionPalette.current.tonalPressed
    public val AvatarPalette: List<List<Color>> @Composable get() = LocalMullionPalette.current.avatarPalette

    /** Constant, not theme dependent. */
    public val Transparent: Color = Color(0x00000000)
}

/** Named type families used across the components. */
public object MullionFonts {
    public val Sans: FontFamily = FontFamily.SansSerif
    public val Mono: FontFamily = FontFamily.Monospace
}

/**
 * Reusable text styles.
 *
 * [UI], [UISmall] and [UILabel] are the product-facing defaults. [Mono] remains
 * available for IDs, logs and device readouts, but it is not the voice of the
 * whole application.
 */
public object MullionTextStyles {
    public val Label: TextStyle = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        letterSpacing = 0.4.sp,
        lineHeight = 14.sp,
    )

    public val LabelSmall: TextStyle = Label.copy(fontSize = 9.sp, letterSpacing = 0.3.sp)

    public val UI: TextStyle = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
    )

    public val UISmall: TextStyle = UI.copy(fontSize = 11.sp, lineHeight = 16.sp)

    public val UILabel: TextStyle = UI.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
    )

    public val Readout: TextStyle = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    )

    public val Mono: TextStyle = TextStyle(
        fontFamily = MullionFonts.Mono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    )

    public val MonoSmall: TextStyle = Mono.copy(fontSize = 10.5.sp, lineHeight = 16.sp)

    public val Body: TextStyle = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontSize = 15.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.1.sp,
    )

    public val BodyLarge: TextStyle = Body.copy(fontSize = 18.sp, lineHeight = 28.sp)
}

/** Motion is part of the structure. */
public object MullionMotion {
    public const val MicroMillis: Int = 120
    public const val StateMillis: Int = 180
    public const val PaneMillis: Int = 240
    public const val SheetMillis: Int = 340
    public const val DockMillis: Int = 380
    public const val FoldMillis: Int = 420
    public const val SnackbarMillis: Long = 3_200

    public val Settle: Easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
    public val Standard: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    public val NavMorph: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
}

/** Fixed geometry shared by controls. */
@Immutable
public data class MullionDimens(
    public val touchTarget: Dp = 48.dp,
    public val control: Dp = 44.dp,
    public val controlSmall: Dp = 36.dp,
    public val controlLarge: Dp = 52.dp,
    public val field: Dp = 42.dp,
    public val radiusCard: Dp = 12.dp,
    public val radiusPanel: Dp = 18.dp,
    public val radiusWindow: Dp = 28.dp,
    public val gutter: Dp = 24.dp,
    public val railWidth: Dp = 80.dp,
    public val drawerWidth: Dp = 216.dp,
    public val dividerWidth: Dp = 11.dp,
) {
    public companion object {
        public val Default: MullionDimens = MullionDimens()
    }
}
