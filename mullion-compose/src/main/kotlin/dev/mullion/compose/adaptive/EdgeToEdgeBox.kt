package dev.mullion.compose.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import dev.mullion.core.MullionInsets

/**
 * The one place window insets are resolved.
 *
 * Status bar, gesture bar and IME are folded into a single [PaddingValues].
 * API 21 and API 35 behave the same because the calculation goes through
 * Compose's compat window insets, never through reflection.
 *
 * @param includeIme whether the keyboard should push content.
 * @param applyPadding if `true`, the box itself pads its content. Set to `false`
 *   when the caller wants to draw chrome behind the inset bands (as the cabinet
 *   does) and position children with the supplied values.
 * @param simulatedInsets overrides the real insets, for previews and the sample.
 */
@Composable
public fun EdgeToEdgeBox(
    modifier: Modifier = Modifier,
    includeIme: Boolean = true,
    applyPadding: Boolean = true,
    simulatedInsets: MullionInsets? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val values: PaddingValues = if (simulatedInsets != null) {
        PaddingValues(
            start = simulatedInsets.leftDp.dp,
            top = simulatedInsets.topDp.dp,
            end = simulatedInsets.rightDp.dp,
            bottom = maxOf(
                simulatedInsets.bottomDp,
                if (includeIme) simulatedInsets.imeDp else 0,
            ).dp,
        )
    } else {
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val bars = WindowInsets.systemBars
        val ime = WindowInsets.ime
        PaddingValues(
            start = with(density) { bars.getLeft(density, layoutDirection).toDp() },
            top = with(density) { bars.getTop(density).toDp() },
            end = with(density) { bars.getRight(density, layoutDirection).toDp() },
            bottom = with(density) {
                maxOf(bars.getBottom(density), if (includeIme) ime.getBottom(density) else 0).toDp()
            },
        )
    }

    Box(modifier = if (applyPadding) modifier.padding(values) else modifier) {
        content(values)
    }
}
