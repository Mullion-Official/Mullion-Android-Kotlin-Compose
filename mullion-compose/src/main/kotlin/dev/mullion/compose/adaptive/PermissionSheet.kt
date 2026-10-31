package dev.mullion.compose.adaptive

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.mullionWindow

/**
 * A permission request presented as a row of capabilities.
 *
 * The sheet never asks for permissions itself. The host owns the platform
 * request; this component only gives the request a shape that changes with the
 * window: a bottom sheet on compact widths, a trigger-anchored panel on wider
 * ones. The focus target and back behavior are the same in both forms.
 */
public data class MullionPermission(
    /** Human-readable capability, for example "相机". */
    public val label: String,
    /** The platform permission name, shown as a monospace hint. */
    public val permission: String,
    /** Optional explanation rendered under the permission. */
    public val rationale: String? = null,
    /** Required permissions cannot be skipped, only denied as a group. */
    public val required: Boolean = false,
)

/** Where a [PermissionSheet] is anchored. */
public enum class PermissionAnchor {
    /** Bottom on compact widths, trigger-anchored otherwise. */
    Auto,

    /** Always a bottom sheet. */
    Bottom,

    /** Always an anchored panel. */
    Trigger,
}

/**
 * The permission panel.
 *
 * @param anchorOffset where an anchored panel starts, relative to its parent.
 *   Pass the trigger's position if the trigger lives in the same `Box`.
 */
@Composable
public fun PermissionSheet(
    visible: Boolean,
    permissions: List<MullionPermission>,
    onDismissRequest: () -> Unit,
    onGrantAll: () -> Unit,
    onDeny: () -> Unit,
    modifier: Modifier = Modifier,
    anchor: PermissionAnchor = PermissionAnchor.Auto,
    title: String = "权限清单",
    confirmLabel: String = "全部允许",
    dismissLabel: String = "仅本次",
    anchorOffset: DpOffset = DpOffset(24.dp, 72.dp),
) {
    val window = mullionWindow()
    val bottom: Boolean = when (anchor) {
        PermissionAnchor.Auto -> window.bp.isCompact
        PermissionAnchor.Bottom -> true
        PermissionAnchor.Trigger -> false
    }

    BackHandler(enabled = visible, onBack = onDismissRequest)
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(visible) {
        if (visible) focusRequester.requestFocus()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            modifier = if (bottom) {
                Modifier.align(Alignment.BottomCenter)
            } else {
                Modifier
                    .align(Alignment.TopStart)
                    .offset(x = anchorOffset.x, y = anchorOffset.y)
            },
            enter = if (bottom) {
                slideInVertically(
                    animationSpec = tween(MullionMotion.SheetMillis, easing = MullionMotion.Settle),
                    initialOffsetY = { it },
                ) + fadeIn(tween(MullionMotion.StateMillis))
            } else {
                scaleIn(
                    animationSpec = tween(MullionMotion.SheetMillis, easing = MullionMotion.Settle),
                    initialScale = 0.95f,
                ) + fadeIn(tween(MullionMotion.StateMillis))
            },
            exit = if (bottom) {
                slideOutVertically(
                    animationSpec = tween(MullionMotion.StateMillis),
                    targetOffsetY = { it },
                ) + fadeOut(tween(MullionMotion.StateMillis))
            } else {
                fadeOut(tween(MullionMotion.StateMillis))
            },
        ) {
            Column(
                modifier = Modifier
                    .then(
                        if (bottom) {
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        } else {
                            Modifier
                                .width(300.dp)
                                .padding(horizontal = 16.dp)
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
                    .semantics { paneTitle = title }
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title.uppercase(),
                        style = MullionTextStyles.Label,
                        color = MullionColors.Volt,
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MullionColors.Tonal, CircleShape)
                            .clickable(onClick = onDismissRequest),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "×",
                            style = MullionTextStyles.Mono,
                            color = MullionColors.Muted,
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                permissions.forEach { permission ->
                    PermissionRow(permission)
                }

                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SheetActionButton(
                        label = confirmLabel,
                        primary = true,
                        onClick = onGrantAll,
                    )
                    SheetActionButton(
                        label = dismissLabel,
                        primary = false,
                        onClick = onDeny,
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(permission: MullionPermission) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics(mergeDescendants = true) {
                paneTitle = permission.label
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .border(1.dp, MullionColors.Volt, CircleShape)
                .background(MullionColors.Volt.copy(alpha = 0.18f), CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = permission.label,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 11.sp),
                    color = MullionColors.Ink,
                )
                if (permission.required) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "REQUIRED",
                        style = MullionTextStyles.LabelSmall.copy(fontSize = 8.sp),
                        color = MullionColors.Amber,
                    )
                }
            }
            Text(
                text = permission.permission,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Faint,
            )
            if (permission.rationale != null) {
                Text(
                    text = permission.rationale,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                    color = MullionColors.Muted,
                )
            }
        }
    }
}

@Composable
private fun SheetActionButton(
    label: String,
    primary: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.heightIn(min = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .height(34.dp)
                .background(
                    brush = if (primary) {
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(MullionColors.VoltHigh, MullionColors.Volt),
                        )
                    } else {
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(MullionColors.Tonal, MullionColors.Tonal),
                        )
                    },
                    shape = CircleShape,
                )
                .then(
                    if (primary) Modifier else Modifier.border(1.dp, MullionColors.Edge, CircleShape),
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp),
                color = if (primary) MullionColors.OnVolt else MullionColors.Ink,
            )
        }
    }
}
