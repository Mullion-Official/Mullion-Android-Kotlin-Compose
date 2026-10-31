package dev.mullion.sample.reference

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.adaptive.DockSide
import dev.mullion.compose.adaptive.MullionSearchBar
import dev.mullion.compose.adaptive.DockedSheet
import dev.mullion.compose.adaptive.MultiPane
import dev.mullion.compose.adaptive.SharedAxis
import dev.mullion.compose.adaptive.SharedAxisTransition
import dev.mullion.compose.base.MullionButton
import dev.mullion.compose.base.MullionButtonSize
import dev.mullion.compose.base.MullionButtonVariant
import dev.mullion.compose.base.MullionRadio
import dev.mullion.compose.base.MullionSegmentedControl
import dev.mullion.compose.base.MullionTab
import dev.mullion.compose.base.MullionTabs
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.adaptive.SizeClassText
import dev.mullion.compose.window.currentBreakpoint
import dev.mullion.compose.window.currentHinge
import dev.mullion.core.PaneStrategy
import dev.mullion.sample.ui.SampleBody
import dev.mullion.sample.ui.SampleChip

@Composable
fun DevicesScreen(
    state: ReferenceUiState,
    onSelectDevice: (String) -> Unit,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onFilterChange: (DeviceFilter) -> Unit,
    onTabChange: (DeviceTab) -> Unit,
    onRestart: (String) -> Unit,
    onScan: () -> Unit,
    onOpenFilter: () -> Unit,
    onRefresh: () -> Unit,
) {
    val bp = currentBreakpoint()
    val strategy = remember(state.paneGutterDp) {
        PaneStrategy(gutterDp = state.paneGutterDp)
    }
    val selected = state.selectedDevice

    BackHandler(enabled = !bp.isExpanded && selected != null) {
        onBack()
    }

    LaunchedEffect(bp.isExpanded, state.filteredDevices, selected?.id) {
        if (bp.isExpanded && selected == null && state.filteredDevices.isNotEmpty()) {
            onSelectDevice(state.filteredDevices.first().id)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    RefLabel("设备总览 · ${state.devices.size} 台", color = MullionColors.Volt, size = 9)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "设备",
                        style = MullionTextStyles.UI.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        color = MullionColors.Ink,
                    )
                }
                MullionButton(
                    text = "筛选",
                    onClick = onOpenFilter,
                    variant = MullionButtonVariant.Tonal,
                    size = MullionButtonSize.S,
                )
                Spacer(Modifier.width(6.dp))
                MullionButton(
                    text = "刷新",
                    onClick = onRefresh,
                    variant = MullionButtonVariant.Outlined,
                    size = MullionButtonSize.S,
                    loading = state.refreshing,
                )
            }
            Spacer(Modifier.height(10.dp))
            MullionSearchBar(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = "搜索名称 / 型号 / 序列号 / 位置…",
            )
            Spacer(Modifier.height(9.dp))
            MullionSegmentedControl(
                segments = DeviceFilter.entries.map { it.label },
                selectedIndex = state.filter.ordinal,
                onSelected = { onFilterChange(DeviceFilter.entries[it]) },
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${state.filteredDevices.size} 台匹配 · ${state.filter.label}",
                    style = MullionTextStyles.UISmall.copy(fontSize = 8.5.sp),
                    color = MullionColors.Faint,
                )
                Spacer(Modifier.weight(1f))
                if (state.refreshing) {
                    Text(
                        text = "同步中…",
                        style = MullionTextStyles.UISmall.copy(fontSize = 8.5.sp),
                        color = MullionColors.Volt,
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            MultiPane(
                modifier = Modifier.fillMaxSize(),
                strategy = strategy,
                list = {
                    DeviceList(
                        devices = state.filteredDevices,
                        selectedId = selected?.id,
                        onSelect = onSelectDevice,
                    )
                },
                detail = {
                    if (selected != null) {
                        DeviceDetailPanel(
                            device = selected,
                            state = state,
                            onBack = onBack,
                            onTabChange = onTabChange,
                            onRestart = { onRestart(selected.id) },
                            onScan = onScan,
                        )
                    } else {
                        EmptyState(
                            title = "选择一台设备",
                            body = "在宽窗口下，列表与详情会并排显示；在折叠屏上，详情会避开铰链。",
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                },
                preview = {
                    if (selected != null) {
                        DevicePreviewPanel(
                            device = selected,
                            onOpen = { onSelectDevice(selected.id) },
                        )
                    }
                },
                showDetail = selected != null,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun DeviceList(
    devices: List<Device>,
    selectedId: String?,
    onSelect: (String) -> Unit,
) {
    if (devices.isEmpty()) {
        EmptyState("没有匹配的设备", "尝试清空搜索词，或切换到“全部”。")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        items(devices, key = { it.id }) { device ->
            DeviceRow(
                device = device,
                selected = device.id == selectedId,
                onClick = { onSelect(device.id) },
            )
        }
    }
}

@Composable
private fun DeviceRow(
    device: Device,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint = statusColor(device.status)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) MullionColors.Volt.copy(alpha = 0.1f)
                else MullionColors.Ink.copy(alpha = 0.04f),
                RoundedCornerShape(14.dp),
            )
            .border(
                1.dp,
                if (selected) MullionColors.Volt.copy(alpha = 0.58f) else MullionColors.Edge.copy(alpha = 0.6f),
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(tint.copy(alpha = 0.1f), CircleShape)
                    .border(1.dp, tint.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                DeviceGlyph(device.kind, Modifier.size(21.dp), tint)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = MullionTextStyles.UISmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold),
                    color = MullionColors.Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${device.model} · ${device.location}",
                    style = MullionTextStyles.UISmall.copy(fontSize = 8.sp),
                    color = MullionColors.Faint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            StatusChip(device.status)
        }
        Spacer(Modifier.height(7.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "CPU ${device.metrics.cpuPercent}%",
                style = MullionTextStyles.UISmall.copy(fontSize = 8.sp),
                color = MullionColors.Faint,
            )
            Text(
                text = "MEM ${device.metrics.memoryPercent}%",
                style = MullionTextStyles.UISmall.copy(fontSize = 8.sp),
                color = MullionColors.Faint,
            )
            Text(
                text = "SIG ${device.metrics.signalPercent}%",
                style = MullionTextStyles.UISmall.copy(fontSize = 8.sp),
                color = if (device.metrics.signalPercent < 55) MullionColors.Amber else MullionColors.Faint,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = device.lastSeen,
                style = MullionTextStyles.UISmall.copy(fontSize = 8.sp),
                color = MullionColors.Faint,
            )
        }
    }
}

@Composable
private fun DeviceDetailPanel(
    device: Device,
    state: ReferenceUiState,
    onBack: () -> Unit,
    onTabChange: (DeviceTab) -> Unit,
    onRestart: () -> Unit,
    onScan: () -> Unit,
) {
    val compact = currentBreakpoint().isCompact
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            if (compact) {
                SampleChip("← 返回设备列表", onClick = onBack)
                Spacer(Modifier.height(8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(statusColor(device.status).copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, statusColor(device.status).copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    DeviceGlyph(device.kind, Modifier.size(27.dp), statusColor(device.status))
                }
                Spacer(Modifier.width(11.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.name,
                        style = MullionTextStyles.UI.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                        color = MullionColors.Ink,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${device.model} · ${device.serial}",
                        style = MullionTextStyles.UISmall.copy(fontSize = 8.5.sp),
                        color = MullionColors.Faint,
                    )
                }
                StatusChip(device.status)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MullionButton(
                    text = "重启设备",
                    onClick = onRestart,
                    variant = MullionButtonVariant.Outlined,
                    size = MullionButtonSize.S,
                    loading = state.restartingId == device.id,
                    enabled = device.status != DeviceStatus.Offline,
                )
                MullionButton(
                    text = "扫描机身码",
                    onClick = onScan,
                    variant = MullionButtonVariant.Tonal,
                    size = MullionButtonSize.S,
                )
            }
        }

        item {
            MullionTabs(
                tabs = DeviceTab.entries.map { MullionTab(it.label) },
                selectedIndex = state.tab.ordinal,
                onSelected = { onTabChange(DeviceTab.entries[it]) },
            )
        }

        item {
            SharedAxisTransition(
                targetState = state.tab,
                axis = SharedAxis.Y,
                modifier = Modifier.fillMaxWidth(),
            ) { tab ->
                when (tab) {
                    DeviceTab.Overview -> OverviewTab(device)
                    DeviceTab.Metrics -> MetricsTab(device)
                    DeviceTab.Logs -> LogsTab(device)
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(device: Device) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ReferenceSection {
            RefLabel("设备说明", size = 8)
            Spacer(Modifier.height(7.dp))
            SizeClassText(
                text = device.description,
                color = MullionColors.Muted,
                maxMeasureCh = 58,
            )
        }
        ReferenceSection {
            RefLabel("设备信息", size = 8)
            Spacer(Modifier.height(5.dp))
            InfoRow("位置", device.location)
            InfoRow("固件", device.firmware)
            InfoRow("运行时长", device.uptime)
            InfoRow("序列号", device.serial)
            InfoRow("最后心跳", device.lastSeen)
        }
        ReferenceSection {
            RefLabel("实时负载", size = 8)
            Spacer(Modifier.height(10.dp))
            MetricBar("CPU", device.metrics.cpuPercent)
            Spacer(Modifier.height(10.dp))
            MetricBar("内存", device.metrics.memoryPercent)
            Spacer(Modifier.height(10.dp))
            MetricBar("信号", device.metrics.signalPercent)
        }
    }
}

@Composable
private fun MetricsTab(device: Device) {
    val seed = device.metrics.cpuPercent
    val values = remember(seed) {
        List(24) { index ->
            0.35f + ((seed + index * 13) % 43) / 100f + index * 0.004f
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricTile("CPU", "${device.metrics.cpuPercent}%", "平均 34%", Modifier.weight(1f), accent = true)
            MetricTile("内存", "${device.metrics.memoryPercent}%", "峰值 68%", Modifier.weight(1f))
        }
        ReferenceSection {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RefLabel("24H CPU", size = 8)
                Spacer(Modifier.weight(1f))
                Text(
                    text = "样本 / 小时",
                    style = MullionTextStyles.UISmall.copy(fontSize = 8.sp),
                    color = MullionColors.Faint,
                )
            }
            Spacer(Modifier.height(10.dp))
            Sparkline(values = values, modifier = Modifier.fillMaxWidth().height(92.dp))
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "温度 ${device.metrics.temperatureC}°C",
                    style = MullionTextStyles.UISmall.copy(fontSize = 8.5.sp),
                    color = if (device.metrics.temperatureC >= 40f) MullionColors.Signal else MullionColors.Faint,
                )
                Text(
                    text = "信号 ${device.metrics.signalPercent}%",
                    style = MullionTextStyles.UISmall.copy(fontSize = 8.5.sp),
                    color = MullionColors.Faint,
                )
            }
        }
        ReferenceSection {
            RefLabel("采样设置", size = 8)
            Spacer(Modifier.height(6.dp))
            InfoRow("采样间隔", "10 秒")
            InfoRow("缓存窗口", "24 小时")
            InfoRow("上行协议", "MQTT / TLS")
        }
    }
}

@Composable
private fun LogsTab(device: Device) {
    ReferenceSection {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RefLabel("设备日志", size = 8)
            Spacer(Modifier.weight(1f))
            SampleChip("实时", selected = true)
        }
        Spacer(Modifier.height(8.dp))
        device.logs.forEachIndexed { index, log ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = log.time,
                    style = MullionTextStyles.UISmall.copy(fontSize = 8.5.sp),
                    color = MullionColors.Faint,
                    modifier = Modifier.width(42.dp),
                )
                Text(
                    text = log.level,
                    style = MullionTextStyles.UISmall.copy(fontSize = 8.sp),
                    color = when (log.level) {
                        "ERROR" -> MullionColors.Signal
                        "WARN" -> MullionColors.Amber
                        else -> MullionColors.Mint
                    },
                    modifier = Modifier.width(44.dp),
                )
                Text(
                    text = log.message,
                    style = MullionTextStyles.UISmall.copy(fontSize = 9.sp),
                    color = MullionColors.Muted,
                    modifier = Modifier.weight(1f),
                )
            }
            if (index != device.logs.lastIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MullionColors.Edge.copy(alpha = 0.4f)),
                )
            }
        }
    }
}

@Composable
private fun DevicePreviewPanel(device: Device, onOpen: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            ReferenceSection {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DeviceGlyph(device.kind, Modifier.size(34.dp), statusColor(device.status))
                    Spacer(Modifier.width(9.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = device.name,
                            style = MullionTextStyles.UI.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            color = MullionColors.Ink,
                        )
                        Text(
                            text = device.location,
                            style = MullionTextStyles.UISmall.copy(fontSize = 8.5.sp),
                            color = MullionColors.Faint,
                        )
                    }
                    StatusChip(device.status)
                }
                Spacer(Modifier.height(12.dp))
                MetricBar("CPU", device.metrics.cpuPercent)
                Spacer(Modifier.height(8.dp))
                MetricBar("内存", device.metrics.memoryPercent)
                Spacer(Modifier.height(8.dp))
                MetricBar("信号", device.metrics.signalPercent)
                Spacer(Modifier.height(12.dp))
                MullionButton(
                    text = "打开完整详情",
                    onClick = onOpen,
                    size = MullionButtonSize.S,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        item {
            ReferenceSection {
                RefLabel("关键信息", size = 8)
                Spacer(Modifier.height(6.dp))
                InfoRow("固件", device.firmware)
                InfoRow("心跳", device.lastSeen)
                InfoRow("类型", device.kind.label)
            }
        }
    }
}

@Composable
fun DeviceFilterSheet(
    selected: DeviceFilter,
    onSelect: (DeviceFilter) -> Unit,
    onDismiss: () -> Unit,
) {
    DockedSheet(
        open = true,
        onDismissRequest = onDismiss,
        hinge = currentHinge(),
        dockSide = DockSide.Auto,
        title = {
            RefLabel("筛选设备", color = MullionColors.Volt)
        },
        sheetContent = {
            DeviceFilter.entries.forEach { filter ->
                MullionRadio(
                    selected = filter == selected,
                    onSelect = { onSelect(filter) },
                    label = filter.label,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(8.dp))
            MullionButton(
                text = "关闭",
                onClick = onDismiss,
                variant = MullionButtonVariant.Tonal,
                size = MullionButtonSize.S,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        content = {},
    )
}
