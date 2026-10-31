package dev.mullion.compose.base

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionFonts

/** The three documented avatar sizes. */
public enum class MullionAvatarSize(
    public val size: Dp,
    public val fontSize: TextUnit,
) {
    S(size = 28.dp, fontSize = 10.sp),
    M(size = 40.dp, fontSize = 13.sp),
    L(size = 56.dp, fontSize = 17.sp),
}

/**
 * A letter avatar with a deterministic gradient.
 *
 * No network and no image loader: an initial is always available, and the same
 * name yields the same color on every device. Pass [image] to replace the
 * initials with any composable (for example, an AsyncImage).
 */
@Composable
public fun MullionAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: MullionAvatarSize = MullionAvatarSize.M,
    colorIndex: Int = (name.hashCode() and 0x7FFFFFFF) % MullionColors.AvatarPalette.size,
    image: (@Composable () -> Unit)? = null,
) {
    val palette = MullionColors.AvatarPalette[
        (colorIndex and 0x7FFFFFFF) % MullionColors.AvatarPalette.size
    ]
    Box(
        modifier = modifier
            .size(size.size)
            .clip(CircleShape)
            .background(Brush.linearGradient(palette), CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) {
            image()
        } else {
            Text(
                text = initials(name),
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = MullionFonts.Mono,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = size.fontSize,
                ),
                color = MullionColors.OnVolt,
            )
        }
    }
}

/**
 * An overlapping avatar stack with an overflow counter.
 *
 * @param max number of full avatars to show before collapsing into `+N`.
 */
@Composable
public fun MullionAvatarStack(
    names: List<String>,
    modifier: Modifier = Modifier,
    size: MullionAvatarSize = MullionAvatarSize.M,
    max: Int = 4,
    overlap: Dp = 12.dp,
    extraCount: Int = 0,
) {
    require(max >= 1) { "max must be at least 1" }
    val shown = names.take(max)
    val hidden = (names.size - shown.size) + extraCount.coerceAtLeast(0)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(-overlap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        shown.forEachIndexed { index, name ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MullionColors.Background, CircleShape)
                    .padding(1.dp),
            ) {
                MullionAvatar(name = name, size = size, colorIndex = index)
            }
        }
        if (hidden > 0) {
            Box(
                modifier = Modifier
                    .size(size.size)
                    .clip(CircleShape)
                    .background(MullionColors.Tonal, CircleShape)
                    .border(1.dp, MullionColors.Edge, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$hidden",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = MullionFonts.Mono,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (size.fontSize.value * 0.8f).sp,
                    ),
                    color = MullionColors.Ink,
                )
            }
        }
    }
}

private fun initials(name: String): String {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return "?"
    val parts = trimmed.split(Regex("\\s+"))
    return if (parts.size >= 2) {
        (parts[0].take(1) + parts[1].take(1)).uppercase()
    } else {
        trimmed.take(2).uppercase()
    }
}
