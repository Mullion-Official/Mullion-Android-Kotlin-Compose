package dev.mullion.compose.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.glass

/**
 * A glass card, optionally tappable.
 *
 * The card is the only base component that owns a shadow; it establishes the
 * cabinet's depth. Hover and focus are intentionally left to the platform, while
 * press feedback is a subtle scale.
 */
@Composable
public fun MullionCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(MullionMotion.MicroMillis),
        label = "Card.press",
    )
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = MullionColors.Ink.copy(alpha = 0.08f),
                spotColor = MullionColors.Ink.copy(alpha = 0.12f),
            )
            .glass(
                shape = shape,
                level = GlassLevel.Panel,
                borderColor = if (selected) {
                    MullionColors.Volt.copy(alpha = 0.65f)
                } else {
                    MullionColors.Edge
                },
            )
            .then(
                if (selected) {
                    Modifier.border(1.dp, MullionColors.Volt.copy(alpha = 0.65f), shape)
                } else {
                    Modifier
                },
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interaction,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .padding(contentPadding),
        content = content,
    )
}
