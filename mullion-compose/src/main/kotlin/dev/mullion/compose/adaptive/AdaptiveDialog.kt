package dev.mullion.compose.adaptive

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.Bp

/** How an adaptive dialog chooses its container. */
public enum class DialogKind {
    /** Centered on compact windows, a side panel from Large up. */
    Auto,

    /** Always a centered card. */
    Centered,

    /** Always a side panel docked to the end edge. */
    SidePanel,
}

/**
 * One dialog body, two containers.
 *
 * On phones the body is a centered glass card. From [Bp.Large] it becomes a side
 * panel so the underlying list stays visible and useful. Back and scrim behavior
 * are identical in both forms, and focus moves into the panel when it opens.
 *
 * The dialog is an **inline overlay**: place it in a `Box` after the screen
 * content. This keeps it inside a simulation viewport (and inside a foldable
 * leaf) instead of escaping into a platform window.
 *
 * @param visible drives the entry and exit animations.
 */
@Composable
public fun AdaptiveDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    kind: DialogKind = DialogKind.Auto,
    dismissOnScrimClick: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val window = mullionWindow()
    val side: Boolean = when (kind) {
        DialogKind.Auto -> window.bp >= Bp.Large
        DialogKind.Centered -> false
        DialogKind.SidePanel -> true
    }

    BackHandler(enabled = visible, onBack = onDismissRequest)

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(visible) {
        if (visible) focusRequester.requestFocus()
    }

    val sideWidth = (window.widthDp * 0.36f).coerceIn(320f, 480f).dp
    val scrimInteraction = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(MullionMotion.StateMillis)),
            exit = fadeOut(tween(MullionMotion.StateMillis)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MullionColors.Scrim)
                    .clickable(
                        enabled = dismissOnScrimClick,
                        interactionSource = scrimInteraction,
                        indication = null,
                        onClick = onDismissRequest,
                    ),
            )
        }

        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(if (side) Alignment.CenterEnd else Alignment.Center),
            enter = if (side) {
                slideInHorizontally(
                    animationSpec = tween(MullionMotion.SheetMillis, easing = MullionMotion.Settle),
                    initialOffsetX = { it },
                ) + fadeIn(tween(MullionMotion.StateMillis))
            } else {
                scaleIn(
                    animationSpec = tween(MullionMotion.SheetMillis, easing = MullionMotion.Settle),
                    initialScale = 0.94f,
                ) + fadeIn(tween(MullionMotion.StateMillis))
            },
            exit = if (side) {
                slideOutHorizontally(
                    animationSpec = tween(MullionMotion.StateMillis, easing = MullionMotion.Standard),
                    targetOffsetX = { it },
                ) + fadeOut(tween(MullionMotion.StateMillis))
            } else {
                scaleOut(
                    animationSpec = tween(MullionMotion.StateMillis, easing = MullionMotion.Standard),
                    targetScale = 0.96f,
                ) + fadeOut(tween(MullionMotion.StateMillis))
            },
        ) {
            Column(
                modifier = Modifier
                    .then(
                        if (side) {
                            Modifier
                                .width(sideWidth)
                                .heightIn(max = (window.heightDp - 64f).dp)
                                .padding(vertical = 16.dp, horizontal = 16.dp)
                        } else {
                            Modifier
                                .widthIn(min = 280.dp, max = 520.dp)
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                        },
                    )
                    .background(
                        color = MullionColors.Background.copy(alpha = 1f),
                        shape = RoundedCornerShape(22.dp),
                    )
                    .glass(
                        shape = RoundedCornerShape(22.dp),
                        level = GlassLevel.Floating,
                        borderColor = MullionColors.Volt.copy(alpha = 0.45f),
                    )
                    .focusRequester(focusRequester)
                    .focusable()
                    .semantics { paneTitle = "Mullion dialog" }
                    .padding(22.dp),
                content = content,
            )
        }
    }
}
