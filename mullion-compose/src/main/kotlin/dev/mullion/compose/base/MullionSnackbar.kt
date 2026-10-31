package dev.mullion.compose.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.NavigationMode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay

/** Outcome of a snackbar shown through [MullionSnackbarHostState.showSnackbar]. */
public enum class MullionSnackbarResult {
    ActionPerformed,
    Dismissed,
}

/** The currently visible snackbar. */
public class MullionSnackbarData internal constructor(
    internal val id: Long,
    public val message: String,
    public val actionLabel: String?,
    public val durationMillis: Long,
    internal val onAction: (() -> Unit)?,
    internal val completion: CompletableDeferred<MullionSnackbarResult>,
) {
    internal var completed: Boolean = false
}

/**
 * A queue-free snackbar state.
 *
 * Only one snackbar is visible at a time; showing a new one dismisses the
 * previous one and resumes its coroutine. The host is responsible for the timer,
 * so a configuration change can re-show the same data without losing it.
 */
@Stable
public class MullionSnackbarHostState {
    private var nextId: Long = 0L

    /** The snackbar to render, or `null`. */
    public var current: MullionSnackbarData? by mutableStateOf(null)
        private set

    /**
     * Shows a snackbar and suspends until it is dismissed or its action is used.
     */
    public suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        durationMillis: Long = MullionMotion.SnackbarMillis,
        onAction: (() -> Unit)? = null,
    ): MullionSnackbarResult {
        current?.completion?.complete(MullionSnackbarResult.Dismissed)
        val data = MullionSnackbarData(
            id = nextId++,
            message = message,
            actionLabel = actionLabel,
            durationMillis = durationMillis.coerceAtLeast(0L),
            onAction = onAction,
            completion = CompletableDeferred(),
        )
        current = data
        return data.completion.await()
    }

    /** Dismisses the current snackbar, if any. */
    public fun dismiss(result: MullionSnackbarResult = MullionSnackbarResult.Dismissed) {
        val data = current ?: return
        finish(data, result)
    }

    internal fun finish(data: MullionSnackbarData, result: MullionSnackbarResult) {
        if (data.completed) return
        data.completed = true
        if (current === data) current = null
        data.completion.complete(result)
    }
}

/** Remembers a [MullionSnackbarHostState] across recompositions. */
@Composable
public fun rememberMullionSnackbarHostState(): MullionSnackbarHostState =
    remember { MullionSnackbarHostState() }

/**
 * The snackbar host.
 *
 * Placement is part of the adaptive contract:
 *  - bottom bar windows: full width, docked above the bar
 *  - rail windows: a centered 420dp capsule
 *  - drawer windows: a 292dp capsule anchored to the end
 *
 * Place it last in a `Box` so it overlays the screen. It never steals touches
 * outside the snackbar itself.
 */
@Composable
public fun MullionSnackbarHost(
    state: MullionSnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val window = mullionWindow()
    val data = state.current

    LaunchedEffect(data?.id) {
        val active = data ?: return@LaunchedEffect
        if (active.durationMillis > 0L) {
            delay(active.durationMillis)
            state.finish(active, MullionSnackbarResult.Dismissed)
        }
    }

    val alignment = when (window.navigationMode) {
        NavigationMode.BottomBar, NavigationMode.Rail -> Alignment.BottomCenter
        NavigationMode.Drawer -> Alignment.BottomEnd
    }
    val widthModifier = when (window.navigationMode) {
        NavigationMode.BottomBar -> Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        NavigationMode.Rail -> Modifier.widthIn(max = 420.dp).fillMaxWidth(0.86f)
        NavigationMode.Drawer -> Modifier.width(292.dp).padding(end = 16.dp)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentAlignment = alignment,
    ) {
        AnimatedVisibility(
            visible = data != null,
            enter = slideInVertically(
                animationSpec = tween(MullionMotion.SheetMillis, easing = MullionMotion.Settle),
                initialOffsetY = { it },
            ) + fadeIn(tween(MullionMotion.StateMillis)),
            exit = slideOutVertically(
                animationSpec = tween(MullionMotion.StateMillis, easing = MullionMotion.Standard),
                targetOffsetY = { it },
            ) + fadeOut(tween(MullionMotion.StateMillis)),
        ) {
            if (data != null) {
                SnackbarCard(
                    data = data,
                    modifier = widthModifier,
                    onAction = {
                        data.onAction?.invoke()
                        state.finish(data, MullionSnackbarResult.ActionPerformed)
                    },
                )
            }
        }
    }
}

@Composable
private fun SnackbarCard(
    data: MullionSnackbarData,
    modifier: Modifier,
    onAction: () -> Unit,
) {
    Row(
        modifier = modifier
            .heightIn(min = 52.dp)
            .glass(
                shape = RoundedCornerShape(14.dp),
                level = GlassLevel.Floating,
                borderColor = MullionColors.Volt.copy(alpha = 0.35f),
            )
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            text = data.message,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 11.sp),
            color = MullionColors.Ink,
            modifier = Modifier.weight(1f),
        )
        if (data.actionLabel != null) {
            Spacer(Modifier.width(12.dp))
            Text(
                text = data.actionLabel.uppercase(),
                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                color = MullionColors.Volt,
                modifier = Modifier
                    .background(MullionColors.Transparent)
                    .clickable(onClick = onAction)
                    .padding(horizontal = 4.dp, vertical = 6.dp),
            )
        }
    }
}
