package dev.mullion.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.window.mullionWindow

private fun MullionPalette.toColorScheme(dark: Boolean) = if (dark) {
    darkColorScheme(
        primary = volt,
        onPrimary = onVolt,
        primaryContainer = volt.copy(alpha = 0.18f),
        onPrimaryContainer = voltHigh,
        secondary = mint,
        onSecondary = onVolt,
        tertiary = amber,
        onTertiary = onVolt,
        background = background,
        onBackground = ink,
        surface = backgroundLift,
        onSurface = ink,
        surfaceVariant = backgroundDeep,
        onSurfaceVariant = muted,
        outline = edgeHigh,
        outlineVariant = edge,
        error = signal,
        onError = onVolt,
        scrim = scrim,
    )
} else {
    lightColorScheme(
        primary = volt,
        onPrimary = onVolt,
        primaryContainer = volt.copy(alpha = 0.10f),
        onPrimaryContainer = volt,
        secondary = mint,
        onSecondary = Color.White,
        tertiary = amber,
        onTertiary = Color.White,
        background = background,
        onBackground = ink,
        surface = backgroundLift,
        onSurface = ink,
        surfaceVariant = backgroundDeep,
        onSurfaceVariant = muted,
        outline = edgeHigh,
        outlineVariant = edge,
        error = signal,
        onError = Color.White,
        scrim = scrim,
    )
}

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
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.SemiBold,
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
    bodySmall = MullionTextStyles.UISmall,
    labelLarge = TextStyle(
        fontFamily = MullionFonts.Sans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = MullionTextStyles.Label,
    labelSmall = MullionTextStyles.LabelSmall,
)

public val LocalMullionDimens: androidx.compose.runtime.ProvidableCompositionLocal<MullionDimens> =
    staticCompositionLocalOf { MullionDimens.Default }

/**
 * Applies the MULLION adaptive color scheme, type scale, shapes and glass tokens.
 *
 * The default follows the system. Pass [darkTheme] explicitly for a kiosk, a
 * design preview or a screenshot test. Every component reads
 * [LocalMullionPalette] through [MullionColors], so a split-screen window can
 * change theme without rebuilding component code.
 */
@Composable
public fun MullionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) DarkMullionPalette else LightMullionPalette
    val window = mullionWindow()
    val density = LocalDensity.current
    val dimens = remember(window.bp, density.fontScale) {
        MullionDimens.forBp(window.bp, density.fontScale)
    }
    CompositionLocalProvider(
        LocalMullionDimens provides dimens,
        LocalMullionPalette provides palette,
    ) {
        MaterialTheme(
            colorScheme = palette.toColorScheme(darkTheme),
            typography = MullionTypography,
            shapes = MullionShapes,
            content = content,
        )
    }
}
