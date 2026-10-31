package dev.mullion.compose.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.drawWithContent

/**
 * Glass depth. The deeper the layer, the stronger the fill and the border.
 *
 * Compose cannot sample a live backdrop before Android 12, so MULLION builds
 * glass from translucent gradients, a 1dp light edge and a dark ambient shadow.
 * The result is deterministic on API 21 and identical in previews.
 */
public enum class GlassLevel(
    public val radius: Dp,
    public val topAlpha: Float,
    public val midAlpha: Float,
    public val bottomAlpha: Float,
    public val borderAlpha: Float,
    public val elevation: Dp,
) {
    /** Small inline surfaces: chips, fields, segmented controls. */
    Chip(
        radius = 999.dp,
        topAlpha = 0.07f,
        midAlpha = 0.022f,
        bottomAlpha = 0.05f,
        borderAlpha = 0.13f,
        elevation = 0.dp,
    ),

    /** Cards and property sheets. */
    Panel(
        radius = 18.dp,
        topAlpha = 0.09f,
        midAlpha = 0.028f,
        bottomAlpha = 0.06f,
        borderAlpha = 0.16f,
        elevation = 10.dp,
    ),

    /** Dialogs, sheets and floating panels. */
    Floating(
        radius = 22.dp,
        topAlpha = 0.13f,
        midAlpha = 0.045f,
        bottomAlpha = 0.09f,
        borderAlpha = 0.22f,
        elevation = 26.dp,
    ),

    /** The outermost cabinet window. */
    Window(
        radius = 28.dp,
        topAlpha = 0.1f,
        midAlpha = 0.03f,
        bottomAlpha = 0.07f,
        borderAlpha = 0.18f,
        elevation = 32.dp,
    ),
}

/**
 * Draws a MULLION glass surface onto any modifier chain.
 *
 * The modifier clips, fills with a three-stop gradient, draws the 1dp edge and
 * adds a 1dp highlight along the top edge. It does not impose a size, so it can
 * be used by fields, cards, buttons and sheets alike.
 */
public fun Modifier.glass(
    shape: Shape = RoundedCornerShape(GlassLevel.Panel.radius),
    level: GlassLevel = GlassLevel.Panel,
    borderColor: Color = Color.White.copy(alpha = level.borderAlpha),
): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = level.topAlpha),
                Color.White.copy(alpha = level.midAlpha),
                Color.White.copy(alpha = level.bottomAlpha),
            ),
        ),
    )
    .border(1.dp, borderColor, shape)
    .drawWithContent {
        drawContent()
        // A single hairline along the top edge sells the glass.
        drawLine(
            color = Color.White.copy(alpha = 0.22f),
            start = androidx.compose.ui.geometry.Offset(shapePadding(), 0f),
            end = androidx.compose.ui.geometry.Offset(size.width - shapePadding(), 0f),
            strokeWidth = 1f,
        )
    }

private fun shapePadding(): Float = 0f

/**
 * A composable glass container.
 *
 * @param level depth of the glass.
 * @param shape corner shape; defaults to the level's radius.
 * @param contentAlignment alignment used by callers that add a single child.
 */
@Composable
public fun GlassSurface(
    modifier: Modifier = Modifier,
    level: GlassLevel = GlassLevel.Panel,
    shape: Shape = RoundedCornerShape(level.radius),
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.glass(shape = shape, level = level),
        contentAlignment = contentAlignment,
        content = content,
    )
}

/** The default rounded shape for a glass level. */
public fun GlassLevel.shape(): Shape = RoundedCornerShape(radius)
