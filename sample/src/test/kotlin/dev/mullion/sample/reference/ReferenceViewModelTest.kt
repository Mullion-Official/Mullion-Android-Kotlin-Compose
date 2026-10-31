package dev.mullion.sample.reference

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReferenceViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads a fleet and derives status counts`() = runTest {
        val viewModel = ReferenceViewModel(InMemoryDeviceRepository())
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(5, state.devices.size)
        assertEquals(3, state.onlineCount)
        assertEquals(1, state.warningCount)
        assertEquals(1, state.offlineCount)
        assertEquals(3, state.alerts.size)
        assertFalse(state.loading)
    }

    @Test
    fun `filter hides a selected device and clears the selection`() = runTest {
        val viewModel = ReferenceViewModel(InMemoryDeviceRepository())
        advanceUntilIdle()

        viewModel.selectDevice("SN-B4")
        assertEquals("SN-B4", viewModel.state.value.selectedDeviceId)

        viewModel.setFilter(DeviceFilter.Offline)
        assertNull(viewModel.state.value.selectedDeviceId)
        assertEquals(listOf("MT-D3"), viewModel.state.value.filteredDevices.map { it.id })
    }

    @Test
    fun `restarting an online device updates status, alerts and timeline`() = runTest {
        val viewModel = ReferenceViewModel(InMemoryDeviceRepository())
        advanceUntilIdle()

        viewModel.openRestartDialog("SN-B4")
        viewModel.confirmRestart()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(DeviceStatus.Online, state.devices.first { it.id == "SN-B4" }.status)
        assertEquals(2, state.alerts.size)
        assertTrue(state.events.any { it.kind == ActivityKind.Restart && it.deviceId == "SN-B4" })
        assertNull(state.restartingId)
    }

    @Test
    fun `restarting an offline device fails without changing its status`() = runTest {
        val viewModel = ReferenceViewModel(InMemoryDeviceRepository())
        advanceUntilIdle()

        viewModel.openRestartDialog("MT-D3")
        viewModel.confirmRestart()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(DeviceStatus.Offline, state.devices.first { it.id == "MT-D3" }.status)
        assertNull(state.restartingId)
        assertNull(state.restartTargetId)
    }

    @Test
    fun `permission result updates the granted map`() = runTest {
        val viewModel = ReferenceViewModel(InMemoryDeviceRepository())
        advanceUntilIdle()

        viewModel.onPermissionResult(
            mapOf(
                ReferencePermission.Camera.permission to true,
                ReferencePermission.Location.permission to true,
                ReferencePermission.Notifications.permission to false,
            ),
        )

        val state = viewModel.state.value
        assertTrue(state.permissions[ReferencePermission.Camera] == true)
        assertTrue(state.permissions[ReferencePermission.Location] == true)
        assertFalse(state.permissions[ReferencePermission.Notifications] == true)
        assertEquals(2, state.grantedPermissionCount)
        assertFalse(state.permissionSheetVisible)
    }
}
