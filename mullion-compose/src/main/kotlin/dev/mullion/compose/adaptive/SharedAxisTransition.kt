package dev.mullion.compose.adaptive

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.mullion.compose.theme.MullionMotion

/** The axis a shared-axis transition travels along. */
public enum class SharedAxis {
    /** Horizontal: old pane exits left, new pane enters right. */
    X,

    /** Vertical: old pane exits up, new pane enters down. */
    Y,

    /** Depth: old pane recedes, new pane comes forward. */
    Z,
}

/**
 * A single motion grammar for every width-class change.
 *
 * The old pane leaves along one axis while the new pane enters from the
 * opposite side. 28dp is enough to signal direction without turning a re-layout
 * into a carousel. The container is never left blank mid-transition because
 * enter and exit run together.
 *
 * @param durationMillis defaults to the framework's 240ms pane duration.
 */
@Composable
public fun <T> SharedAxisTransition(
    targetState: T,
    modifier: Modifier = Modifier,
    axis: SharedAxis = SharedAxis.X,
    durationMillis: Int = MullionMotion.PaneMillis,
    content: @Composable (T) -> Unit,
) {
    val distancePx = with(LocalDensity.current) { 28.dp.roundToPx() }
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            val forward = targetState != initialState
            val spec = tween<Float>(durationMillis, easing = MullionMotion.Standard)
            val enter = fadeIn(spec) + when (axis) {
                SharedAxis.X -> slideInHorizontally(tween(durationMillis, easing = MullionMotion.Standard)) {
                    if (forward) distancePx else -distancePx
                }

                SharedAxis.Y -> slideInVertically(tween(durationMillis, easing = MullionMotion.Standard)) {
                    if (forward) distancePx else -distancePx
                }

                SharedAxis.Z -> scaleIn(
                    animationSpec = tween(durationMillis, easing = MullionMotion.Standard),
                    initialScale = 0.94f,
                )
            }
            val exit = fadeOut(spec) + when (axis) {
                SharedAxis.X -> slideOutHorizontally(tween(durationMillis, easing = MullionMotion.Standard)) {
                    if (forward) -distancePx else distancePx
                }

                SharedAxis.Y -> slideOutVertically(tween(durationMillis, easing = MullionMotion.Standard)) {
                    if (forward) -distancePx else distancePx
                }

                SharedAxis.Z -> scaleOut(
                    animationSpec = tween(durationMillis, easing = MullionMotion.Standard),
                    targetScale = 1.04f,
                )
            }
            (enter togetherWith exit).using(SizeTransform(clip = false))
        },
        label = "MullionSharedAxis",
    ) { state ->
        content(state)
    }
}
