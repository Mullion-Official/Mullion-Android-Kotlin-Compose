package dev.mullion.sample.reference

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.base.MullionAvatar
import dev.mullion.compose.base.MullionAvatarSize
import dev.mullion.compose.base.MullionButton
import dev.mullion.compose.base.MullionButtonSize
import dev.mullion.compose.base.MullionButtonVariant
import dev.mullion.compose.base.MullionCard
import dev.mullion.compose.base.MullionProgressBar
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.currentBreakpoint
import dev.mullion.sample.ui.Lab
import dev.mullion.sample.ui.SampleBody
import dev.mullion.sample.ui.SampleChip

@Composable
fun WorkbenchScreen(
    state: ReferenceUiState,
    onOpenDevice: (String) -> Unit,
    onOpenDevices: () -> Unit,
    onScan: () -> Unit,
    onRefresh: () -> Unit,
) {
    val compact = currentBreakpoint().isCompact
    ReferencePage {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Lab("SHIFT A · ${state.siteName}", color = MullionColors.Volt, size = 9)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "早上好，${state.user.name.substringBefore(' ')}",
                            style = MullionTextStyles.Mono.copy(
                                fontSize = if (compact) 24.sp else 30.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.4).sp,
                            ),
                            color = MullionColors.Ink,
                        )
                        Spacer(Modifier.height(4.dp))
                        SampleBody(
                            "5 台设备接入 · 今天已处理 ${state.alerts.size} 条需要关注的事项。",
                            size = 12,
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    MullionAvatar(name = state.user.name, size = MullionAvatarSize.L, colorIndex = 0)
                }
                if (state.refreshing) {
                    Spacer(Modifier.height(10.dp))
                    MullionProgressBar(progress = null)
                }
            }

            item {
                if (compact) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricTile(
                                label = "在线设备",
                                value = "${state.onlineCount}/${state.devices.size}",
                                caption = "边缘网关 + 传感器",
                                modifier = Modifier.weight(1f),
                                accent = true,
                            )
                            MetricTile(
                                label = "待处理",
                                value = state.alerts.size.toString(),
                                caption = "告警与离线",
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricTile(
                                label = "可用率",
                                value = "99.97%",
                                caption = "过去 24 小时",
                                modifier = Modifier.weight(1f),
                            )
                            MetricTile(
                                label = "平均信号",
                                value = "82%",
                                caption = "5 台设备",
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricTile("在线设备", "${state.onlineCount}/${state.devices.size}", "边缘网关 + 传感器", Modifier.weight(1f), accent = true)
                        MetricTile("待处理", state.alerts.size.toString(), "告警与离线", Modifier.weight(1f))
                        MetricTile("可用率", "99.97%", "过去 24 小时", Modifier.weight(1f))
                        MetricTile("平均信号", "82%", "5 台设备", Modifier.weight(1f))
                    }
                }
            }

            item {
                SectionHeading(
                    title = "需要关注",
                    kicker = "ATTENTION",
                    action = {
                        MullionButton(
                            text = "全部设备",
                            onClick = onOpenDevices,
                            variant = MullionButtonVariant.Text,
                            size = MullionButtonSize.S,
                        )
                    },
                )
            }

            if (state.alerts.isEmpty()) {
                item {
                    ReferenceSection {
                        EmptyState("当前没有告警", "所有设备运行正常。可以刷新一次，确认最新状态。")
                    }
                }
            } else {
                items(state.alerts.take(3), key = { it.id }) { alert ->
                    AlertRow(
                        alert = alert,
                        onOpen = { onOpenDevice(alert.deviceId) },
                    )
                }
            }

            item {
                SectionHeading(title = "快捷操作", kicker = "QUICK ACTIONS")
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickAction(
                        title = "扫描设备",
                        caption = "相机 / 机身码",
                        onClick = onScan,
                        modifier = Modifier.weight(1f),
                    )
                    QuickAction(
                        title = "同步状态",
                        caption = "所有在线设备",
                        onClick = onRefresh,
                        modifier = Modifier.weight(1f),
                    )
                    QuickAction(
                        title = "全部设备",
                        caption = "搜索 / 筛选",
                        onClick = onOpenDevices,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                SectionHeading(
                    title = "最近动态",
                    kicker = "ACTIVITY",
                    action = {
                        SampleChip("自动刷新 · 30s")
                    },
                )
            }

            items(state.events.take(5), key = { it.id }) { event ->
                ActivityRow(
                    event = event,
                    onClick = event.deviceId?.let { id -> { onOpenDevice(id) } },
                )
            }

            item {
                ReferenceSection {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DeviceGlyph(DeviceKind.Gateway, Modifier.size(28.dp), MullionColors.Volt)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "北侧边缘网关运行正常",
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                                color = MullionColors.Ink,
                            )
                            Text(
                                text = "数据本地缓存 24h · 断网自动接管",
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                                color = MullionColors.Faint,
                            )
                        }
                        Text(
                            text = "LIVE",
                            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                            color = MullionColors.Mint,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertRow(
    alert: DeviceAlert,
    onOpen: () -> Unit,
) {
    val color = severityColor(alert.severity)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(16.dp), level = GlassLevel.Panel)
            .clickable(onClick = onOpen)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(color.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(8.dp).background(color, CircleShape))
        }
        Spacer(Modifier.width(11.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = alert.title,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold),
                    color = MullionColors.Ink,
                )
                Spacer(Modifier.width(8.dp))
                SeverityChip(alert.severity)
            }
            Text(
                text = alert.detail,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Muted,
            )
            Text(
                text = alert.timeAgo,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                color = MullionColors.Faint,
            )
        }
        Text(
            text = "查看 →",
            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
            color = MullionColors.Volt,
        )
    }
}

@Composable
private fun QuickAction(
    title: String,
    caption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MullionCard(
        modifier = modifier,
        onClick = onClick,
        contentPadding = PaddingValues(13.dp),
    ) {
        Text(
            text = title,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold),
            color = MullionColors.Ink,
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = caption,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
            color = MullionColors.Faint,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "→",
            style = MullionTextStyles.Mono.copy(fontSize = 14.sp),
            color = MullionColors.Volt,
        )
    }
}

@Composable
fun ActivityRow(
    event: ActivityEvent,
    onClick: (() -> Unit)?,
) {
    val color = activityColor(event.kind)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MullionColors.Ink.copy(alpha = 0.035f), RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape),
        )
        Spacer(Modifier.width(11.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = MullionColors.Ink,
            )
            Text(
                text = event.detail,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                color = MullionColors.Muted,
                maxLines = 2,
            )
        }
        Text(
            text = formatEventTime(event.timestamp),
            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
            color = MullionColors.Faint,
        )
    }
}
