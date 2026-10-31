package dev.mullion.compose.adaptive

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionFonts
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.NavigationMode

/** One destination in an [OrbitNav]. Routes stay in the caller's domain. */
public data class OrbitNavItem(
    public val label: String,
    public val badge: String? = null,
    public val enabled: Boolean = true,
)

/**
 * One navigation data source, three morphologies.
 *
 * The selection index is the same in every mode; switching width never loses
 * which destination is active. Screens should declare [items] once and let the
 * window decide whether they render as a bottom bar, a rail or a drawer.
 *
 * @param mode optional override used by previews and the sample cabinet.
 * @param icon optional leading glyph. When omitted, the first character of the
 *   label is used as an orbit marker.
 */
@Composable
public fun OrbitNav(
    items: List<OrbitNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    mode: NavigationMode? = null,
    header: (@Composable () -> Unit)? = null,
    icon: (@Composable (index: Int, selected: Boolean) -> Unit)? = null,
) {
    val resolved = mode ?: mullionWindow().navigationMode
    AnimatedContent(
        targetState = resolved,
        modifier = modifier,
        transitionSpec = {
            fadeIn(tween(MullionMotion.PaneMillis, easing = MullionMotion.NavMorph)) togetherWith
                fadeOut(tween(MullionMotion.StateMillis))
        },
        label = "OrbitNav.morph",
    ) { navMode ->
        when (navMode) {
            NavigationMode.BottomBar -> BottomBar(
                items = items,
                selectedIndex = selectedIndex,
                onSelect = onSelect,
                icon = icon,
            )

            NavigationMode.Rail -> Rail(
                items = items,
                selectedIndex = selectedIndex,
                onSelect = onSelect,
                icon = icon,
            )

            NavigationMode.Drawer -> Drawer(
                items = items,
                selectedIndex = selectedIndex,
                onSelect = onSelect,
                header = header,
                icon = icon,
            )
        }
    }
}

@Composable
private fun BottomBar(
    items: List<OrbitNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    icon: (@Composable (index: Int, selected: Boolean) -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(24.dp), level = GlassLevel.Floating)
            .padding(6.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .background(
                        color = if (selected) MullionColors.Volt.copy(alpha = 0.16f) else Color.Transparent,
                        shape = RoundedCornerShape(18.dp),
                    )
                    .border(
                        width = if (selected) 1.dp else 0.dp,
                        color = if (selected) MullionColors.Volt.copy(alpha = 0.75f) else Color.Transparent,
                        shape = RoundedCornerShape(18.dp),
                    )
                    .selectable(
                        selected = selected,
                        enabled = item.enabled,
                        role = Role.Tab,
                        onClick = { onSelect(index) },
                    )
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (icon != null) {
                    icon(index, selected)
                    Spacer(Modifier.height(2.dp))
                } else {
                    OrbitMark(
                        label = item.label,
                        selected = selected,
                        size = 18.dp,
                    )
                    Spacer(Modifier.height(3.dp))
                }
                Text(
                    text = item.label,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
                    color = if (selected) MullionColors.Ink else MullionColors.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun Rail(
    items: List<OrbitNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    icon: (@Composable (index: Int, selected: Boolean) -> Unit)?,
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .glass(shape = RoundedCornerShape(0.dp), level = GlassLevel.Panel)
            .padding(horizontal = 9.dp, vertical = 12.dp)
            .selectableGroup(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(
                        color = if (selected) MullionColors.Volt.copy(alpha = 0.16f) else Color.Transparent,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .border(
                        width = if (selected) 1.dp else 0.dp,
                        color = if (selected) MullionColors.Volt.copy(alpha = 0.75f) else Color.Transparent,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .selectable(
                        selected = selected,
                        enabled = item.enabled,
                        role = Role.Tab,
                        onClick = { onSelect(index) },
                    )
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (icon != null) {
                    icon(index, selected)
                } else {
                    OrbitMark(label = item.label, selected = selected, size = 22.dp)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.label,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                    color = if (selected) MullionColors.Volt else MullionColors.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun Drawer(
    items: List<OrbitNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    header: (@Composable () -> Unit)?,
    icon: (@Composable (index: Int, selected: Boolean) -> Unit)?,
) {
    Column(
        modifier = Modifier
            .width(216.dp)
            .glass(shape = RoundedCornerShape(0.dp), level = GlassLevel.Panel)
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (header != null) {
            header()
            Spacer(Modifier.height(12.dp))
        }
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(
                        color = if (selected) MullionColors.Volt.copy(alpha = 0.16f) else Color.Transparent,
                        shape = CircleShape,
                    )
                    .selectable(
                        selected = selected,
                        enabled = item.enabled,
                        role = Role.Tab,
                        onClick = { onSelect(index) },
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (icon != null) {
                    icon(index, selected)
                } else {
                    OrbitMark(label = item.label, selected = selected, size = 18.dp)
                }
                Text(
                    text = item.label,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 11.sp),
                    color = if (selected) MullionColors.Volt else MullionColors.Muted,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.badge != null) {
                    Text(
                        text = item.badge,
                        style = MullionTextStyles.MonoSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MullionColors.OnVolt,
                        modifier = Modifier
                            .background(MullionColors.Volt, CircleShape)
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun OrbitMark(
    label: String,
    selected: Boolean,
    size: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(
                        if (selected) MullionColors.VoltHigh else MullionColors.Muted.copy(alpha = 0.5f),
                        if (selected) MullionColors.Volt else MullionColors.Faint,
                    ),
                ),
                shape = CircleShape,
            )
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.take(1).uppercase(),
            style = MullionTextStyles.MonoSmall.copy(
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MullionFonts.Mono,
            ),
            color = if (selected) MullionColors.OnVolt else MullionColors.Background,
        )
    }
}
