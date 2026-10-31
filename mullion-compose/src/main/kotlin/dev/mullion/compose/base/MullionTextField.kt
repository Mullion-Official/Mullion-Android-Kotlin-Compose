package dev.mullion.compose.base

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles

/** Validation state of a field. */
public enum class MullionFieldState {
    Default,
    Error,
    Disabled,
}

/**
 * A glass field with a monospace value and an uppercase label.
 *
 * Focus is communicated by a volt border plus a 3dp translucent ring, matching
 * the focus tokens in the design system. The ring is drawn inside the field so
 * it can never clip against a neighboring control.
 */
@Composable
public fun MullionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    state: MullionFieldState = MullionFieldState.Default,
    supportingText: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textStyle: TextStyle = MullionTextStyles.Mono.copy(fontSize = 12.sp),
) {
    val enabled = state != MullionFieldState.Disabled
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(12.dp)
    val borderColor by animateColorAsState(
        targetValue = when {
            state == MullionFieldState.Error -> MullionColors.Signal.copy(alpha = 0.75f)
            focused -> MullionColors.Volt.copy(alpha = 0.75f)
            else -> MullionColors.Edge
        },
        animationSpec = tween(MullionMotion.StateMillis),
        label = "Field.border",
    )

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label.uppercase(),
                style = MullionTextStyles.LabelSmall,
                color = if (state == MullionFieldState.Error) {
                    MullionColors.Signal
                } else {
                    MullionColors.Faint
                },
                modifier = Modifier.padding(bottom = 6.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (singleLine) Modifier.height(42.dp) else Modifier.heightIn(min = 42.dp))
                .clip(shape)
                .background(
                    color = if (enabled) {
                        MullionColors.Ink.copy(alpha = 0.07f)
                    } else {
                        MullionColors.Ink.copy(alpha = 0.035f)
                    },
                    shape = shape,
                )
                .border(
                    width = if (focused) 1.5.dp else 1.dp,
                    color = borderColor,
                    shape = shape,
                )
                .padding(horizontal = 13.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(Modifier.width(8.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle,
                            color = MullionColors.Faint.copy(alpha = if (enabled) 1f else 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        textStyle = textStyle.copy(
                            color = MullionColors.Ink.copy(alpha = if (enabled) 1f else 0.45f),
                        ),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        singleLine = singleLine,
                        minLines = minLines,
                        visualTransformation = visualTransformation,
                        interactionSource = interaction,
                        cursorBrush = SolidColor(MullionColors.Volt),
                    )
                }
                if (trailingIcon != null) {
                    Spacer(Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }

        if (supportingText != null) {
            Text(
                text = supportingText,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
                color = if (state == MullionFieldState.Error) MullionColors.Signal else MullionColors.Faint,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
