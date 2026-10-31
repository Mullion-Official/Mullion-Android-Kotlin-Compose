package dev.mullion.compose.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles

/** One tab label, with an optional count badge. */
public data class MullionTab(
    public val label: String,
    public val badge: String? = null,
)

/**
 * Tabs that scroll when they must and collapse into an overflow menu when asked.
 *
 * The component never guesses the width class. The caller passes
 * `overflowAfter` (often derived from `currentBreakpoint()`) when it wants the
 * "fixed + ⋯" behavior. The selected tab is always kept in the visible group by
 * surfacing its label on the overflow trigger.
 *
 * @param overflowAfter number of tabs to keep visible before overflow. `null`
 *   disables overflow and scrolls instead.
 */
@Composable
public fun MullionTabs(
    tabs: List<MullionTab>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    overflowAfter: Int? = null,
) {
    require(tabs.isNotEmpty()) { "MullionTabs needs at least one tab" }
    val safeSelected = selectedIndex.coerceIn(0, tabs.lastIndex)
    val overflowEnabled = overflowAfter != null && overflowAfter in 0..<tabs.size
    val visible = if (overflowEnabled) tabs.take(overflowAfter!!) else tabs
    val overflow = if (overflowEnabled) tabs.drop(overflowAfter!!) else emptyList()
    var menuOpen by remember { mutableStateOf(false) }
    val edge = MullionColors.Edge

    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = edge,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1f,
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            visible.forEachIndexed { index, tab ->
                TabItem(
                    tab = tab,
                    selected = index == safeSelected,
                    onClick = { onSelected(index) },
                )
            }
            if (overflowEnabled) {
                val selectedOverflow = safeSelected >= (overflowAfter ?: 0)
                Box {
                    TabItem(
                        tab = MullionTab(
                            label = if (selectedOverflow) {
                                tabs[safeSelected].label
                            } else {
                                "⋯"
                            },
                        ),
                        selected = selectedOverflow,
                        onClick = { menuOpen = true },
                    )
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                    ) {
                        overflow.forEachIndexed { index, tab ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = tab.label,
                                        style = MullionTextStyles.UISmall.copy(fontSize = 11.sp),
                                    )
                                },
                                onClick = {
                                    menuOpen = false
                                    onSelected((overflowAfter ?: 0) + index)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: MullionTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val indicator by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(MullionMotion.StateMillis),
        label = "Tab.indicator",
    )
    Box(
        modifier = Modifier
            .height(38.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = tab.label,
                style = MullionTextStyles.UISmall.copy(fontSize = 11.sp),
                color = if (selected) MullionColors.Volt else MullionColors.Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (tab.badge != null) {
                Text(
                    text = tab.badge,
                    style = MullionTextStyles.UISmall.copy(fontSize = 8.sp),
                    color = MullionColors.Amber,
                    modifier = Modifier.padding(start = 5.dp),
                )
            }
        }
        if (indicator > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.82f)
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                MullionColors.VoltHigh.copy(alpha = indicator),
                                MullionColors.Volt.copy(alpha = indicator),
                            ),
                        ),
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
        }
    }
}
