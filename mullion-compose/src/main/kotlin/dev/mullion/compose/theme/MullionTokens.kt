package dev.mullion.compose.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle

/**
 * The MULLION palette.
 *
 * Volt is reserved for the active pane, the focused field and the primary
 * action. Signal (orange) is reserved for hinges and warnings. Everything else
 * is glass and ink.
 */
public object MullionColors {
    /** Window background, nearly black with a blue bias. */
    public val Background: Color = Color(0xFF04070C)

    /** The first elevated surface behind glass. */
    public val BackgroundLift: Color = Color(0xFF081320)

    /** The second elevated surface. */
    public val BackgroundDeep: Color = Color(0xFF0B2231)

    /** Primary accent: the current pane, focus ring, primary action. */
    public val Volt: Color = Color(0xFF35E8FF)

    /** Lighter accent used for gradients and hover states. */
    public val VoltHigh: Color = Color(0xFF84F2FF)

    /** Hinge and destructive accent. */
    public val Signal: Color = Color(0xFFFF8354)

    /** Success and validation. */
    public val Mint: Color = Color(0xFF3EE8A8)

    /** Warning and readouts. */
    public val Amber: Color = Color(0xFFFFD166)

    /** Primary text. */
    public val Ink: Color = Color(0xFFEDF4FA)

    /** Secondary text. */
    public val Muted: Color = Color(0xFF94A9BB)

    /** Tertiary text, labels and rulers. */
    public val Faint: Color = Color(0xFF5F7688)

    /** Text drawn on a volt fill. */
    public val OnVolt: Color = Color(0xFF04222B)

    /** Default glass border. */
    public val Edge: Color = Color(0x21FFFFFF)

    /** Highlighted glass border. */
    public val EdgeHigh: Color = Color(0x4DFFFFFF)

    /** A 1dp top edge highlight. */
    public val EdgeTop: Color = Color(0x42FFFFFF)

    /** Scrim behind dialogs and sheets. */
    public val Scrim: Color = Color(0x8004070C)

    /** Glass fill stops for a panel. */
    public val GlassTop: Color = Color(0x17FFFFFF)
    public val GlassMid: Color = Color(0x07FFFFFF)
    public val GlassBottom: Color = Color(0x0FFFFFFF)

    /** Tonal fill used by secondary buttons and fields. */
    public val Tonal: Color = Color(0x21FFFFFF)

    /** Pressed tonal fill. */
    public val TonalPressed: Color = Color(0x33FFFFFF)

    /** Fully transparent; useful in border and gradient slots. */
    public val Transparent: Color = Color(0x00000000)

    /** Avatar gradient seeds. */
    public val AvatarPalette: List<List<Color>> = listOf(
        listOf(VoltHigh, Color(0xFF1FA8C4)),
        listOf(Amber, Signal),
        listOf(VoltHigh, Mint),
        listOf(Color(0xFFC792EA), Color(0xFF7C6BF0)),
    )
}

/** Named type families used across the cabinet. */
public object MullionFonts {
    public val Sans: FontFamily = FontFamily.SansSerif
    public val Mono: FontFamily = FontFamily.Monospace
}

/**
 * Reusable text styles that carry the cabinet's voice: monospace for labels and
 * readouts, humanist sans for prose.
 */
public object MullionTextStyles {
    public val Label: TextStyle = TextStyle(
        fontFamily = MullionFonts.Mono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        letterSpacing = 1.8.sp,
        lineHeight = 14.sp,
    )

    public val LabelSmall: TextStyle = Label.copy(fontSize = 9.sp, letterSpacing = 1.2.sp)

    public val Readout: TextStyle = TextStyle(
        fontFamily = MullionFonts.Mono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 24.sp,
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
        lineHeight = 27.sp,
        letterSpacing = 0.1.sp,
    )

    public val BodyLarge: TextStyle = Body.copy(fontSize = 18.sp, lineHeight = 30.sp)
}

/**
 * Motion is part of the structure. Durations are short enough to read as
 * "re-layout", not "animation".
 */
public object MullionMotion {
    /** State changes inside a control. */
    public const val MicroMillis: Int = 120

    /** Press, hover and selection. */
    public const val StateMillis: Int = 180

    /** Pane and axis transitions. */
    public const val PaneMillis: Int = 240

    /** Dialog and sheet travel. */
    public const val SheetMillis: Int = 340

    /** Docking and re-docking. */
    public const val DockMillis: Int = 380

    /** Fold re-measure. */
    public const val FoldMillis: Int = 420

    /** Snackbar lifetime. */
    public const val SnackbarMillis: Long = 3_200

    /** A cubic-bezier(.22, 1, .36, 1) ease-out for settling. */
    public val Settle: Easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

    /** A cubic-bezier(.4, 0, .2, 1) standard ease. */
    public val Standard: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

    /** A cubic-bezier(.2, 0, 0, 1) ease used for navigation morphs. */
    public val NavMorph: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
}

/** Fixed geometry shared by controls. */
@Immutable
public data class MullionDimens(
    /** Minimum touch target. Never smaller, regardless of visual size. */
    public val touchTarget: Dp = 48.dp,
    /** Standard control height. */
    public val control: Dp = 44.dp,
    /** Small control height. */
    public val controlSmall: Dp = 36.dp,
    /** Large control height. */
    public val controlLarge: Dp = 52.dp,
    /** Field height. */
    public val field: Dp = 42.dp,
    /** Card corner radius. */
    public val radiusCard: Dp = 12.dp,
    /** Panel corner radius. */
    public val radiusPanel: Dp = 18.dp,
    /** Window corner radius. */
    public val radiusWindow: Dp = 28.dp,
    /** Gutter between panes. */
    public val gutter: Dp = 24.dp,
    /** Rail width. */
    public val railWidth: Dp = 80.dp,
    /** Expanded drawer width. */
    public val drawerWidth: Dp = 216.dp,
    /** Divider hit area. */
    public val dividerWidth: Dp = 11.dp,
) {
    public companion object {
        public val Default: MullionDimens = MullionDimens()
    }
}
