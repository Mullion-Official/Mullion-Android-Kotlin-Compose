package dev.mullion.compose.adaptive

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.HingeInfo
import dev.mullion.core.HingePosture

/**
 * Two leaves separated by a physical seam.
 *
 * Geometry comes from [HingeInfo]: each leaf gets an equal share of the width
 * minus the seam and a 24dp safe gap on both sides of it. Nothing is drawn over
 * the seam. When the available width cannot honor [minPaneWidth], the caller's
 * [singlePane] fallback is shown instead of squeezing both leaves.
 *
 * A flat foldable renders as one continuous surface, which is the point: the
 * layout should not flicker when a device opens to 180°.
 *
 * @param hinge the physical seam. Defaults to the window's detected hinge.
 * @param onHingeChanged called whenever the posture or orientation changes.
 */
@Composable
public fun FoldablePane(
    start: @Composable () -> Unit,
    end: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    hinge: HingeInfo? = null,
    safeGap: Dp = 24.dp,
    minPaneWidth: Dp = 240.dp,
    singlePane: (@Composable () -> Unit)? = null,
    onHingeChanged: ((HingeInfo) -> Unit)? = null,
) {
    val fold = hinge ?: mullionWindow().hinge
    LaunchedEffect(fold) { onHingeChanged?.invoke(fold) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val fullWidth = maxWidth
        when {
            fold.isVerticalSeparator -> {
                val seam = fold.separatorWidthDp.dp
                val enough = maxWidth - seam - safeGap * 2 >= minPaneWidth * 2
                if (!enough) {
                    Box(Modifier.fillMaxSize()) { (singlePane ?: start)() }
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(Modifier.weight(1f).fillMaxHeight()) { start() }
                        HingeStrip(
                            modifier = Modifier.width(seam + safeGap * 2).fillMaxHeight(),
                            horizontal = false,
                            seamDp = seam,
                            gapDp = safeGap,
                        )
                        Box(Modifier.weight(1f).fillMaxHeight()) { end() }
                    }
                }
            }

            fold.isHorizontalSeparator -> {
                val seam = fold.separatorHeightDp.dp
                val enough = maxHeight - seam - safeGap * 2 >= minPaneWidth * 2
                if (!enough) {
                    Box(Modifier.fillMaxSize()) { (singlePane ?: start)() }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(Modifier.weight(1f).fillMaxWidth()) { start() }
                        HingeStrip(
                            modifier = Modifier.width(fullWidth).height(seam + safeGap * 2),
                            horizontal = true,
                            seamDp = seam,
                            gapDp = safeGap,
                        )
                        Box(Modifier.weight(1f).fillMaxWidth()) { end() }
                    }
                }
            }

            fold.posture != HingePosture.Flat && fold.orientation != null -> {
                // A non-separating half-open posture still gets two equal leaves.
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f).fillMaxHeight()) { start() }
                    Box(Modifier.weight(1f).fillMaxHeight()) { end() }
                }
            }

            else -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f).fillMaxHeight()) { start() }
                    Box(Modifier.weight(1f).fillMaxHeight()) { end() }
                }
            }
        }
    }
}

@Composable
private fun HingeStrip(
    modifier: Modifier,
    horizontal: Boolean,
    seamDp: Dp,
    gapDp: Dp,
) {
    val signal = MullionColors.Signal
    Canvas(modifier = modifier) {
        val dash = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 7.dp.toPx()))
        if (horizontal) {
            val y = size.height / 2f
            drawRect(
                color = signal.copy(alpha = 0.06f),
                topLeft = Offset(0f, y - seamDp.toPx() / 2f),
                size = androidx.compose.ui.geometry.Size(size.width, seamDp.toPx()),
            )
            drawLine(
                color = signal.copy(alpha = 0.72f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dash,
            )
        } else {
            val x = size.width / 2f
            drawRect(
                color = signal.copy(alpha = 0.06f),
                topLeft = Offset(x - seamDp.toPx() / 2f, 0f),
                size = androidx.compose.ui.geometry.Size(seamDp.toPx(), size.height),
            )
            drawLine(
                color = signal.copy(alpha = 0.72f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dash,
            )
        }
    }
}
