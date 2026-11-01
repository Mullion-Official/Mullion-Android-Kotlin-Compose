package dev.mullion.compose.window

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import dev.mullion.compose.theme.LocalMullionDimens
import dev.mullion.compose.theme.MullionDimens
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import dev.mullion.core.Bp
import dev.mullion.core.DpRect
import dev.mullion.core.HingeInfo
import dev.mullion.core.HingeOrientation
import dev.mullion.core.HingePosture
import dev.mullion.core.MullionInsets
import dev.mullion.core.MullionWindowState
import dev.mullion.core.NavigationMode
import kotlin.math.roundToInt

/**
 * The window state seen by every MULLION composable.
 *
 * `null` means "measure the real window". The sample cabinet overrides this
 * local to simulate 375 / 760 / 1180dp viewports without rebuilding the tree.
 */
public val LocalMullionWindow: androidx.compose.runtime.ProvidableCompositionLocal<MullionWindowState?> =
    staticCompositionLocalOf { null }

/**
 * Measures the real Compose window, including system insets and fold geometry.
 *
 * The result is a single immutable value: system bars, IME, window size and
 * hinge arrive together, so a resize produces one recomposition instead of four.
 */
@Composable
public fun rememberMullionWindowState(): MullionWindowState {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val windowInfo = LocalWindowInfo.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val container = windowInfo.containerSize
    val widthPx = container.width.takeIf { it > 0 }
    val heightPx = container.height.takeIf { it > 0 }
    val widthDp = if (widthPx != null) {
        with(density) { widthPx.toDp().value.roundToInt() }
    } else {
        configuration.screenWidthDp
    }
    val heightDp = if (heightPx != null) {
        with(density) { heightPx.toDp().value.roundToInt() }
    } else {
        configuration.screenHeightDp
    }

    val insets = rememberInsets()
    val hinge = rememberHingeInfo(activity)
    val fontScale = density.fontScale

    return remember(widthDp, heightDp, insets, hinge, fontScale) {
        MullionWindowState(
            widthDp = widthDp,
            heightDp = heightDp,
            insets = insets,
            hinge = hinge,
            fontScale = fontScale,
        )
    }
}

/**
 * Returns the window state: a composition-local override when present, the real
 * window otherwise.
 */
@Composable
public fun mullionWindow(): MullionWindowState {
    val overridden = LocalMullionWindow.current
    return overridden ?: rememberMullionWindowState()
}

/**
 * Provides a window state to a subtree.
 *
 * Pass a value to simulate a viewport (the sample does this for every tray), or
 * omit it to measure the real window.
 */
@Composable
public fun MullionWindowProvider(
    window: MullionWindowState? = null,
    content: @Composable () -> Unit,
) {
    val resolved = window ?: LocalMullionWindow.current ?: rememberMullionWindowState()
    val density = LocalDensity.current
    val dimens = remember(resolved.bp, density.fontScale) {
        MullionDimens.forBp(resolved.bp, density.fontScale)
    }
    CompositionLocalProvider(
        LocalMullionWindow provides resolved,
        LocalMullionDimens provides dimens,
        content = content,
    )
}

/** The current width class. This is the only breakpoint call a screen needs. */
@Composable
public fun currentBreakpoint(): Bp = mullionWindow().bp

/** The current navigation morphology. */
@Composable
public fun currentNavigationMode(): NavigationMode = mullionWindow().navigationMode

/** The current hinge, or [HingeInfo.None]. */
@Composable
public fun currentHinge(): HingeInfo = mullionWindow().hinge

/** The current system insets, including the IME. */
@Composable
public fun currentInsets(): MullionInsets = mullionWindow().insets

/** The window width in dp, including system insets. */
@Composable
public fun currentWindowWidthDp(): Int = mullionWindow().widthDp

@Composable
private fun rememberInsets(): MullionInsets {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val systemBars = WindowInsets.systemBars
    val ime = WindowInsets.ime

    val left = systemBars.getLeft(density, layoutDirection)
    val top = systemBars.getTop(density)
    val right = systemBars.getRight(density, layoutDirection)
    val bottom = systemBars.getBottom(density)
    val imeBottom = ime.getBottom(density)

    return remember(density.density, density.fontScale, layoutDirection, left, top, right, bottom, imeBottom) {
        with(density) {
            MullionInsets(
                leftDp = left.toDp().value.roundToInt(),
                topDp = top.toDp().value.roundToInt(),
                rightDp = right.toDp().value.roundToInt(),
                bottomDp = bottom.toDp().value.roundToInt(),
                imeDp = imeBottom.toDp().value.roundToInt(),
            )
        }
    }
}

@Composable
private fun rememberHingeInfo(activity: Activity?): HingeInfo {
    if (activity == null) return HingeInfo.None
    val density = LocalDensity.current

    val tracker = remember(activity) { WindowInfoTracker.getOrCreate(activity) }
    val layoutInfo: WindowLayoutInfo? by tracker.windowLayoutInfo(activity)
        .collectAsState(initial = null)

    return remember(layoutInfo, density.density) {
        val feature = layoutInfo?.displayFeatures
            ?.filterIsInstance<FoldingFeature>()
            ?.firstOrNull()
            ?: return@remember HingeInfo.None
        feature.toHingeInfo(density.density)
    }
}

/**
 * Converts a platform [FoldingFeature] to MULLION's dp geometry.
 *
 * Window 1.4 cannot distinguish TENT from HALF_OPENED; an app that knows the
 * posture (for example from a device-specific sensor) can override the value via
 * [MullionWindowProvider].
 */
private fun FoldingFeature.toHingeInfo(density: Float): HingeInfo {
    val posture = if (state == FoldingFeature.State.FLAT) {
        HingePosture.Flat
    } else {
        HingePosture.HalfOpened
    }
    if (posture == HingePosture.Flat) {
        return HingeInfo(posture = HingePosture.Flat)
    }

    val orientation = if (this.orientation == FoldingFeature.Orientation.VERTICAL) {
        HingeOrientation.Vertical
    } else if (this.orientation == FoldingFeature.Orientation.HORIZONTAL) {
        HingeOrientation.Horizontal
    } else {
        return HingeInfo(posture = posture)
    }

    val bounds: Rect = bounds
    val dpBounds = DpRect(
        left = bounds.left / density,
        top = bounds.top / density,
        right = bounds.right / density,
        bottom = bounds.bottom / density,
    )
    return HingeInfo(
        posture = posture,
        orientation = if (isSeparating) orientation else null,
        separatorWidthDp = if (orientation == HingeOrientation.Vertical) dpBounds.width else 0f,
        separatorHeightDp = if (orientation == HingeOrientation.Horizontal) dpBounds.height else 0f,
        bounds = dpBounds,
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
