package dev.mullion.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MullionColorScheme = darkColorScheme(
    primary = MullionColors.Volt,
    onPrimary = MullionColors.OnVolt,
    primaryContainer = MullionColors.Volt.copy(alpha = 0.18f),
    onPrimaryContainer = MullionColors.VoltHigh,
    secondary = MullionColors.Mint,
    onSecondary = MullionColors.OnVolt,
    tertiary = MullionColors.Amber,
    onTertiary = MullionColors.OnVolt,
    background = MullionColors.Background,
    onBackground = MullionColors.Ink,
    surface = MullionColors.BackgroundLift,
    onSurface = MullionColors.Ink,
    surfaceVariant = MullionColors.Tonal,
    onSurfaceVariant = MullionColors.Muted,
    outline = MullionColors.EdgeHigh,
    outlineVariant = MullionColors.Edge,
    error = MullionColors.Signal,
    onError = MullionColors.OnVolt,
    scrim = MullionColors.Scrim,
)

private val MullionShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

private val MullionTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.Black,
        fontSize = 56.sp,
        lineHeight = 62.sp,
        letterSpacing = (-1.2).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.Black,
        fontSize = 44.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.9).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = MullionFonts.Mono,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = MullionTextStyles.BodyLarge,
    bodyMedium = MullionTextStyles.Body,
    bodySmall = MullionTextStyles.MonoSmall,
    labelLarge = TextStyle(
        fontFamily = MullionFonts.Mono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.6.sp,
    ),
    labelMedium = MullionTextStyles.Label,
    labelSmall = MullionTextStyles.LabelSmall,
)

public val LocalMullionDimens: androidx.compose.runtime.ProvidableCompositionLocal<MullionDimens> =
    staticCompositionLocalOf { MullionDimens.Default }

/**
 * Applies the MULLION color scheme, type scale, shapes and glass tokens.
 *
 * The theme is intentionally a single dark mode: glass only reads as glass over
 * a deep, saturated background. A light theme is on the roadmap.
 */
@Composable
public fun MullionTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalMullionDimens provides MullionDimens.Default) {
        MaterialTheme(
            colorScheme = MullionColorScheme,
            typography = MullionTypography,
            shapes = MullionShapes,
            content = content,
        )
    }
}
