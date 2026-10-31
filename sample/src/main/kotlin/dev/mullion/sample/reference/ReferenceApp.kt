package dev.mullion.sample.reference

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.mullion.compose.adaptive.AdaptiveDialog
import dev.mullion.compose.adaptive.AdaptiveScaffold
import dev.mullion.compose.adaptive.EdgeToEdgeBox
import dev.mullion.compose.adaptive.MullionPermission
import dev.mullion.compose.adaptive.OrbitNav
import dev.mullion.compose.adaptive.OrbitNavItem
import dev.mullion.compose.adaptive.PermissionAnchor
import dev.mullion.compose.adaptive.PermissionSheet
import dev.mullion.compose.adaptive.SharedAxis
import dev.mullion.compose.adaptive.SharedAxisTransition
import dev.mullion.compose.base.MullionButton
import dev.mullion.compose.base.MullionButtonSize
import dev.mullion.compose.base.MullionButtonVariant
import dev.mullion.compose.base.MullionSnackbarHost
import dev.mullion.compose.base.rememberMullionSnackbarHostState
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.sample.CabinetActivity
import dev.mullion.sample.ui.SampleBody

/**
 * The reference operations app.
 *
 * This is the “real engineering” sample: a repository-backed ViewModel, data
 * loading, search/filter, list-detail-preview, destructive-action confirmation,
 * runtime permission flow, snackbars and adaptive navigation. The component
 * cabinet is reachable from Settings and lives in its own Activity.
 */
@Composable
fun ReferenceApp() {
    val context = LocalContext.current
    val repository = remember { InMemoryDeviceRepository() }
    val viewModel: ReferenceViewModel = viewModel(
        factory = remember(repository) { ReferenceViewModel.factory(repository) },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = rememberMullionSnackbarHostState()

    var screenName by rememberSaveable { mutableStateOf(RefScreen.Workbench.name) }
    val screen = RefScreen.entries.firstOrNull { it.name == screenName } ?: RefScreen.Workbench

    val initialPermissions = remember(context) {
        ReferencePermission.entries.associateWith { permission ->
            ContextCompat.checkSelfPermission(context, permission.permission) ==
                PackageManager.PERMISSION_GRANTED
        }
    }
    LaunchedEffect(initialPermissions) {
        viewModel.setInitialPermissions(initialPermissions)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        viewModel.onPermissionResult(result)
    }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message ->
            snackbar.showSnackbar(message)
        }
    }

    val navItems = listOf(
        OrbitNavItem(RefScreen.Workbench.label),
        OrbitNavItem(
            label = RefScreen.Devices.label,
            badge = state.alerts.size.takeIf { it > 0 }?.toString(),
        ),
        OrbitNavItem(RefScreen.Activity.label),
        OrbitNavItem(RefScreen.Settings.label),
    )
    val navIcons = listOf(
        Icons.Outlined.Dashboard,
        Icons.Outlined.Devices,
        Icons.Outlined.History,
        Icons.Outlined.Settings,
    )
    val nav: @Composable () -> Unit = {
        OrbitNav(
            items = navItems,
            selectedIndex = screen.ordinal,
            onSelect = { index ->
                screenName = RefScreen.entries[index].name
            },
            header = { DrawerHeader(state) },
            icon = { index, selected ->
                Icon(
                    imageVector = navIcons[index],
                    contentDescription = null,
                    tint = if (selected) MullionColors.Volt else MullionColors.Muted,
                    modifier = Modifier.size(20.dp),
                )
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(MullionColors.Background)) {
        EdgeToEdgeBox(applyPadding = false) { insets ->
            Box(Modifier.fillMaxSize()) {
                AdaptiveScaffold(
                    bottomBarInset = insets.calculateBottomPadding() + 16.dp,
                    modifier = Modifier.padding(
                        start = insets.calculateLeftPadding(androidx.compose.ui.platform.LocalLayoutDirection.current),
                        top = insets.calculateTopPadding(),
                        end = insets.calculateRightPadding(androidx.compose.ui.platform.LocalLayoutDirection.current),
                    ).imePadding(),
                    topBar = {
                        ReferenceTopBar(
                            screen = screen,
                            state = state,
                            onScan = viewModel::openPermissionSheet,
                            onRefresh = viewModel::refresh,
                            onProfile = { screenName = RefScreen.Settings.name },
                        )
                    },
                    bottomBar = nav,
                    rail = nav,
                    drawer = nav,
                    content = { _ ->
                        SharedAxisTransition(
                            targetState = screen,
                            modifier = Modifier.fillMaxSize(),
                            axis = SharedAxis.X,
                        ) { destination ->
                            when (destination) {
                                RefScreen.Workbench -> WorkbenchScreen(
                                    state = state,
                                    onOpenDevice = { id ->
                                        viewModel.selectDevice(id)
                                        screenName = RefScreen.Devices.name
                                    },
                                    onOpenDevices = { screenName = RefScreen.Devices.name },
                                    onScan = viewModel::openPermissionSheet,
                                    onRefresh = viewModel::refresh,
                                )

                                RefScreen.Devices -> DevicesScreen(
                                    state = state,
                                    onSelectDevice = viewModel::selectDevice,
                                    onBack = viewModel::clearSelectedDevice,
                                    onQueryChange = viewModel::setQuery,
                                    onFilterChange = viewModel::setFilter,
                                    onTabChange = viewModel::setTab,
                                    onRestart = viewModel::openRestartDialog,
                                    onScan = viewModel::openPermissionSheet,
                                    onOpenFilter = viewModel::openFilterSheet,
                                    onRefresh = viewModel::refresh,
                                )

                                RefScreen.Activity -> ActivityScreen(
                                    state = state,
                                    onOpenDevice = { id ->
                                        viewModel.selectDevice(id)
                                        screenName = RefScreen.Devices.name
                                    },
                                )

                                RefScreen.Settings -> SettingsScreen(
                                    state = state,
                                    onSiteNameChange = viewModel::setSiteName,
                                    onNotificationsChange = viewModel::setNotificationsEnabled,
                                    onAutoUpdateChange = viewModel::setAutoUpdateEnabled,
                                    onPaneGutterChange = viewModel::setPaneGutter,
                                    onManagePermissions = viewModel::openPermissionSheet,
                                    onOpenCabinet = {
                                        context.startActivity(Intent(context, CabinetActivity::class.java))
                                    },
                                    onReload = viewModel::load,
                                )
                            }
                        }
                    },
                )

                MullionSnackbarHost(
                    state = snackbar,
                    modifier = Modifier.padding(bottom = insets.calculateBottomPadding() + 16.dp),
                )

                val restartTarget = state.restartTarget
                AdaptiveDialog(
                    visible = restartTarget != null,
                    onDismissRequest = viewModel::dismissRestartDialog,
                ) {
                    RefLabel("远程操作", color = MullionColors.Signal)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "重启「${restartTarget?.name ?: "设备"}」？",
                        style = MullionTextStyles.UI.copy(
                            fontSize = 17.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        ),
                        color = MullionColors.Ink,
                    )
                    Spacer(Modifier.height(8.dp))
                    SampleBody(
                        "设备会短暂离线，正在运行的采集任务将在重启后自动恢复。该操作会记录到动态时间线。",
                        size = 12,
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MullionButton(
                            text = "确认重启",
                            onClick = viewModel::confirmRestart,
                            size = MullionButtonSize.S,
                        )
                        MullionButton(
                            text = "取消",
                            onClick = viewModel::dismissRestartDialog,
                            variant = MullionButtonVariant.Tonal,
                            size = MullionButtonSize.S,
                        )
                    }
                }

                PermissionSheet(
                    visible = state.permissionSheetVisible,
                    permissions = ReferencePermission.entries.map { permission ->
                        MullionPermission(
                            label = permission.label,
                            permission = permission.permission,
                            rationale = permission.rationale,
                            required = permission.required,
                        )
                    },
                    onDismissRequest = viewModel::dismissPermissionSheet,
                    onGrantAll = {
                        viewModel.dismissPermissionSheet()
                        val missing = ReferencePermission.entries.filter {
                            state.permissions[it] != true && permissionExists(context, it.permission)
                        }
                        if (missing.isEmpty()) {
                            viewModel.usePermissionsForThisSession()
                        } else {
                            permissionLauncher.launch(missing.map { it.permission }.toTypedArray())
                        }
                    },
                    onDeny = viewModel::usePermissionsForThisSession,
                    anchor = PermissionAnchor.Auto,
                    title = "设备权限",
                    confirmLabel = "全部允许",
                    dismissLabel = "仅本次",
                )

                if (state.filterSheetVisible) {
                    DeviceFilterSheet(
                        selected = state.filter,
                        onSelect = {
                            viewModel.setFilter(it)
                            viewModel.dismissFilterSheet()
                        },
                        onDismiss = viewModel::dismissFilterSheet,
                    )
                }
            }
        }
    }
}

private fun permissionExists(context: android.content.Context, permission: String): Boolean {
    if (permission == Manifest.permission.POST_NOTIFICATIONS && Build.VERSION.SDK_INT < 33) {
        return false
    }
    return context.packageManager.resolvePermission(permission) != null
}

private fun PackageManager.resolvePermission(permission: String): Boolean =
    runCatching { getPermissionInfo(permission, 0) }.isSuccess
