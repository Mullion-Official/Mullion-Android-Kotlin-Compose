package dev.mullion.compose.adaptive

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.PaneSlot
import dev.mullion.core.PaneStrategy

/**
 * List / detail / preview, resolved by a [PaneStrategy] and never by ad-hoc
 * width conditions.
 *
 * At one pane, selecting an item replaces the list with the detail; back returns
 * to the list. At two panes, list and detail are both always visible. At three,
 * the preview docks on the trailing edge. The transition between one-pane states
 * uses the shared X axis.
 *
 * @param showDetail only consulted at one pane. Wide layouts always show detail.
 * @param onBack invoked by the system back gesture while the detail covers the list.
 */
@Composable
public fun MultiPane(
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    preview: (@Composable () -> Unit)? = null,
    showDetail: Boolean = false,
    onBack: () -> Unit = {},
    strategy: PaneStrategy = PaneStrategy(),
) {
    val window = mullionWindow()
    val slots = buildList {
        add(PaneSlot.List)
        add(PaneSlot.Detail)
        if (preview != null) add(PaneSlot.Preview)
    }
    val plan = strategy.plan(window.bp, slots)

    if (plan.isSinglePane) {
        BackHandler(enabled = showDetail, onBack = onBack)
        SharedAxisTransition(
            targetState = showDetail,
            modifier = modifier.fillMaxSize(),
            axis = SharedAxis.X,
        ) { detailVisible ->
            if (detailVisible) detail() else list()
        }
        return
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val gutter = plan.gutterDp.dp
        val preferredListWidth = (maxWidth * 0.34f).coerceIn(280.dp, 380.dp)
        val preferredPreviewWidth = (maxWidth * 0.26f).coerceIn(240.dp, 320.dp)
        val minimumDetailWidth = 320.dp

        // A nav drawer or a fold leaf can make the content narrower than the
        // window class suggests. Never let the detail pane collapse into a
        // one-character column: drop the preview first, then clamp the list.
        val showPreview = preview != null &&
            plan.shows(PaneSlot.Preview) &&
            maxWidth >= preferredListWidth + preferredPreviewWidth + gutter * 2 + minimumDetailWidth
        val listWidth = if (showPreview) {
            preferredListWidth
        } else {
            preferredListWidth.coerceAtMost(
                (maxWidth - gutter - minimumDetailWidth).coerceAtLeast(200.dp),
            )
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Box(Modifier.width(listWidth).fillMaxHeight()) { list() }
            PaneDivider(gutter = gutter)
            Box(Modifier.weight(1f).fillMaxHeight()) { detail() }
            if (showPreview) {
                PaneDivider(gutter = gutter)
                Box(Modifier.width(preferredPreviewWidth).fillMaxHeight()) { preview() }
            }
        }
    }
}

/**
 * A 1dp rule floating in the gutter. It is decorative and never consumes
 * pointer input, so dragging still belongs to the adjacent pane.
 */
@Composable
internal fun PaneDivider(gutter: Dp) {
    Box(
        modifier = Modifier
            .width(gutter)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        val edge = MullionColors.Edge
        val edgeHigh = MullionColors.EdgeHigh
        Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            val x = size.width / 2f
            drawLine(
                brush = Brush.verticalGradient(
                    listOf(
                        edge.copy(alpha = 0f),
                        edgeHigh,
                        edge.copy(alpha = 0f),
                    ),
                ),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f,
            )
        }
    }
}
