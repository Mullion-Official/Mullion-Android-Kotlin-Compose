package dev.mullion.compose.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mullion.compose.theme.MullionColors

/**
 * Split position holder, saved across configuration changes.
 *
 * The fraction is clamped to `0.02..0.98`; the component applies a stricter
 * clamp derived from [DraggableDivider.minPaneWidth] at layout time.
 */
@Stable
public class DraggableSplitState(initialFraction: Float = 0.42f) {
    private var rawFraction by mutableFloatStateOf(initialFraction.coerceIn(0.05f, 0.95f))

    /** Current split, `0f` = all second pane, `1f` = all first pane. */
    public var fraction: Float
        get() = rawFraction
        set(value) {
            rawFraction = value.coerceIn(0.02f, 0.98f)
        }

    /** Restores the cabinet's default 42 / 58 split. */
    public fun reset() {
        fraction = 0.42f
    }

    public companion object {
        /** State restoration used by [rememberDraggableSplitState]. */
        public val Saver: Saver<DraggableSplitState, Float> = Saver(
            save = { it.fraction },
            restore = { DraggableSplitState(it) },
        )
    }
}

/** Creates a split state that survives rotation and process death. */
@Composable
public fun rememberDraggableSplitState(initialFraction: Float = 0.42f): DraggableSplitState =
    rememberSaveable(saver = DraggableSplitState.Saver) {
        DraggableSplitState(initialFraction)
    }

/**
 * A draggable two-pane split with a hard minimum pane width.
 *
 * The divider is 11dp wide for the pointer, but only 5dp of it is painted. Drag
 * updates are clamped before they reach layout, so a pane can never become
 * unreadable; releasing simply stays at the clamp. The split is advertised to
 * accessibility services as two custom actions.
 *
 * @param onSplitChange reports the resolved leaf widths after every layout.
 */
@Composable
public fun DraggableDivider(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: DraggableSplitState = rememberDraggableSplitState(),
    minPaneWidth: Dp = 120.dp,
    dividerWidth: Dp = 11.dp,
    onSplitChange: ((left: Dp, right: Dp) -> Unit)? = null,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val contentWidth = (maxWidth - dividerWidth).coerceAtLeast(1.dp)
        val minFraction = (minPaneWidth / contentWidth).coerceIn(0.02f, 0.48f)
        val maxFraction = 1f - minFraction
        val fraction = state.fraction.coerceIn(minFraction, maxFraction)
        val leftWidth = contentWidth * fraction
        val rightWidth = contentWidth - leftWidth
        val contentWidthPx = with(LocalDensity.current) { contentWidth.toPx() }
            .coerceAtLeast(1f)

        val dragState = rememberDraggableState { delta ->
            state.fraction = (state.fraction + delta / contentWidthPx)
                .coerceIn(minFraction, maxFraction)
        }

        val actions = listOf(
            CustomAccessibilityAction("将分隔条向左移动") {
                state.fraction = (fraction - 0.05f).coerceIn(minFraction, maxFraction)
                true
            },
            CustomAccessibilityAction("将分隔条向右移动") {
                state.fraction = (fraction + 0.05f).coerceIn(minFraction, maxFraction)
                true
            },
        )

        LaunchedEffect(leftWidth, rightWidth) {
            onSplitChange?.invoke(leftWidth, rightWidth)
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.width(leftWidth).fillMaxHeight()) { first() }
            Box(
                modifier = Modifier
                    .width(dividerWidth)
                    .fillMaxHeight()
                    .draggable(
                        state = dragState,
                        orientation = Orientation.Horizontal,
                    )
                    .semantics {
                        contentDescription = "分栏分隔条"
                        customActions = actions
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(64.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    MullionColors.Volt.copy(alpha = 0.95f),
                                    MullionColors.Volt.copy(alpha = 0.35f),
                                ),
                            ),
                            shape = RoundedCornerShape(999.dp),
                        ),
                )
            }
            Box(modifier = Modifier.width(rightWidth).fillMaxHeight()) { second() }
        }
    }
}
