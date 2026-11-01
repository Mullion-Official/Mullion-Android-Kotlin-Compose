package dev.mullion.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaneStrategyTest {

    private val slots = listOf(PaneSlot.List, PaneSlot.Detail, PaneSlot.Preview)

    @Test
    fun `cabinet defaults map 1 1 2 3 3`() {
        val strategy = PaneStrategy()
        assertEquals(1, strategy.countFor(Bp.Compact))
        assertEquals(1, strategy.countFor(Bp.Medium))
        assertEquals(2, strategy.countFor(Bp.Expanded))
        assertEquals(3, strategy.countFor(Bp.Large))
        assertEquals(3, strategy.countFor(Bp.XL))
    }

    @Test
    fun `1180dp resolves to three panes`() {
        assertEquals(3, PaneStrategy().countForWidth(1180))
        val plan = PaneStrategy().planForWidth(1180, slots)
        assertEquals(3, plan.count)
        assertTrue(plan.shows(PaneSlot.Preview))
    }

    @Test
    fun `375dp collapses to the list only`() {
        val plan = PaneStrategy().planForWidth(375, slots)
        assertEquals(listOf(PaneSlot.List), plan.slots)
        assertTrue(plan.isSinglePane)
        assertFalse(plan.shows(PaneSlot.Detail))
    }

    @Test
    fun `maxPanes caps a wide strategy at two`() {
        val strategy = PaneStrategy.TWO_PANE
        assertEquals(2, strategy.countFor(Bp.XL))
        assertEquals(2, strategy.plan(Bp.Large, slots).count)
    }

    @Test
    fun `single pane strategy keeps one slot at every width`() {
        Bp.entries.forEach { bp ->
            assertEquals(1, PaneStrategy.SINGLE_PANE.countFor(bp))
        }
    }

    @Test
    fun `fewer slots than allowed shows all supplied slots`() {
        val plan = PaneStrategy().plan(Bp.XL, listOf(PaneSlot.List, PaneSlot.Detail))
        assertEquals(2, plan.count)
    }

    @Test
    fun `custom counts survive construction`() {
        val strategy = PaneStrategy(
            counts = mapOf(Bp.Compact to 1, Bp.Expanded to 3),
            maxPanes = 3,
        )
        assertEquals(3, strategy.countFor(Bp.Expanded))
        assertEquals(1, strategy.countFor(Bp.Large))
    }

    @Test
    fun `gutter is carried into the plan`() {
        assertEquals(MullionMetrics.Expanded.paneGutterDp, PaneStrategy().plan(Bp.Expanded, slots).gutterDp)
        assertEquals(MullionMetrics.Compact.paneGutterDp, PaneStrategy().plan(Bp.Compact, slots).gutterDp)
        assertEquals(12, PaneStrategy(gutterDp = 12).plan(Bp.Expanded, slots).gutterDp)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero max panes is rejected`() {
        PaneStrategy(maxPanes = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty slot list is rejected`() {
        PaneStrategy().plan(Bp.Expanded, emptyList())
    }

    @Test
    fun `pane gutter follows the width class unless overridden`() {
        val slots = listOf(PaneSlot.List, PaneSlot.Detail, PaneSlot.Preview)
        assertEquals(
            MullionMetrics.Compact.paneGutterDp,
            PaneStrategy().plan(Bp.Compact, slots).gutterDp,
        )
        assertEquals(
            MullionMetrics.Large.paneGutterDp,
            PaneStrategy().plan(Bp.Large, slots).gutterDp,
        )
    }

    @Test
    fun `semantic viewports carry their own context`() {
        assertEquals(Bp.Compact, MullionViewport.Phone.bp)
        assertEquals(Bp.Expanded, MullionViewport.Fold.bp)
        assertEquals(3, PaneStrategy().countFor(MullionViewport.Desktop.bp))
        assertTrue(MullionViewport.Phone.metrics.paneGutterDp < MullionViewport.Desktop.metrics.paneGutterDp)
    }
}
