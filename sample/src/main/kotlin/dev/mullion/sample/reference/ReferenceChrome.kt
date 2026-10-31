package dev.mullion.sample.reference

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.base.MullionAvatar
import dev.mullion.compose.base.MullionAvatarSize
import dev.mullion.compose.base.MullionBadge
import dev.mullion.compose.base.MullionBadgeKind
import dev.mullion.compose.base.MullionIconButton
import dev.mullion.compose.base.MullionProgressBar
import dev.mullion.compose.base.MullionButtonSize
import dev.mullion.compose.base.MullionButtonVariant
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.currentBreakpoint
import dev.mullion.sample.ui.BrandMark
import dev.mullion.sample.ui.Lab
import dev.mullion.sample.ui.SampleBody

/** Centers reference-app content at a readable engineering width. */
@Composable
fun ReferencePage(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.widthIn(max = 1180.dp).fillMaxSize()) {
            content()
        }
    }
}

@Composable
fun ReferenceTopBar(
    screen: RefScreen,
    state: ReferenceUiState,
    onScan: () -> Unit,
    onRefresh: () -> Unit,
    onProfile: () -> Unit,
) {
    val compact = currentBreakpoint().isCompact
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .glass(shape = CircleShape, level = GlassLevel.Floating)
            .padding(start = 14.dp, end = 10.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrandMark(size = 27.dp, intensity = 0.9f)
        Spacer(Modifier.width(9.dp))
        Column {
            Text(
                text = "MULLION CONTROL",
                style = MullionTextStyles.Mono.copy(
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                ),
                color = MullionColors.Ink,
            )
            Text(
                text = screen.label,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                color = MullionColors.Faint,
            )
        }
        Spacer(Modifier.weight(1f))
        if (!compact && (state.offlineCount > 0 || state.warningCount > 0)) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MullionColors.Signal.copy(alpha = 0.12f), CircleShape)
                    .border(1.dp, MullionColors.Signal.copy(alpha = 0.4f), CircleShape)
                    .padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Box(
                    Modifier
                        .size(6.dp)
                        .background(MullionColors.Signal, CircleShape),
                )
                Text(
                    text = "${state.alerts.size} 告警",
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                    color = MullionColors.Signal,
                )
            }
            Spacer(Modifier.width(6.dp))
        }
        MullionIconButton(
            onClick = onScan,
            variant = MullionButtonVariant.Tonal,
            size = MullionButtonSize.S,
            contentDescription = "扫描设备",
        ) {
            Text("⌁", style = MullionTextStyles.Mono.copy(fontSize = 17.sp), color = MullionColors.Volt)
        }
        if (!compact) {
            Spacer(Modifier.width(3.dp))
            MullionIconButton(
                onClick = onRefresh,
                variant = MullionButtonVariant.Tonal,
                size = MullionButtonSize.S,
                contentDescription = "刷新",
            ) {
                Text("↻", style = MullionTextStyles.Mono.copy(fontSize = 16.sp), color = MullionColors.Ink)
            }
        }
        Spacer(Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .clickable(onClick = onProfile),
        ) {
            MullionBadge(
                count = state.alerts.size,
                kind = if (state.alerts.isEmpty()) MullionBadgeKind.Dot else MullionBadgeKind.Number,
                contentDescription = "告警 ${state.alerts.size}",
            ) {
                MullionAvatar(
                    name = state.user.name,
                    size = MullionAvatarSize.M,
                    colorIndex = 0,
                )
            }
        }
        Spacer(Modifier.width(7.dp))
    }
}

@Composable
fun SectionHeading(
    title: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            if (kicker != null) {
                Lab(kicker, color = MullionColors.Volt, size = 9)
                Spacer(Modifier.height(3.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MullionColors.Ink,
            )
        }
        if (action != null) action()
    }
}

@Composable
fun StatusDot(status: DeviceStatus, size: Dp = 8.dp) {
    Box(
        Modifier
            .size(size)
            .background(statusColor(status), CircleShape),
    )
}

@Composable
fun StatusChip(status: DeviceStatus) {
    val color = statusColor(status)
    Text(
        text = status.label,
        style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
        color = color,
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f), CircleShape)
            .border(1.dp, color.copy(alpha = 0.42f), CircleShape)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}

@Composable
fun SeverityChip(severity: AlertSeverity) {
    val color = severityColor(severity)
    Text(
        text = severity.label,
        style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
        color = color,
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

fun statusColor(status: DeviceStatus) = when (status) {
    DeviceStatus.Online -> MullionColors.Mint
    DeviceStatus.Warning -> MullionColors.Amber
    DeviceStatus.Offline -> MullionColors.Faint
}

fun severityColor(severity: AlertSeverity) = when (severity) {
    AlertSeverity.Critical -> MullionColors.Signal
    AlertSeverity.Warning -> MullionColors.Amber
    AlertSeverity.Info -> MullionColors.Volt
}

fun activityColor(kind: ActivityKind) = when (kind) {
    ActivityKind.Online -> MullionColors.Mint
    ActivityKind.Offline -> MullionColors.Faint
    ActivityKind.Restart -> MullionColors.Volt
    ActivityKind.Firmware -> MullionColors.Amber
    ActivityKind.Backup -> MullionColors.VoltHigh
}

@Composable
fun MetricTile(
    label: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
) {
    Column(
        modifier = modifier
            .glass(shape = RoundedCornerShape(16.dp), level = GlassLevel.Panel)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Lab(label, size = 8, color = if (accent) MullionColors.Volt else MullionColors.Faint)
        Spacer(Modifier.height(7.dp))
        Text(
            text = value,
            style = MullionTextStyles.Mono.copy(
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = if (accent) MullionColors.Volt else MullionColors.Ink,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = caption,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
            color = MullionColors.Faint,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun InfoRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = key,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
            color = MullionColors.Faint,
            modifier = Modifier.width(96.dp),
        )
        Text(
            text = value,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp),
            color = MullionColors.Ink,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun MetricBar(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    unit: String = "%",
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Faint,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "$value$unit",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
                color = when {
                    value >= 80 -> MullionColors.Signal
                    value >= 65 -> MullionColors.Amber
                    else -> MullionColors.Volt
                },
            )
        }
        Spacer(Modifier.height(5.dp))
        MullionProgressBar(
            progress = value / 100f,
            label = null,
            showValue = false,
        )
    }
}

@Composable
fun DeviceGlyph(
    kind: DeviceKind,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = MullionColors.Volt,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.5.dp.toPx()
        when (kind) {
            DeviceKind.Gateway -> {
                drawRoundRect(
                    color = tint.copy(alpha = 0.9f),
                    topLeft = Offset(w * 0.2f, h * 0.32f),
                    size = Size(w * 0.6f, h * 0.46f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                    style = Stroke(width = stroke),
                )
                drawLine(tint, Offset(w * 0.36f, h * 0.3f), Offset(w * 0.36f, h * 0.18f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * 0.64f, h * 0.3f), Offset(w * 0.64f, h * 0.18f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawCircle(tint, radius = w * 0.035f, center = Offset(w * 0.5f, h * 0.56f))
            }

            DeviceKind.Sensor -> {
                drawCircle(tint, radius = w * 0.23f, style = Stroke(width = stroke))
                drawCircle(tint.copy(alpha = 0.5f), radius = w * 0.34f, style = Stroke(width = stroke))
                drawCircle(tint, radius = w * 0.05f)
            }

            DeviceKind.Camera -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.12f, h * 0.3f),
                    size = Size(w * 0.76f, h * 0.44f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                    style = Stroke(width = stroke),
                )
                drawCircle(tint, radius = w * 0.12f, style = Stroke(width = stroke))
            }

            DeviceKind.Meter -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.22f, h * 0.18f),
                    size = Size(w * 0.56f, h * 0.64f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                    style = Stroke(width = stroke),
                )
                val bolt = Path().apply {
                    moveTo(w * 0.53f, h * 0.28f)
                    lineTo(w * 0.4f, h * 0.52f)
                    lineTo(w * 0.53f, h * 0.52f)
                    lineTo(w * 0.46f, h * 0.72f)
                    lineTo(w * 0.64f, h * 0.46f)
                    lineTo(w * 0.51f, h * 0.46f)
                    close()
                }
                drawPath(bolt, color = tint)
            }
        }
    }
}

@Composable
fun Sparkline(
    values: List<Float>,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MullionColors.Volt,
) {
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val max = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f
        val min = values.minOrNull() ?: 0f
        val range = (max - min).coerceAtLeast(1f)
        val stepX = size.width / (values.size - 1)
        val path = Path()
        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - min) / range) * size.height * 0.82f - size.height * 0.09f
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
        drawLine(
            color = MullionColors.Edge.copy(alpha = 0.4f),
            start = Offset(0f, size.height * 0.9f),
            end = Offset(size.width, size.height * 0.9f),
            strokeWidth = 1f,
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 42.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(42.dp)
                .background(MullionColors.Volt.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, MullionColors.Volt.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("⌁", style = MullionTextStyles.Mono.copy(fontSize = 18.sp), color = MullionColors.Volt)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            style = MullionTextStyles.Mono.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
            color = MullionColors.Ink,
        )
        Spacer(Modifier.height(4.dp))
        SampleBody(body, size = 12, color = MullionColors.Muted)
    }
}

@Composable
fun ReferenceSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(20.dp), level = GlassLevel.Panel)
            .padding(18.dp),
        content = content,
    )
}

@Composable
fun DrawerHeader(state: ReferenceUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrandMark(size = 34.dp)
        Spacer(Modifier.width(9.dp))
        Column {
            Text(
                text = "MULLION",
                style = MullionTextStyles.Mono.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
                color = MullionColors.Ink,
            )
            Text(
                text = state.siteName,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                color = MullionColors.Faint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
