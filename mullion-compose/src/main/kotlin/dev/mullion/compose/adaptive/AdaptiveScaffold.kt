package dev.mullion.compose.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.NavigationMode

/**
 * The outer shell. It decides *where* navigation lives, never *what* it is.
 *
 * The three morphologies share one content call site, so state inside [content]
 * survives a width change from phone to tablet. Callers write no width checks.
 *
 * @param mode optional override for previews; defaults to the window's mode.
 * @param drawerOpen whether the wide drawer is expanded. When `false`, [rail]
 *   is shown instead, so a device can collapse navigation without leaving the
 *   wide class.
 * @param topBar slot drawn above the content in every mode.
 * @param bottomBar slot used in [NavigationMode.BottomBar].
 * @param rail slot used in [NavigationMode.Rail] and collapsed drawers.
 * @param drawer slot used in [NavigationMode.Drawer].
 * @param content the only content call site; receives zero padding today, and
 *   is kept for API symmetry with Material scaffolds.
 */
@Composable
public fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    mode: NavigationMode? = null,
    drawerOpen: Boolean = true,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    rail: @Composable () -> Unit = {},
    drawer: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val resolved = mode ?: mullionWindow().navigationMode
    val showSide = resolved == NavigationMode.Rail || resolved == NavigationMode.Drawer

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MullionColors.Background),
    ) {
        if (showSide) {
            Box(Modifier.fillMaxHeight()) {
                if (resolved == NavigationMode.Drawer && drawerOpen) drawer() else rail()
            }
        }

        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            topBar()
            Box(modifier = Modifier.weight(1f)) {
                content(PaddingValues(0.dp))
            }
            if (resolved == NavigationMode.BottomBar) {
                bottomBar()
            }
        }
    }
}
