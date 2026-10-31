package dev.mullion.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.window.LocalMullionWindow
import dev.mullion.core.MullionWindowState
import kotlin.math.roundToInt

/**
 * Renders content as if the window were exactly [widthDp] wide, then scales it
 * down to fit the tray.
 *
 * This is not a screenshot or a mock: the subtree receives a real
 * [MullionWindowState], so every breakpoint read, pane decision and layout pass
 * executes with the simulated width. The scale is a rendering transform only.
 */
@Composable
fun SimulatedViewport(
    widthDp: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val density = LocalDensity.current
        val targetPx = with(density) { widthDp.dp.roundToPx() }.coerceAtLeast(1)
        val availablePx = with(density) { maxWidth.toPx() }.coerceAtLeast(1f)
        val previewScale = (availablePx / targetPx).coerceAtMost(1f)

        Column(modifier = Modifier.fillMaxWidth()) {
            Layout(
                content = {
                    CompositionLocalProvider(
                        LocalMullionWindow provides MullionWindowState(
                            widthDp = widthDp,
                            heightDp = 900,
                        ),
                    ) {
                        Box(modifier = Modifier.width(widthDp.dp)) {
                            content()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { measurables, constraints ->
                val placeable = measurables.first().measure(
                    Constraints(
                        minWidth = targetPx,
                        maxWidth = targetPx,
                        minHeight = 0,
                        maxHeight = Constraints.Infinity,
                    ),
                )
                val scale = if (constraints.maxWidth == Constraints.Infinity) {
                    1f
                } else {
                    (constraints.maxWidth.toFloat() / placeable.width.toFloat()).coerceAtMost(1f)
                }
                val height = (placeable.height * scale).roundToInt().coerceAtLeast(1)
                val width = if (constraints.maxWidth == Constraints.Infinity) {
                    placeable.width
                } else {
                    constraints.maxWidth
                }
                layout(width, height) {
                    placeable.placeWithLayer(0, 0) {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "zoom ${"%.2f".format(java.util.Locale.US, previewScale)}× · ${widthDp}dp",
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                    color = MullionColors.Faint,
                )
            }
        }
    }
}
