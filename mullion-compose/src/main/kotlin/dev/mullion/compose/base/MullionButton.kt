package dev.mullion.compose.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.MullionDimens

/** Visual weight of a button. */
public enum class MullionButtonVariant {
    Filled,
    Tonal,
    Outlined,
    Text,
}

/** Physical size of a button. Touch targets stay 48dp regardless. */
public enum class MullionButtonSize(
    public val height: Dp,
    public val horizontalPadding: Dp,
    public val fontSize: TextUnit,
) {
    S(height = 36.dp, horizontalPadding = 12.dp, fontSize = 10.5.sp),
    M(height = 44.dp, horizontalPadding = 18.dp, fontSize = 12.sp),
    L(height = 52.dp, horizontalPadding = 22.dp, fontSize = 13.sp),
}

/**
 * The primary action control.
 *
 * The visual height can be 36 / 44 / 52dp, but the clickable wrapper is always
 * at least 48dp so the control is reachable. Press feedback is a 3% scale, not a
 * ripple: it survives inside scaled previews and reads as physical travel.
 */
@Composable
public fun MullionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MullionButtonVariant = MullionButtonVariant.Filled,
    size: MullionButtonSize = MullionButtonSize.M,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(MullionMotion.MicroMillis),
        label = "Button.press",
    )
    val clickable = enabled && !loading

    Box(
        modifier = modifier.heightIn(min = MullionDimens.Default.touchTarget),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .height(size.height)
                .clip(CircleShape)
                .then(
                    if (variant == MullionButtonVariant.Filled && enabled) {
                        Modifier.shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = MullionColors.Volt.copy(alpha = 0.4f),
                            spotColor = MullionColors.Volt.copy(alpha = 0.55f),
                        )
                    } else {
                        Modifier
                    },
                )
                .background(
                    brush = backgroundBrush(variant),
                    shape = CircleShape,
                )
                .then(
                    when (variant) {
                        MullionButtonVariant.Tonal ->
                            Modifier.border(1.dp, MullionColors.Edge, CircleShape)

                        MullionButtonVariant.Outlined ->
                            Modifier.border(1.dp, MullionColors.Volt.copy(alpha = 0.55f), CircleShape)

                        else -> Modifier
                    },
                )
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = clickable,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(horizontal = size.horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(15.dp),
                    color = contentColor(variant),
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(9.dp))
            } else if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MullionTextStyles.Mono.copy(
                    fontSize = size.fontSize,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = contentColor(variant).copy(alpha = if (enabled) 1f else 0.45f),
                maxLines = 1,
            )
            if (trailingIcon != null) {
                Spacer(Modifier.width(8.dp))
                trailingIcon()
            }
        }
    }
}

/**
 * A circular icon action. The foreground is supplied by the caller; MULLION
 * does not ship an icon set, which keeps the library free of a 2 MB
 * material-icons dependency.
 */
@Composable
public fun MullionIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MullionButtonVariant = MullionButtonVariant.Filled,
    size: MullionButtonSize = MullionButtonSize.M,
    enabled: Boolean = true,
    contentDescription: String? = null,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(MullionMotion.MicroMillis),
        label = "IconButton.press",
    )
    Box(
        modifier = modifier.heightIn(min = MullionDimens.Default.touchTarget),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .size(size.height)
                .clip(CircleShape)
                .background(backgroundBrush(variant), CircleShape)
                .then(
                    when (variant) {
                        MullionButtonVariant.Tonal ->
                            Modifier.border(1.dp, MullionColors.Edge, CircleShape)

                        MullionButtonVariant.Outlined ->
                            Modifier.border(1.dp, MullionColors.Volt.copy(alpha = 0.55f), CircleShape)

                        else -> Modifier
                    },
                )
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .semantics {
                    if (contentDescription != null) this.contentDescription = contentDescription
                },
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

private fun backgroundBrush(variant: MullionButtonVariant): Brush = when (variant) {
    MullionButtonVariant.Filled -> Brush.verticalGradient(
        listOf(MullionColors.VoltHigh, MullionColors.Volt),
    )

    MullionButtonVariant.Tonal -> Brush.verticalGradient(
        listOf(MullionColors.Tonal, MullionColors.Tonal),
    )

    MullionButtonVariant.Outlined,
    MullionButtonVariant.Text,
    -> Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
}

private fun contentColor(variant: MullionButtonVariant): Color = when (variant) {
    MullionButtonVariant.Filled -> MullionColors.OnVolt
    MullionButtonVariant.Tonal -> MullionColors.Ink
    MullionButtonVariant.Outlined,
    MullionButtonVariant.Text,
    -> MullionColors.Volt
}
