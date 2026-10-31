package dev.mullion.compose.adaptive

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.core.TypeScale
import dev.mullion.compose.window.currentBreakpoint

/**
 * Body copy that steps with the width class instead of stretching with it.
 *
 * Font size and line length change in two discrete steps. The measure is capped
 * at a readable number of `ch` characters, measured from the actual typeface
 * rather than guessed from an average glyph width.
 *
 * @param maxMeasureCh optional override for the readable measure.
 */
@Composable
public fun SizeClassText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    color: Color = Color.Unspecified,
    maxMeasureCh: Int? = null,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
) {
    val scale: TypeScale = TypeScale.forBp(currentBreakpoint())
    val base = style ?: MaterialTheme.typography.bodyMedium.let {
        if (it.fontFamily == null) MullionTextStyles.Body else it
    }
    val resolved = remember(base, scale) {
        base.copy(
            fontSize = scale.fontSizeSp.sp,
            lineHeight = scale.lineHeightSp.sp,
        )
    }
    val measure = maxMeasureCh ?: scale.maxMeasureCh
    val measurer = rememberTextMeasurer()
    val zeroWidthPx = remember(resolved, measurer) {
        measurer.measure(text = "0", style = resolved).size.width
    }
    val maxWidth = with(LocalDensity.current) { (zeroWidthPx * measure).toDp() }

    Text(
        text = text,
        modifier = modifier.widthIn(max = maxWidth),
        style = resolved,
        color = color,
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
        textAlign = textAlign,
    )
}
