package dev.mullion.compose.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.LocalMullionDimens
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionDimens
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles

/**
 * A checkbox with an explicit 48dp row target.
 *
 * Supports the three visual states MULLION documents: unchecked, checked and
 * indeterminate. The check mark is drawn on a canvas, so it stays crisp at any
 * density and does not depend on an icon font.
 */
@Composable
public fun MullionCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indeterminate: Boolean = false,
    label: String? = null,
) {
    Row(
        modifier = modifier
            .heightIn(min = LocalMullionDimens.current.touchTarget)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onCheckedChange,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckboxGlyph(
            checked = checked,
            indeterminate = indeterminate,
            enabled = enabled,
        )
        if (label != null) {
            Spacer(Modifier.width(9.dp))
            Text(
                text = label,
                style = MullionTextStyles.UISmall.copy(fontSize = 11.sp),
                color = when {
                    !enabled -> MullionColors.Faint.copy(alpha = 0.5f)
                    checked || indeterminate -> MullionColors.Ink
                    else -> MullionColors.Muted
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CheckboxGlyph(
    checked: Boolean,
    indeterminate: Boolean,
    enabled: Boolean,
) {
    val active = checked || indeterminate
    val progress by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = tween(160),
        label = "Checkbox.fill",
    )
    val alpha = if (enabled) 1f else 0.45f
    val ink = MullionColors.Ink
    val volt = MullionColors.Volt
    val voltHigh = MullionColors.VoltHigh
    val onVolt = MullionColors.OnVolt

    Canvas(modifier = Modifier.size(18.dp)) {
        val corner = CornerRadius(5.dp.toPx(), 5.dp.toPx())
        drawRoundRect(
            color = ink.copy(alpha = 0.06f * alpha),
            cornerRadius = corner,
        )
        if (progress > 0f) {
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(voltHigh, volt),
                ),
                cornerRadius = corner,
                alpha = progress * alpha,
            )
        }
        drawRoundRect(
            color = if (active) {
                volt.copy(alpha = 0.55f * alpha)
            } else {
                ink.copy(alpha = 0.3f * alpha)
            },
            cornerRadius = corner,
            style = Stroke(width = 1.5.dp.toPx()),
        )

        if (indeterminate) {
            drawLine(
                color = onVolt.copy(alpha = alpha),
                start = Offset(size.width * 0.25f, size.height * 0.5f),
                end = Offset(size.width * 0.75f, size.height * 0.5f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        } else if (checked) {
            val path = Path().apply {
                moveTo(size.width * 0.22f, size.height * 0.52f)
                lineTo(size.width * 0.43f, size.height * 0.72f)
                lineTo(size.width * 0.78f, size.height * 0.3f)
            }
            drawPath(
                path = path,
                color = onVolt.copy(alpha = alpha),
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}

/**
 * A radio button. Selection semantics are owned by the caller's group; this
 * control reports taps through [onSelect].
 */
@Composable
public fun MullionRadio(
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
) {
    Row(
        modifier = modifier
            .heightIn(min = LocalMullionDimens.current.touchTarget)
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onSelect,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioGlyph(selected = selected, enabled = enabled)
        if (label != null) {
            Spacer(Modifier.width(9.dp))
            Text(
                text = label,
                style = MullionTextStyles.UISmall.copy(fontSize = 11.sp),
                color = when {
                    !enabled -> MullionColors.Faint.copy(alpha = 0.5f)
                    selected -> MullionColors.Ink
                    else -> MullionColors.Muted
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RadioGlyph(selected: Boolean, enabled: Boolean) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(MullionMotion.StateMillis),
        label = "Radio.fill",
    )
    val alpha = if (enabled) 1f else 0.45f
    val ink = MullionColors.Ink
    val volt = MullionColors.Volt
    val voltHigh = MullionColors.VoltHigh
    val onVolt = MullionColors.OnVolt

    Canvas(modifier = Modifier.size(18.dp)) {
        val radius = size.minDimension / 2f
        if (progress > 0f) {
            drawCircle(
                brush = Brush.linearGradient(
                    listOf(voltHigh, volt),
                ),
                radius = radius,
                alpha = progress * alpha,
            )
        }
        drawCircle(
            color = if (selected) {
                volt.copy(alpha = 0.55f * alpha)
            } else {
                ink.copy(alpha = 0.3f * alpha)
            },
            radius = radius - 0.75.dp.toPx(),
            style = Stroke(width = 1.5.dp.toPx()),
        )
        if (progress > 0f) {
            drawCircle(
                color = onVolt.copy(alpha = progress * alpha),
                radius = 3.5.dp.toPx() * progress,
            )
        }
    }
}
