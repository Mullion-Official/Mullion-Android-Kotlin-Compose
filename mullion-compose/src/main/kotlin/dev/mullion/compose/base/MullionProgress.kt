package dev.mullion.compose.base

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles

/** Shape of a progress indicator. */
public enum class MullionProgressShape {
    Linear,
    Circular,
}

/**
 * Progress, determinate or not.
 *
 * Passing `null` switches to an indeterminate loop. The loop is drawn on a
 * canvas rather than animated layout, so it never triggers a recomposition
 * per frame.
 */
@Composable
public fun MullionProgressBar(
    progress: Float?,
    modifier: Modifier = Modifier,
    shape: MullionProgressShape = MullionProgressShape.Linear,
    label: String? = null,
    showValue: Boolean = false,
    circularSize: Dp = 26.dp,
    contentDescription: String? = null,
) {
    val safeProgress = progress?.coerceIn(0f, 1f)
    Column(modifier = modifier) {
        if (label != null || (showValue && safeProgress != null)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (label != null) {
                    Text(
                        text = label.uppercase(),
                        style = MullionTextStyles.LabelSmall,
                        color = MullionColors.Faint,
                    )
                }
                if (showValue && safeProgress != null) {
                    Text(
                        text = "${(safeProgress * 100).toInt()}%",
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                        color = MullionColors.Volt,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (shape == MullionProgressShape.Linear) {
            LinearProgress(progress = safeProgress, modifier = Modifier.fillMaxWidth())
        } else {
            Box(
                modifier = Modifier.size(circularSize),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgress(
                    progress = safeProgress,
                    diameter = circularSize,
                    contentDescription = contentDescription,
                )
            }
        }
    }
}

@Composable
private fun LinearProgress(
    progress: Float?,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(
        targetValue = progress ?: 0f,
        animationSpec = tween(160, easing = LinearEasing),
        label = "Progress.value",
    )
    val infinite = rememberInfiniteTransition(label = "Progress.loop")
    val sweep by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Progress.sweep",
    )

    Canvas(
        modifier = modifier
            .height(6.dp)
            .semanticsProgress(progress),
    ) {
        val radius = CornerRadius(size.height / 2f, size.height / 2f)
        drawRoundRect(
            color = MullionColors.Ink.copy(alpha = 0.12f),
            size = size,
            cornerRadius = radius,
        )
        if (progress == null) {
            val barWidth = size.width * 0.32f
            val start = (size.width + barWidth) * sweep - barWidth
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(MullionColors.Volt, MullionColors.VoltHigh),
                    startX = start,
                    endX = start + barWidth,
                ),
                topLeft = Offset(start, 0f),
                size = Size(barWidth, size.height),
                cornerRadius = radius,
            )
        } else {
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(MullionColors.Volt, MullionColors.VoltHigh),
                    startX = 0f,
                    endX = size.width,
                ),
                size = Size(size.width * animated, size.height),
                cornerRadius = radius,
            )
        }
    }
}

@Composable
private fun CircularProgress(
    progress: Float?,
    diameter: Dp,
    contentDescription: String?,
) {
    val infinite = rememberInfiniteTransition(label = "Progress.circular")
    val rotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Progress.rotation",
    )
    val stroke = 2.5.dp

    Canvas(
        modifier = Modifier
            .size(diameter)
            .semanticsProgress(progress, description = contentDescription),
    ) {
        val strokePx = stroke.toPx()
        drawCircle(
            color = MullionColors.Ink.copy(alpha = 0.14f),
            style = Stroke(width = strokePx),
        )
        if (progress != null) {
            drawArc(
                color = MullionColors.Volt,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )
        } else {
            rotate(rotation) {
                drawArc(
                    color = MullionColors.Volt,
                    startAngle = -90f,
                    sweepAngle = 280f,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                )
            }
        }
    }
}

@Composable
private fun Modifier.semanticsProgress(
    progress: Float?,
    description: String? = null,
): Modifier = this.semantics {
    if (progress != null) {
        progressBarRangeInfo = ProgressBarRangeInfo(
            current = progress,
            range = 0f..1f,
            steps = 0,
        )
    }
    if (description != null) {
        contentDescription = description
    }
}
