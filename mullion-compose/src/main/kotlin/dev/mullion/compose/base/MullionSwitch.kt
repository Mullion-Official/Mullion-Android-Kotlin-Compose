package dev.mullion.compose.base

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.LocalMullionDimens
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionDimens
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles

/**
 * A 52×30 switch with a 22dp thumb that travels 22dp.
 *
 * The row is at least 48dp tall so it can be toggled comfortably even when the
 * visual track is small. The thumb's motion uses a spring because a switch is a
 * physical promise; a linear slide feels broken.
 */
@Composable
public fun MullionSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) MullionColors.Volt else MullionColors.Ink.copy(alpha = 0.13f),
        animationSpec = androidx.compose.animation.core.tween(MullionMotion.StateMillis),
        label = "Switch.track",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "Switch.thumb",
    )

    Row(
        modifier = modifier
            .heightIn(min = LocalMullionDimens.current.touchTarget)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MullionTextStyles.UISmall.copy(fontSize = 11.sp),
                color = if (enabled) MullionColors.Muted else MullionColors.Faint.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(12.dp))
        }

        Box(
            modifier = Modifier
                .width(52.dp)
                .height(30.dp)
                .clip(CircleShape)
                .background(
                    brush = if (checked) {
                        Brush.verticalGradient(
                            listOf(MullionColors.VoltHigh, MullionColors.Volt),
                        )
                    } else {
                        Brush.verticalGradient(listOf(trackColor, trackColor))
                    },
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = if (checked) MullionColors.Transparent else MullionColors.Edge,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 3.dp)
                    .offset(x = thumbOffset)
                    .size(22.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        clip = false,
                    )
                    .background(Color.White, CircleShape)
                    .border(1.dp, MullionColors.EdgeHigh, CircleShape),
            )
        }
    }
}
