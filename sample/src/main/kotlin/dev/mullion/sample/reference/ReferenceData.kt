package dev.mullion.sample.reference

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** The four top-level destinations of the reference operations app. */
enum class RefScreen(val label: String) {
    Workbench("工作台"),
    Devices("设备"),
    Activity("动态"),
    Settings("设置"),
}

enum class DeviceKind(val label: String) {
    Gateway("边缘网关"),
    Sensor("环境传感器"),
    Camera("视觉相机"),
    Meter("电力仪表"),
}

enum class DeviceStatus(val label: String) {
    Online("在线"),
    Warning("告警"),
    Offline("离线"),
}

enum class DeviceFilter(val label: String) {
    All("全部"),
    Online("在线"),
    Warning("告警"),
    Offline("离线"),
    ;

    fun matches(device: Device): Boolean = when (this) {
        All -> true
        Online -> device.status == DeviceStatus.Online
        Warning -> device.status == DeviceStatus.Warning
        Offline -> device.status == DeviceStatus.Offline
    }
}

enum class DeviceTab(val label: String) {
    Overview("概览"),
    Metrics("指标"),
    Logs("日志"),
}

enum class AlertSeverity(val label: String) {
    Critical("严重"),
    Warning("告警"),
    Info("提示"),
}

enum class ActivityKind(val label: String) {
    Online("上线"),
    Offline("离线"),
    Restart("重启"),
    Firmware("固件"),
    Backup("备份"),
}

enum class ReferencePermission(
    val label: String,
    val permission: String,
    val rationale: String,
    val required: Boolean = false,
) {
    Camera("相机", "android.permission.CAMERA", "扫描设备二维码与机身编码", required = true),
    Location("位置", "android.permission.ACCESS_FINE_LOCATION", "发现附近的蓝牙 / Wi-Fi 设备"),
    Notifications("通知", "android.permission.POST_NOTIFICATIONS", "设备告警与固件更新提醒"),
}

data class UserProfile(
    val name: String,
    val role: String,
    val initials: String,
    val site: String,
)

data class DeviceMetrics(
    val cpuPercent: Int,
    val memoryPercent: Int,
    val signalPercent: Int,
    val temperatureC: Float,
)

data class DeviceLog(
    val time: String,
    val level: String,
    val message: String,
)

data class Device(
    val id: String,
    val name: String,
    val model: String,
    val serial: String,
    val kind: DeviceKind,
    val status: DeviceStatus,
    val location: String,
    val firmware: String,
    val uptime: String,
    val lastSeen: String,
    val metrics: DeviceMetrics,
    val description: String,
    val logs: List<DeviceLog>,
)

data class DeviceAlert(
    val id: String,
    val deviceId: String,
    val title: String,
    val detail: String,
    val severity: AlertSeverity,
    val timeAgo: String,
)

data class ActivityEvent(
    val id: String,
    val deviceId: String?,
    val title: String,
    val detail: String,
    val timestamp: Long,
    val kind: ActivityKind,
)

data class DeviceSnapshot(
    val devices: List<Device>,
    val alerts: List<DeviceAlert>,
    val events: List<ActivityEvent>,
)

/** A realistic repository boundary: the UI never knows where the data came from. */
interface DeviceRepository {
    suspend fun load(): Result<DeviceSnapshot>
    suspend fun refresh(): Result<DeviceSnapshot>
    suspend fun restart(deviceId: String): Result<DeviceSnapshot>
}

/**
 * In-memory implementation with artificial latency so loading and error states
 * are exercised. In a production app this would be a Retrofit/Room repository.
 */
class InMemoryDeviceRepository : DeviceRepository {
    private var devices: List<Device> = seedDevices()
    private var alerts: List<DeviceAlert> = seedAlerts()
    private var events: List<ActivityEvent> = seedEvents()

    override suspend fun load(): Result<DeviceSnapshot> {
        delay(420)
        return Result.success(snapshot())
    }

    override suspend fun refresh(): Result<DeviceSnapshot> {
        delay(700)
        devices = devices.map { it.copy(lastSeen = "刚刚") }
        events = buildList {
            add(
                ActivityEvent(
                    id = "evt-refresh-${System.currentTimeMillis()}",
                    deviceId = null,
                    title = "手动刷新",
                    detail = "已同步 ${devices.size} 台设备状态",
                    timestamp = System.currentTimeMillis(),
                    kind = ActivityKind.Online,
                ),
            )
            addAll(events.take(8))
        }
        return Result.success(snapshot())
    }

    override suspend fun restart(deviceId: String): Result<DeviceSnapshot> {
        delay(1_050)
        val current = devices.firstOrNull { it.id == deviceId }
            ?: return Result.failure(IllegalArgumentException("设备不存在"))
        if (current.status == DeviceStatus.Offline) {
            return Result.failure(IllegalStateException("设备离线，无法远程重启"))
        }

        devices = devices.map { device ->
            if (device.id == deviceId) {
                device.copy(
                    status = DeviceStatus.Online,
                    uptime = "0 分钟",
                    lastSeen = "刚刚",
                    metrics = device.metrics.copy(
                        cpuPercent = (device.metrics.cpuPercent - 18).coerceAtLeast(12),
                        memoryPercent = (device.metrics.memoryPercent - 9).coerceAtLeast(20),
                    ),
                )
            } else {
                device
            }
        }
        alerts = alerts.filterNot { it.deviceId == deviceId && it.severity != AlertSeverity.Info }
        events = buildList {
            add(
                ActivityEvent(
                    id = "evt-restart-${System.currentTimeMillis()}",
                    deviceId = deviceId,
                    title = "远程重启完成",
                    detail = "${current.name} · ${current.model} 已恢复在线",
                    timestamp = System.currentTimeMillis(),
                    kind = ActivityKind.Restart,
                ),
            )
            addAll(events.take(8))
        }
        return Result.success(snapshot())
    }

    private fun snapshot() = DeviceSnapshot(devices = devices, alerts = alerts, events = events)
}

private fun seedDevices(): List<Device> = listOf(
    Device(
        id = "GW-A1",
        name = "边缘网关 A1",
        model = "Edge Gateway G2",
        serial = "GW2-A1-8821",
        kind = DeviceKind.Gateway,
        status = DeviceStatus.Online,
        location = "装配车间 · 北侧",
        firmware = "2.8.1",
        uptime = "12 天 4 小时",
        lastSeen = "刚刚",
        metrics = DeviceMetrics(cpuPercent = 34, memoryPercent = 52, signalPercent = 92, temperatureC = 37.4f),
        description = "负责北侧产线的设备汇聚与边缘推理。网关会把本地告警压缩后上传，断网时保留最近 24 小时缓存。",
        logs = listOf(
            DeviceLog("09:42", "INFO", "gateway heartbeat ok · 42ms"),
            DeviceLog("09:18", "INFO", "edge rule 'temp-high' loaded"),
            DeviceLog("08:55", "WARN", "uplink retry · backoff 200ms"),
            DeviceLog("08:41", "INFO", "firmware channel stable"),
        ),
    ),
    Device(
        id = "SN-B4",
        name = "温湿度传感器 B4",
        model = "Climate Sensor S2",
        serial = "CS2-B4-1140",
        kind = DeviceKind.Sensor,
        status = DeviceStatus.Warning,
        location = "装配车间 · 东门",
        firmware = "1.4.0",
        uptime = "3 天 16 小时",
        lastSeen = "2 分钟前",
        metrics = DeviceMetrics(cpuPercent = 71, memoryPercent = 63, signalPercent = 48, temperatureC = 41.8f),
        description = "东门区域的温湿度采样点。当前温度高于 38°C 阈值，建议先检查风道，再决定是否重启。",
        logs = listOf(
            DeviceLog("09:44", "WARN", "temperature 41.8C > threshold 38.0C"),
            DeviceLog("09:40", "INFO", "sample interval 10s"),
            DeviceLog("09:12", "INFO", "battery 86%"),
            DeviceLog("08:58", "WARN", "signal -82dBm"),
        ),
    ),
    Device(
        id = "CM-C2",
        name = "入口相机 C2",
        model = "Vision Cam 4K",
        serial = "VC4-C2-7741",
        kind = DeviceKind.Camera,
        status = DeviceStatus.Online,
        location = "仓库 · 入口",
        firmware = "3.2.0",
        uptime = "8 天 2 小时",
        lastSeen = "刚刚",
        metrics = DeviceMetrics(cpuPercent = 58, memoryPercent = 44, signalPercent = 88, temperatureC = 39.1f),
        description = "入口通道的视觉识别相机，负责托盘条码与人员通行。固件 3.2.1 修复了夜间补光抖动。",
        logs = listOf(
            DeviceLog("09:45", "INFO", "stream 1080p · 24fps"),
            DeviceLog("09:30", "INFO", "plate detected · 沪A·8F21"),
            DeviceLog("09:02", "WARN", "firmware 3.2.1 available"),
            DeviceLog("08:47", "INFO", "recording ring buffer 78%"),
        ),
    ),
    Device(
        id = "MT-D3",
        name = "配电仪表 D3",
        model = "Power Meter P1",
        serial = "PM1-D3-3308",
        kind = DeviceKind.Meter,
        status = DeviceStatus.Offline,
        location = "配电间 · 2F",
        firmware = "1.9.3",
        uptime = "—",
        lastSeen = "18 分钟前",
        metrics = DeviceMetrics(cpuPercent = 0, memoryPercent = 0, signalPercent = 0, temperatureC = 0f),
        description = "2F 配电间的主路电力仪表。离线超过 15 分钟，建议现场检查电源与网关链路。",
        logs = listOf(
            DeviceLog("09:26", "ERROR", "heartbeat timeout · 18m"),
            DeviceLog("09:08", "WARN", "last telemetry 3.2kW"),
            DeviceLog("08:59", "INFO", "scheduled poll skipped"),
        ),
    ),
    Device(
        id = "GW-E5",
        name = "边缘网关 E5",
        model = "Edge Gateway G2",
        serial = "GW2-E5-9014",
        kind = DeviceKind.Gateway,
        status = DeviceStatus.Online,
        location = "装配车间 · 南侧",
        firmware = "2.8.1",
        uptime = "5 天 9 小时",
        lastSeen = "刚刚",
        metrics = DeviceMetrics(cpuPercent = 26, memoryPercent = 38, signalPercent = 81, temperatureC = 35.2f),
        description = "南侧产线的备份网关，负责 A1 断链时的本地接管。",
        logs = listOf(
            DeviceLog("09:43", "INFO", "standby mode · A1 healthy"),
            DeviceLog("09:10", "INFO", "config synced from A1"),
            DeviceLog("08:33", "INFO", "clock offset 3ms"),
        ),
    ),
)

private fun seedAlerts(): List<DeviceAlert> = listOf(
    DeviceAlert(
        id = "al-1",
        deviceId = "SN-B4",
        title = "温度超过阈值",
        detail = "B4 · 41.8°C > 38.0°C，已持续 6 分钟",
        severity = AlertSeverity.Critical,
        timeAgo = "6 分钟前",
    ),
    DeviceAlert(
        id = "al-2",
        deviceId = "MT-D3",
        title = "设备离线",
        detail = "D3 · 心跳丢失 18 分钟",
        severity = AlertSeverity.Warning,
        timeAgo = "18 分钟前",
    ),
    DeviceAlert(
        id = "al-3",
        deviceId = "CM-C2",
        title = "固件可更新",
        detail = "C2 · 3.2.0 → 3.2.1 修复夜间补光",
        severity = AlertSeverity.Info,
        timeAgo = "1 小时前",
    ),
)

private fun seedEvents(): List<ActivityEvent> {
    val now = System.currentTimeMillis()
    return listOf(
        ActivityEvent("evt-1", "GW-A1", "网关心跳恢复", "A1 · 北侧产线 · 42ms", now - 6 * 60_000, ActivityKind.Online),
        ActivityEvent("evt-2", "SN-B4", "温度告警触发", "B4 · 41.8°C · 规则 temp-high", now - 12 * 60_000, ActivityKind.Online),
        ActivityEvent("evt-3", "CM-C2", "固件检查完成", "C2 · 3.2.1 可用", now - 58 * 60_000, ActivityKind.Firmware),
        ActivityEvent("evt-4", "MT-D3", "心跳丢失", "D3 · 离线告警已创建", now - 18 * 60_000, ActivityKind.Offline),
        ActivityEvent("evt-5", null, "夜间备份完成", "配置与日志 · 4.2GB", now - 3 * 3600_000, ActivityKind.Backup),
        ActivityEvent("evt-6", "GW-E5", "远程重启", "E5 · 固件 2.8.1", now - 26 * 3600_000, ActivityKind.Restart),
    )
}

/** Formats an event timestamp for the timeline without java.time desugaring. */
fun formatEventTime(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
