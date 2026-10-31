package dev.mullion.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MetricsTest {

    @Test
    fun `three resource buckets follow the 600 and 840 thresholds`() {
        assertEquals(ResourceBucket.Default, ResourceBucket.forWidth(375))
        assertEquals(ResourceBucket.W600, ResourceBucket.forWidth(600))
        assertEquals(ResourceBucket.W600, ResourceBucket.forWidth(760))
        assertEquals(ResourceBucket.W840, ResourceBucket.forWidth(840))
        assertEquals(ResourceBucket.W840, ResourceBucket.forWidth(1180))
    }

    @Test
    fun `section spacing steps 48 80 120`() {
        assertEquals(48, MullionMetrics.forWidth(375).sectionSpacingDp)
        assertEquals(80, MullionMetrics.forWidth(760).sectionSpacingDp)
        assertEquals(120, MullionMetrics.forWidth(1180).sectionSpacingDp)
    }

    @Test
    fun `display type steps 32 44 56`() {
        assertEquals(32, MullionMetrics.forWidth(375).displayTypeSp)
        assertEquals(44, MullionMetrics.forWidth(760).displayTypeSp)
        assertEquals(56, MullionMetrics.forWidth(1180).displayTypeSp)
    }

    @Test
    fun `pane gutter steps 12 24 24`() {
        assertEquals(12, MullionMetrics.forWidth(375).paneGutterDp)
        assertEquals(24, MullionMetrics.forWidth(760).paneGutterDp)
        assertEquals(24, MullionMetrics.forWidth(1180).paneGutterDp)
    }

    @Test
    fun `metrics resolve from a bucket without a context`() {
        assertEquals(MullionMetrics.Expanded, MullionMetrics.forBucket(ResourceBucket.W600))
        assertEquals(MullionMetrics.Large, MullionMetrics.forBucket(ResourceBucket.W840))
        assertNotEquals(MullionMetrics.Compact, MullionMetrics.Large)
    }

    @Test
    fun `type scale is a step function, not linear`() {
        assertEquals(TypeScale.Compact, TypeScale.forWidth(320))
        assertEquals(TypeScale.Compact, TypeScale.forWidth(479))
        assertEquals(TypeScale.Expanded.fontSizeSp, TypeScale.forWidth(600).fontSizeSp, 0.01f)
        assertEquals(17f, TypeScale.forWidth(700).fontSizeSp, 0.01f)
        assertEquals(19f, TypeScale.forWidth(840).fontSizeSp, 0.01f)
        assertEquals(62, TypeScale.forWidth(375).maxMeasureCh)
        assertEquals(72, TypeScale.forWidth(1180).maxMeasureCh)
    }

    @Test
    fun `insets fold the ime into the effective bottom`() {
        val insets = MullionInsets(leftDp = 2, topDp = 24, rightDp = 2, bottomDp = 16, imeDp = 300)
        assertEquals(4, insets.horizontalDp)
        assertEquals(340, insets.verticalDp)
        assertEquals(300, insets.effectiveBottomDp)
        assertEquals(300, insets.withIme(300).imeDp)
    }

    @Test
    fun `content size subtracts insets and never goes negative`() {
        val state = MullionWindowState(
            widthDp = 320,
            heightDp = 640,
            insets = MullionInsets(leftDp = 10, rightDp = 10, topDp = 24, bottomDp = 24),
        )
        assertEquals(300, state.contentWidthDp)
        assertEquals(592, state.contentHeightDp)
        val tiny = MullionWindowState(widthDp = 10, heightDp = 10, insets = MullionMetricsInset)
        assertEquals(0, tiny.contentWidthDp)
        assertEquals(0, tiny.contentHeightDp)
    }

    private val MullionMetricsInset = MullionInsets(leftDp = 20, rightDp = 20, topDp = 20, bottomDp = 20)
}
