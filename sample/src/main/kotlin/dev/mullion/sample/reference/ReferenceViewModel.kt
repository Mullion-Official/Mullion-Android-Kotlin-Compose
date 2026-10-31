package dev.mullion.sample.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Operator-facing state for the reference app.
 *
 * The ViewModel owns the data snapshot and transient UI state (selection,
 * query, filter, dialog target, permission sheet). It never holds a Compose
 * type, so it can be unit-tested and reused by a View screen.
 */
data class ReferenceUiState(
    val user: UserProfile = UserProfile("Lin Wei", "设备运维", "LW", "上海 · 装配车间"),
    val devices: List<Device> = emptyList(),
    val alerts: List<DeviceAlert> = emptyList(),
    val events: List<ActivityEvent> = emptyList(),
    val query: String = "",
    val filter: DeviceFilter = DeviceFilter.All,
    val selectedDeviceId: String? = null,
    val tab: DeviceTab = DeviceTab.Overview,
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val restartTargetId: String? = null,
    val restartingId: String? = null,
    val filterSheetVisible: Boolean = false,
    val permissionSheetVisible: Boolean = false,
    val permissions: Map<ReferencePermission, Boolean> =
        ReferencePermission.entries.associateWith { false },
    val notificationsEnabled: Boolean = true,
    val autoUpdateEnabled: Boolean = true,
    val siteName: String = "上海 · 装配车间",
    val paneGutterDp: Int = 24,
) {
    val selectedDevice: Device?
        get() = devices.firstOrNull { it.id == selectedDeviceId }

    val restartTarget: Device?
        get() = devices.firstOrNull { it.id == restartTargetId }

    val filteredDevices: List<Device>
        get() = devices.filter { filter.matches(it) && it.matchesQuery(query) }

    val onlineCount: Int
        get() = devices.count { it.status == DeviceStatus.Online }

    val warningCount: Int
        get() = devices.count { it.status == DeviceStatus.Warning }

    val offlineCount: Int
        get() = devices.count { it.status == DeviceStatus.Offline }

    val grantedPermissionCount: Int
        get() = permissions.values.count { it }

    val anyPermissionDenied: Boolean
        get() = permissions.values.any { !it }
}


private fun Device.matchesQuery(query: String): Boolean =
    query.isBlank() ||
        name.contains(query, ignoreCase = true) ||
        model.contains(query, ignoreCase = true) ||
        location.contains(query, ignoreCase = true) ||
        serial.contains(query, ignoreCase = true)

class ReferenceViewModel(
    private val repository: DeviceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ReferenceUiState())
    val state: StateFlow<ReferenceUiState> = _state.asStateFlow()

    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            repository.load()
                .onSuccess { snapshot -> applySnapshot(snapshot, loading = false) }
                .onFailure {
                    _state.update { it.copy(loading = false) }
                    _messages.send("加载失败，请检查网络后重试")
                }
        }
    }

    fun refresh() {
        if (_state.value.refreshing) return
        viewModelScope.launch {
            _state.update { it.copy(refreshing = true) }
            repository.refresh()
                .onSuccess { snapshot ->
                    applySnapshot(snapshot, loading = false)
                    _state.update { it.copy(refreshing = false) }
                    _messages.send("已刷新 ${snapshot.devices.size} 台设备")
                }
                .onFailure {
                    _state.update { it.copy(refreshing = false) }
                    _messages.send("刷新失败，保留上次数据")
                }
        }
    }

    fun selectDevice(deviceId: String) {
        _state.update { it.copy(selectedDeviceId = deviceId, tab = DeviceTab.Overview) }
    }

    fun clearSelectedDevice() {
        _state.update { it.copy(selectedDeviceId = null) }
    }

    fun setQuery(query: String) {
        _state.update { current ->
            val nextMatches = current.devices.filter {
                current.filter.matches(it) && it.matchesQuery(query)
            }
            val visibleIds = nextMatches.mapTo(HashSet()) { it.id }
            current.copy(
                query = query,
                selectedDeviceId = current.selectedDeviceId?.takeIf { it in visibleIds },
            )
        }
    }

    fun setFilter(filter: DeviceFilter) {
        _state.update { current ->
            val nextMatches = current.devices.filter {
                filter.matches(it) && it.matchesQuery(current.query)
            }
            val visibleIds = nextMatches.mapTo(HashSet()) { it.id }
            current.copy(
                filter = filter,
                selectedDeviceId = current.selectedDeviceId?.takeIf { it in visibleIds },
            )
        }
    }

    fun setTab(tab: DeviceTab) {
        _state.update { it.copy(tab = tab) }
    }

    fun openRestartDialog(deviceId: String) {
        _state.update { it.copy(restartTargetId = deviceId) }
    }

    fun dismissRestartDialog() {
        if (_state.value.restartingId != null) return
        _state.update { it.copy(restartTargetId = null) }
    }

    fun confirmRestart() {
        val target = _state.value.restartTarget ?: return
        _state.update { it.copy(restartingId = target.id, restartTargetId = null) }
        viewModelScope.launch {
            repository.restart(target.id)
                .onSuccess { snapshot ->
                    applySnapshot(snapshot, loading = false)
                    _state.update { it.copy(restartingId = null) }
                    _messages.send("「${target.name}」已重启并恢复在线")
                }
                .onFailure { error ->
                    _state.update { it.copy(restartingId = null) }
                    _messages.send(error.message ?: "重启失败")
                }
        }
    }

    fun openFilterSheet() {
        _state.update { it.copy(filterSheetVisible = true) }
    }

    fun dismissFilterSheet() {
        _state.update { it.copy(filterSheetVisible = false) }
    }

    fun openPermissionSheet() {
        _state.update { it.copy(permissionSheetVisible = true) }
    }

    fun dismissPermissionSheet() {
        _state.update { it.copy(permissionSheetVisible = false) }
    }

    fun setInitialPermissions(permissions: Map<ReferencePermission, Boolean>) {
        _state.update { current ->
            current.copy(
                permissions = current.permissions + permissions,
            )
        }
    }

    fun onPermissionResult(result: Map<String, Boolean>) {
        val updated = ReferencePermission.entries.associateWith { reference ->
            result[reference.permission] ?: _state.value.permissions[reference] ?: false
        }
        _state.update {
            it.copy(
                permissions = it.permissions + updated,
                permissionSheetVisible = false,
            )
        }
        val granted = updated.values.count { it }
        viewModelScope.launch {
            _messages.send("权限已更新：$granted / ${ReferencePermission.entries.size}")
        }
    }

    fun usePermissionsForThisSession() {
        _state.update {
            it.copy(
                permissions = it.permissions + ReferencePermission.entries.associateWith { true },
                permissionSheetVisible = false,
            )
        }
        viewModelScope.launch {
            _messages.send("已按本次会话授予权限")
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _state.update { it.copy(notificationsEnabled = enabled) }
    }

    fun setAutoUpdateEnabled(enabled: Boolean) {
        _state.update { it.copy(autoUpdateEnabled = enabled) }
    }

    fun setSiteName(name: String) {
        _state.update { it.copy(siteName = name.take(40)) }
    }

    fun setPaneGutter(gutterDp: Int) {
        _state.update { it.copy(paneGutterDp = gutterDp.coerceIn(12, 40)) }
    }

    private fun applySnapshot(snapshot: DeviceSnapshot, loading: Boolean) {
        _state.update { current ->
            val selectionStillValid = snapshot.devices.any { it.id == current.selectedDeviceId }
            current.copy(
                devices = snapshot.devices,
                alerts = snapshot.alerts,
                events = snapshot.events,
                loading = loading,
                selectedDeviceId = if (selectionStillValid) current.selectedDeviceId else null,
            )
        }
    }

    companion object {
        fun factory(repository: DeviceRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ReferenceViewModel(repository) as T
            }
    }
}
