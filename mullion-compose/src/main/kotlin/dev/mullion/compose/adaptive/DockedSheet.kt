package dev.mullion.compose.adaptive

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.Foldables
import dev.mullion.core.HingeInfo

/** Where a [DockedSheet] is allowed to dock. */
public enum class DockSide {
    /** Side-dock when a vertical hinge leaves room, otherwise bottom. */
    Auto,

    /** Dock to the start edge, inside the start leaf. */
    Start,

    /** Dock to the end edge, inside the end leaf. */
    End,

    /** Always a bottom sheet. */
    Bottom,
}

/**
 * A sheet that never draws across a physical hinge.
 *
 * With a vertical, separating hinge the sheet docks to one leaf and is sized to
 * fit inside it. With a horizontal hinge or no hinge it degrades to a bottom
 * panel, capped so it cannot swallow the screen. The content underneath stays
 * composed; opening and closing only moves the sheet.
 *
 * @param hinge pass an explicit hinge for previews; defaults to the window's.
 * @param minSheetWidth below this width a side dock is rejected and the sheet
 *   falls back to the bottom.
 * @param sheetContent receives a [ColumnScope] so actions can align naturally.
 */
@Composable
public fun DockedSheet(
    open: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hinge: HingeInfo? = null,
    dockSide: DockSide = DockSide.Auto,
    minSheetWidth: Dp = 140.dp,
    maxSheetWidth: Dp = 420.dp,
    title: (@Composable () -> Unit)? = null,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val window = mullionWindow()
    val fold = hinge ?: window.hinge

    BackHandler(enabled = open, onBack = onDismissRequest)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableWidthDp = maxWidth.value
        val leafWidthDp = if (fold.isVerticalSeparator) {
            Foldables.splitVertically(availableWidthDp, fold).paneWidthDp
        } else {
            availableWidthDp
        }
        val desiredSheetWidth = (availableWidthDp * 0.44f)
            .coerceAtMost(maxSheetWidth.value)
            .coerceAtMost(leafWidthDp)
        val canDockSide = fold.isVerticalSeparator &&
            window.bp.isExpanded &&
            desiredSheetWidth >= minSheetWidth.value

        val resolvedSide = when (dockSide) {
            DockSide.Auto -> if (canDockSide) DockSide.End else DockSide.Bottom
            else -> dockSide
        }
        val isBottom = resolvedSide == DockSide.Bottom

        val sideWidth: Dp = desiredSheetWidth
            .coerceAtLeast(minSheetWidth.value)
            .dp

        val maxSheetHeight: Dp = if (fold.isHorizontalSeparator) {
            val seamBottom = fold.bounds?.bottom
            if (seamBottom != null) {
                (maxHeight.value - seamBottom - 24f)
                    .coerceIn(160f, maxHeight.value * 0.75f)
                    .dp
            } else {
                (maxHeight * 0.45f)
            }
        } else {
            (maxHeight * 0.62f)
        }

        val scrimInteraction = remember { MutableInteractionSource() }

        content()

        AnimatedVisibility(
            visible = open,
            enter = fadeIn(tween(MullionMotion.StateMillis)),
            exit = fadeOut(tween(MullionMotion.StateMillis)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MullionColors.Scrim)
                    .clickable(
                        interactionSource = scrimInteraction,
                        indication = null,
                        onClick = onDismissRequest,
                    ),
            )
        }

        AnimatedVisibility(
            visible = open,
            modifier = Modifier.align(
                when (resolvedSide) {
                    DockSide.Start -> Alignment.CenterStart
                    DockSide.End -> Alignment.CenterEnd
                    DockSide.Bottom, DockSide.Auto -> Alignment.BottomCenter
                },
            ),
            enter = if (isBottom) {
                slideInVertically(
                    animationSpec = tween(MullionMotion.DockMillis, easing = MullionMotion.Settle),
                    initialOffsetY = { it },
                ) + fadeIn(tween(MullionMotion.StateMillis))
            } else {
                slideInHorizontally(
                    animationSpec = tween(MullionMotion.DockMillis, easing = MullionMotion.Settle),
                    initialOffsetX = { if (resolvedSide == DockSide.Start) -it else it },
                ) + fadeIn(tween(MullionMotion.StateMillis))
            },
            exit = if (isBottom) {
                slideOutVertically(
                    animationSpec = tween(MullionMotion.StateMillis, easing = MullionMotion.Standard),
                    targetOffsetY = { it },
                ) + fadeOut(tween(MullionMotion.StateMillis))
            } else {
                slideOutHorizontally(
                    animationSpec = tween(MullionMotion.StateMillis, easing = MullionMotion.Standard),
                    targetOffsetX = { if (resolvedSide == DockSide.Start) -it else it },
                ) + fadeOut(tween(MullionMotion.StateMillis))
            },
        ) {
            Column(
                modifier = if (isBottom) {
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .heightIn(max = maxSheetHeight)
                        .background(
                            color = MullionColors.Background.copy(alpha = 1f),
                            shape = RoundedCornerShape(22.dp),
                        )
                        .glass(
                            shape = RoundedCornerShape(22.dp),
                            level = GlassLevel.Floating,
                            borderColor = MullionColors.Volt.copy(alpha = 0.5f),
                        )
                        .semantics { paneTitle = "Docked sheet" }
                        .padding(18.dp)
                } else {
                    Modifier
                        .width(sideWidth)
                        .fillMaxHeight()
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .background(
                            color = MullionColors.Background.copy(alpha = 1f),
                            shape = RoundedCornerShape(22.dp),
                        )
                        .glass(
                            shape = RoundedCornerShape(22.dp),
                            level = GlassLevel.Floating,
                            borderColor = MullionColors.Volt.copy(alpha = 0.5f),
                        )
                        .semantics { paneTitle = "Docked sheet" }
                        .padding(18.dp)
                },
            ) {
                if (isBottom) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .background(MullionColors.EdgeHigh, RoundedCornerShape(999.dp))
                                .padding(vertical = 1.5.dp),
                        )
                    }
                }
                if (title != null) {
                    title()
                    Spacer(Modifier.height(12.dp))
                }
                sheetContent()
            }
        }
    }
}
