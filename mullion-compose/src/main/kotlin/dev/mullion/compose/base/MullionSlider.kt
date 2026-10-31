package dev.mullion.compose.base

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import kotlin.math.roundToInt

/**
 * A single-cursor slider with optional step snapping.
 *
 * The track is 5dp, the thumb 20dp, but the pointer target is the full 32dp high
 * canvas. Tapping anywhere jumps to that value; dragging scrubs. Accessibility
 * services get a progress range plus increment/decrement actions.
 *
 * @param steps number of stops *between* the endpoints, as in Material. `0`
 *   means continuous.
 * @param valueLabel formats the readout; defaults to one decimal place.
 */
@Composable
public fun MullionSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    label: String? = null,
    valueLabel: ((Float) -> String)? = null,
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val span = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
    val fraction = ((value - valueRange.start) / span).coerceIn(0f, 1f)
    val effectiveSteps = steps.coerceAtLeast(0)
    val stepSize = if (effectiveSteps > 0) span / (effectiveSteps + 1) else 0f

    fun snap(raw: Float): Float {
        val clamped = raw.coerceIn(valueRange.start, valueRange.endInclusive)
        if (effectiveSteps <= 0) return clamped
        val index = ((clamped - valueRange.start) / span * (effectiveSteps + 1)).roundToInt()
        return (valueRange.start + index * stepSize)
            .coerceIn(valueRange.start, valueRange.endInclusive)
    }

    Column(modifier = modifier) {
        if (label != null || valueLabel != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (label != null) {
                    Text(
                        text = label.uppercase(),
                        style = MullionTextStyles.LabelSmall,
                        color = MullionColors.Faint,
                    )
                }
                Spacer(Modifier.weight(1f))
                if (valueLabel != null) {
                    Text(
                        text = valueLabel(value),
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                        color = MullionColors.Volt,
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        val actions = listOf(
            CustomAccessibilityAction("增加") {
                currentOnValueChange(snap(value + if (stepSize > 0f) stepSize else span * 0.05f))
                true
            },
            CustomAccessibilityAction("减少") {
                currentOnValueChange(snap(value - if (stepSize > 0f) stepSize else span * 0.05f))
                true
            },
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .semantics {
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        current = value,
                        range = valueRange,
                        steps = effectiveSteps,
                    )
                    customActions = actions
                }
                .pointerInput(enabled, valueRange, effectiveSteps) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        fun update(px: Float) {
                            val width = size.width.toFloat().coerceAtLeast(1f)
                            val raw = valueRange.start + (px / width) * span
                            currentOnValueChange(snap(raw))
                        }
                        update(down.position.x)
                        drag(down.id) { change ->
                            update(change.position.x)
                            change.consume()
                        }
                    }
                },
        ) {
            val alpha = if (enabled) 1f else 0.4f
            val trackY = size.height / 2f
            val trackHeight = 5.dp.toPx()
            val radius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
            val thumbRadius = 10.dp.toPx()

            drawRoundRect(
                color = MullionColors.Ink.copy(alpha = 0.14f * alpha),
                topLeft = Offset(0f, trackY - trackHeight / 2f),
                size = Size(size.width, trackHeight),
                cornerRadius = radius,
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(MullionColors.Volt, MullionColors.VoltHigh),
                    startX = 0f,
                    endX = size.width * fraction,
                ),
                topLeft = Offset(0f, trackY - trackHeight / 2f),
                size = Size(size.width * fraction, trackHeight),
                cornerRadius = radius,
                alpha = alpha,
            )

            if (effectiveSteps in 1..10) {
                for (i in 0 until effectiveSteps) {
                    val tickX = size.width * (i + 1f) / (effectiveSteps + 1f)
                    drawCircle(
                        color = MullionColors.Ink.copy(alpha = 0.25f * alpha),
                        radius = 1.5.dp.toPx(),
                        center = Offset(tickX, trackY),
                    )
                }
            }

            val thumbX = (size.width * fraction).coerceIn(thumbRadius, size.width - thumbRadius)
            drawCircle(
                color = MullionColors.Volt.copy(alpha = 0.18f * alpha),
                radius = thumbRadius + 4.dp.toPx(),
                center = Offset(thumbX, trackY),
            )
            drawCircle(
                brush = Brush.verticalGradient(
                    listOf(MullionColors.Ink, MullionColors.VoltHigh),
                ),
                radius = thumbRadius,
                center = Offset(thumbX, trackY),
                alpha = alpha,
            )
        }
    }
}
