package dev.mullion.compose.base

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles

/**
 * A pill segmented control whose segment count is the caller's decision.
 *
 * MULLION does not hide segments for you: an app that wants 2 segments on a
 * phone and 5 on a tablet computes that list from the width class and passes it
 * in. This keeps the control honest and previewable at any width.
 */
@Composable
public fun MullionSegmentedControl(
    segments: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(CircleShape)
            .background(MullionColors.Tonal, CircleShape)
            .padding(4.dp)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        segments.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val textColor by animateColorAsState(
                targetValue = if (selected) MullionColors.OnVolt else MullionColors.Muted,
                animationSpec = tween(MullionMotion.StateMillis),
                label = "Segment.text",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .clip(CircleShape)
                    .then(
                        if (selected) {
                            Modifier.background(
                                Brush.verticalGradient(
                                    listOf(MullionColors.VoltHigh, MullionColors.Volt),
                                ),
                                CircleShape,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .selectable(
                        selected = selected,
                        enabled = enabled,
                        role = Role.RadioButton,
                        onClick = { onSelected(index) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MullionTextStyles.UISmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = if (selected) {
                            androidx.compose.ui.text.font.FontWeight.SemiBold
                        } else {
                            androidx.compose.ui.text.font.FontWeight.Normal
                        },
                    ),
                    color = textColor.copy(alpha = if (enabled) 1f else 0.45f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 6.dp),
                )
            }
        }
    }
}
