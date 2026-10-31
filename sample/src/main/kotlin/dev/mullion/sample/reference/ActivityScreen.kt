package dev.mullion.sample.reference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.base.MullionSegmentedControl
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.sample.ui.Lab
import dev.mullion.sample.ui.SampleBody
import dev.mullion.sample.ui.SampleChip

private enum class ActivityFilter(val label: String) {
    All("全部"),
    Devices("设备"),
    System("系统"),
}

@Composable
fun ActivityScreen(
    state: ReferenceUiState,
    onOpenDevice: (String) -> Unit,
) {
    var filterIndex by rememberSaveable { mutableIntStateOf(0) }
    val filter = ActivityFilter.entries[filterIndex]
    val filtered = state.events.filter { event ->
        when (filter) {
            ActivityFilter.All -> true
            ActivityFilter.Devices -> event.deviceId != null
            ActivityFilter.System -> event.deviceId == null
        }
    }

    ReferencePage {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Lab("AUDIT TRAIL / ${state.events.size} EVENTS", color = MullionColors.Volt, size = 9)
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = "动态",
                            style = MullionTextStyles.Mono.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                            color = MullionColors.Ink,
                        )
                        Spacer(Modifier.height(3.dp))
                        SampleBody("设备操作、告警与系统任务的时间线。", size = 12)
                    }
                    SampleChip("本地保留 7 天")
                }
                Spacer(Modifier.height(10.dp))
                MullionSegmentedControl(
                    segments = ActivityFilter.entries.map { it.label },
                    selectedIndex = filterIndex,
                    onSelected = { filterIndex = it },
                )
            }

            if (filtered.isEmpty()) {
                item {
                    ReferenceSection {
                        EmptyState("这个筛选下没有动态", "切换到“全部”，或者等待新的设备事件。")
                    }
                }
            } else {
                items(filtered, key = { it.id }) { event ->
                    ActivityEventCard(
                        event = event,
                        onClick = event.deviceId?.let { id -> { onOpenDevice(id) } },
                    )
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Lab("RETENTION", size = 8)
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "7 天 · 仅本地",
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                        color = MullionColors.Faint,
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityEventCard(
    event: ActivityEvent,
    onClick: (() -> Unit)?,
) {
    ReferenceSection {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SampleChip(event.kind.label, selected = true)
            Spacer(Modifier.weight(1f))
            Text(
                text = formatEventTime(event.timestamp),
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                color = MullionColors.Faint,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = event.title,
            style = MullionTextStyles.Mono.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = MullionColors.Ink,
        )
        Text(
            text = event.detail,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
            color = MullionColors.Muted,
        )
        if (onClick != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "查看设备 →",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                color = MullionColors.Volt,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable(onClick = onClick),
            )
        }
    }
}
