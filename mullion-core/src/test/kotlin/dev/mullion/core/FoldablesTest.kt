package dev.mullion.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FoldablesTest {

    @Test
    fun `no hinge splits into two equal panes with no dead seam`() {
        val split = Foldables.splitVertically(800f, HingeInfo.None)
        assertEquals(800f, split.totalWidthDp, 0.01f)
        assertEquals(400f, split.paneWidthDp, 0.01f)
        assertEquals(0f, split.hingeWidthDp, 0.01f)
        assertEquals(400f, split.endPaneStartDp, 0.01f)
        assertEquals(800f, split.endPaneEndDp, 0.01f)
    }

    @Test
    fun `vertical hinge reserves its width and a safe gap on both sides`() {
        val hinge = HingeInfo.vertical(separatorWidthDp = 20f)
        val split = Foldables.splitVertically(800f, hinge, safeGapDp = 24f)
        assertEquals(20f, split.hingeWidthDp, 0.01f)
        assertEquals(24f, split.gutterDp, 0.01f)
        assertEquals(366f, split.paneWidthDp, 0.01f) // (800 - 20 - 48) / 2
        assertEquals(366f, split.startPaneEndDp, 0.01f)
        assertEquals(390f, split.hingeStartDp, 0.01f)
        assertEquals(410f, split.hingeEndDp, 0.01f)
        assertEquals(434f, split.endPaneStartDp, 0.01f)
    }

    @Test
    fun `flat foldable is continuous even when a hinge is reported`() {
        val hinge = HingeInfo.vertical(separatorWidthDp = 20f, posture = HingePosture.Flat)
        assertFalse(hinge.isSeparating)
        val split = Foldables.splitVertically(800f, hinge)
        assertEquals(400f, split.paneWidthDp, 0.01f)
        assertEquals(0f, split.hingeWidthDp, 0.01f)
    }

    @Test
    fun `horizontal hinge does not split columns`() {
        val hinge = HingeInfo.horizontal(separatorHeightDp = 40f)
        assertFalse(hinge.isVerticalSeparator)
        val split = Foldables.splitVertically(800f, hinge)
        assertEquals(400f, split.paneWidthDp, 0.01f)
    }

    @Test
    fun `side sheet only docks when a vertical seam exists`() {
        assertNull(Foldables.sideSheetWidth(800f, HingeInfo.None))
        assertNull(Foldables.sideSheetWidth(800f, HingeInfo.horizontal()))
        assertNull(Foldables.sideSheetWidth(800f, HingeInfo.vertical(20f)))
        assertNotNull(Foldables.sideSheetWidth(1200f, HingeInfo.vertical(20f)))
    }

    @Test
    fun `side sheet respects a minimum pane width`() {
        val hinge = HingeInfo.vertical(20f)
        assertNull(
            Foldables.sideSheetWidth(
                availableWidthDp = 600f,
                hinge = hinge,
                minPaneDp = 400f,
            ),
        )
        assertNotNull(
            Foldables.sideSheetWidth(
                availableWidthDp = 1200f,
                hinge = hinge,
                minPaneDp = 240f,
            ),
        )
    }

    @Test
    fun `canSplit rejects a hinge that leaves too little room`() {
        val wideHinge = HingeInfo.vertical(400f)
        assertTrue(Foldables.canSplit(1200f, HingeInfo.vertical(20f), minPaneDp = 280f))
        assertFalse(Foldables.canSplit(700f, wideHinge, minPaneDp = 280f))
    }

    @Test
    fun `posture helper preserves geometry`() {
        val tent = HingeInfo.vertical(20f).withPosture(HingePosture.Tent)
        assertEquals(HingePosture.Tent, tent.posture)
        assertEquals(20f, tent.separatorWidthDp, 0.01f)
        assertTrue(tent.isSeparating)
    }
}
