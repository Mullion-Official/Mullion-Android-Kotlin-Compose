package dev.mullion.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BreakpointTest {

    @Test
    fun `classifies every boundary exactly once`() {
        val cases = mapOf(
            0 to Bp.Compact,
            319 to Bp.Compact,
            479 to Bp.Compact,
            480 to Bp.Medium,
            599 to Bp.Medium,
            600 to Bp.Expanded,
            839 to Bp.Expanded,
            840 to Bp.Large,
            1199 to Bp.Large,
            1200 to Bp.XL,
            1600 to Bp.XL,
        )
        cases.forEach { (width, expected) ->
            assertEquals("width=$width", expected, Bp.of(width))
        }
    }

    @Test
    fun `float overload truncates at the boundary`() {
        assertEquals(Bp.Compact, Bp.of(479.99f))
        assertEquals(Bp.Medium, Bp.of(480f))
    }

    @Test
    fun `five design ticks drive the rail`() {
        assertEquals(listOf(320, 480, 600, 840, 1200), Bp.DESIGN_TICKS)
        assertEquals(5, Bp.entries.size)
    }

    @Test
    fun `next and previous walk the ladder`() {
        assertEquals(Bp.Medium, Bp.Compact.next)
        assertEquals(Bp.XL, Bp.Large.next)
        assertNull(Bp.XL.next)
        assertNull(Bp.Compact.previous)
        assertEquals(Bp.Large, Bp.of(1200).previous)
    }

    @Test
    fun `navigation follows the documented thresholds`() {
        assertEquals(NavigationMode.BottomBar, NavigationMode.forWidth(320))
        assertEquals(NavigationMode.BottomBar, NavigationMode.forWidth(599))
        assertEquals(NavigationMode.Rail, NavigationMode.forWidth(600))
        assertEquals(NavigationMode.Rail, NavigationMode.forWidth(839))
        assertEquals(NavigationMode.Drawer, NavigationMode.forWidth(840))
        assertEquals(NavigationMode.Drawer, NavigationMode.forWidth(1200))
    }

    @Test
    fun `isExpanded is true from Expanded upward`() {
        assertTrue(!Bp.Compact.isExpanded)
        assertTrue(Bp.Expanded.isExpanded)
        assertTrue(Bp.XL.isExpanded)
    }

    @Test
    fun `window state derives bp and navigation`() {
        val state = MullionWindowState.of(widthDp = 760, heightDp = 1024)
        assertEquals(Bp.Expanded, state.bp)
        assertEquals(NavigationMode.Rail, state.navigationMode)
        assertEquals(2, state.paneCount())
    }
}
