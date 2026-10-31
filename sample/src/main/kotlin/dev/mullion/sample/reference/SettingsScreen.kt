package dev.mullion.sample.reference

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.base.MullionAvatar
import dev.mullion.compose.base.MullionAvatarSize
import dev.mullion.compose.base.MullionBadge
import dev.mullion.compose.base.MullionButton
import dev.mullion.compose.base.MullionButtonSize
import dev.mullion.compose.base.MullionButtonVariant
import dev.mullion.compose.base.MullionSlider
import dev.mullion.compose.base.MullionSwitch
import dev.mullion.compose.base.MullionTextField
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.window.currentBreakpoint
import dev.mullion.compose.adaptive.mullionMetrics
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.MullionMetrics
import dev.mullion.sample.ui.Lab
import dev.mullion.sample.ui.SampleBody
import dev.mullion.sample.ui.SampleChip

@Composable
fun SettingsScreen(
    state: ReferenceUiState,
    onSiteNameChange: (String) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onAutoUpdateChange: (Boolean) -> Unit,
    onPaneGutterChange: (Int) -> Unit,
    onManagePermissions: () -> Unit,
    onOpenCabinet: () -> Unit,
    onReload: () -> Unit,
) {
    val bp = currentBreakpoint()
    val window = mullionWindow()
    val metrics: MullionMetrics = mullionMetrics()

    ReferencePage {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Lab("PROFILE / OPERATOR", color = MullionColors.Volt, size = 9)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "设置",
                            style = MullionTextStyles.Mono.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                            color = MullionColors.Ink,
                        )
                    }
                    SampleChip("v1.0.0")
                }
            }

            item {
                ReferenceSection {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MullionBadge(count = state.alerts.size) {
                            MullionAvatar(
                                name = state.user.name,
                                size = MullionAvatarSize.L,
                                colorIndex = 0,
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.user.name,
                                style = MullionTextStyles.Mono.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                                color = MullionColors.Ink,
                            )
                            Text(
                                text = "${state.user.role} · ${state.siteName}",
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                                color = MullionColors.Faint,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "SHIFT A",
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                                color = MullionColors.Mint,
                            )
                            Text(
                                text = "08:00–20:00",
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                                color = MullionColors.Faint,
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    MullionTextField(
                        value = state.siteName,
                        onValueChange = onSiteNameChange,
                        label = "站点名称",
                        placeholder = "上海 · 装配车间",
                    )
                }
            }

            item {
                ReferenceSection {
                    Lab("NOTIFICATIONS / AUTOMATION", size = 8)
                    Spacer(Modifier.height(8.dp))
                    MullionSwitch(
                        checked = state.notificationsEnabled,
                        onCheckedChange = onNotificationsChange,
                        label = "设备告警通知",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    MullionSwitch(
                        checked = state.autoUpdateEnabled,
                        onCheckedChange = onAutoUpdateChange,
                        label = "夜间自动更新固件",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item {
                ReferenceSection {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Lab("LAYOUT / PANE GUTTER", size = 8)
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "${state.paneGutterDp}dp",
                            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                            color = MullionColors.Volt,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    MullionSlider(
                        value = state.paneGutterDp.toFloat(),
                        onValueChange = { onPaneGutterChange(it.toInt()) },
                        valueRange = 12f..40f,
                        label = null,
                        valueLabel = null,
                    )
                    SampleBody(
                        "修改后设备页的 MultiPane 栏间距会实时更新。",
                        size = 10,
                    )
                }
            }

            item {
                ReferenceSection {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Lab("PERMISSIONS", size = 8)
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "${state.grantedPermissionCount} / ${ReferencePermission.entries.size} 已授予",
                            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                            color = if (state.anyPermissionDenied) MullionColors.Amber else MullionColors.Mint,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    ReferencePermission.entries.forEach { permission ->
                        PermissionStatusRow(
                            permission = permission,
                            granted = state.permissions[permission] == true,
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    MullionButton(
                        text = "管理设备权限",
                        onClick = onManagePermissions,
                        variant = MullionButtonVariant.Outlined,
                        size = MullionButtonSize.S,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item {
                ReferenceSection {
                    Lab("WINDOW / RESOURCES", size = 8)
                    Spacer(Modifier.height(8.dp))
                    InfoRow("宽度", "${window.widthDp}dp · ${bp.label}")
                    InfoRow("导航", window.navigationMode.name)
                    InfoRow("当前栏距", "${state.paneGutterDp}dp")
                    InfoRow("设计栏距", "${metrics.paneGutterDp}dp")
                    InfoRow("区块间距", "${metrics.sectionSpacingDp}dp")
                    InfoRow("显示字号", "${metrics.displayTypeSp}sp")
                    Spacer(Modifier.height(6.dp))
                    SampleBody(
                        "这些值与 Compose 组件、values-w600dp / values-w840dp 资源桶来自同一把标尺。",
                        size = 10,
                    )
                }
            }

            item {
                ReferenceSection {
                    Lab("TOOLS / ABOUT", size = 8)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MullionButton(
                            text = "重新加载数据",
                            onClick = onReload,
                            variant = MullionButtonVariant.Tonal,
                            size = MullionButtonSize.S,
                            modifier = Modifier.weight(1f),
                        )
                        MullionButton(
                            text = "组件柜",
                            onClick = onOpenCabinet,
                            variant = MullionButtonVariant.Outlined,
                            size = MullionButtonSize.S,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "MULLION 1.0.0 · Apache-2.0 · minSdk 21",
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                        color = MullionColors.Faint,
                    )
                    Text(
                        text = "26 个组件 · Compose + View · 一樘窗，处处合缝",
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                        color = MullionColors.Faint,
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionStatusRow(
    permission: ReferencePermission,
    granted: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = permission.label,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                color = MullionColors.Ink,
            )
            Text(
                text = permission.permission,
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                color = MullionColors.Faint,
            )
        }
        Text(
            text = if (granted) "已授予" else "未授予",
            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
            color = if (granted) MullionColors.Mint else MullionColors.Amber,
            modifier = Modifier
                .background(
                    color = if (granted) {
                        MullionColors.Mint.copy(alpha = 0.1f)
                    } else {
                        MullionColors.Amber.copy(alpha = 0.1f)
                    },
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = if (granted) {
                        MullionColors.Mint.copy(alpha = 0.35f)
                    } else {
                        MullionColors.Amber.copy(alpha = 0.35f)
                    },
                    shape = CircleShape,
                )
                .padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
