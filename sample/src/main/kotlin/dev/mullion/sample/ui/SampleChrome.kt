package dev.mullion.sample.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionMotion
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.Bp
import dev.mullion.core.MullionViewport

/** A quiet, theme-aware wash behind the cabinet. No neon, no blueprint. */
@Composable
fun SampleBackdrop(modifier: Modifier = Modifier) {
    val background = MullionColors.Background
    val tint = MullionColors.Volt
    val grid = MullionColors.Ink.copy(alpha = 0.025f)
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(background)
        drawRect(
            Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.07f), Color.Transparent),
                center = Offset(size.width * 0.18f, 0f),
                radius = size.width * 0.9f,
            ),
        )
        drawRect(
            Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.035f), Color.Transparent),
                center = Offset(size.width * 0.9f, size.height * 0.35f),
                radius = size.width * 0.7f,
            ),
        )

        val step = 56.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = grid,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f,
            )
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = grid,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
            y += step
        }
    }
}

/** Centers page content at the cabinet's 1320dp maximum. */
@Composable
fun PageSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = modifier
                .widthIn(max = 1320.dp)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            content = content,
        )
    }
}

/** A small monospace label. */
@Composable
fun Lab(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MullionColors.Faint,
    size: Int = 10,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MullionTextStyles.Label.copy(fontSize = size.sp),
        color = color,
    )
}

/** Body copy in the sample's voice. */
@Composable
fun SampleBody(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MullionColors.Muted,
    size: Int = 15,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MullionTextStyles.Body.copy(fontSize = size.sp),
        color = color,
    )
}

/** A glass chip that can be static or selectable. */
@Composable
fun SampleChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (selected) MullionColors.Volt.copy(alpha = 0.16f) else MullionColors.Tonal,
                CircleShape,
            )
            .border(
                width = 1.dp,
                color = if (selected) {
                    MullionColors.Volt.copy(alpha = 0.7f)
                } else {
                    MullionColors.Edge
                },
                shape = CircleShape,
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 11.dp, vertical = 5.dp),
    ) {
        Text(
            text = text,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
            color = if (selected) MullionColors.Volt else MullionColors.Muted,
            maxLines = 1,
        )
    }
}

/** One of the three cabinet widths. */
@Composable
fun WidthToggle(
    width: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MullionColors.Tonal, CircleShape)
            .border(1.dp, MullionColors.Edge, CircleShape)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        MullionViewport.entries.forEach { option ->
            val selected = option.widthDp == width
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            Brush.verticalGradient(
                                listOf(MullionColors.VoltHigh, MullionColors.Volt),
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Transparent),
                            )
                        },
                        CircleShape,
                    )
                    .clickable { onChange(option.widthDp) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = option.widthDp.toString(),
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                    color = if (selected) MullionColors.OnVolt else MullionColors.Muted,
                )
            }
        }
    }
}

/** A large section heading with a numbered kicker. */
@Composable
fun SectionHeader(
    kicker: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Lab(kicker, color = MullionColors.Volt)
            Spacer(Modifier.weight(1f))
            if (trailing != null) trailing()
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
            color = MullionColors.Ink,
        )
        Spacer(Modifier.height(6.dp))
        SampleBody(text = description, size = 14)
    }
}

/**
 * The cabinet's mark, drawn in a 160×160 viewport.
 *
 * It is a window: outer frame, one strong mullion, two smaller transoms, one
 * volt pane and four corner brackets.
 */
@Composable
fun BrandMark(
    modifier: Modifier = Modifier,
    size: Dp = 152.dp,
    intensity: Float = 1f,
) {
    val ink = MullionColors.Ink
    val volt = MullionColors.Volt
    val voltHigh = MullionColors.VoltHigh
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension / 160f
        fun p(value: Float): Float = value * s

        // Frame fill
        drawRoundRect(
            color = ink.copy(alpha = 0.05f * intensity),
            topLeft = Offset(p(18f), p(18f)),
            size = Size(p(124f), p(124f)),
            cornerRadius = CornerRadius(p(30f), p(30f)),
        )
        drawRoundRect(
            color = ink.copy(alpha = 0.22f * intensity),
            topLeft = Offset(p(18f), p(18f)),
            size = Size(p(124f), p(124f)),
            cornerRadius = CornerRadius(p(30f), p(30f)),
            style = Stroke(width = p(1.5f)),
        )

        // Transoms
        drawLine(
            color = ink.copy(alpha = 0.30f * intensity),
            start = Offset(p(18f), p(62f)),
            end = Offset(p(80f), p(62f)),
            strokeWidth = p(1.5f),
        )
        drawLine(
            color = ink.copy(alpha = 0.30f * intensity),
            start = Offset(p(80f), p(100f)),
            end = Offset(p(142f), p(100f)),
            strokeWidth = p(1.5f),
        )

        // Mullion glow + line
        drawLine(
            color = volt.copy(alpha = 0.16f * intensity),
            start = Offset(p(80f), p(18f)),
            end = Offset(p(80f), p(142f)),
            strokeWidth = p(11f),
            cap = StrokeCap.Round,
        )
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(voltHigh, volt),
                startY = p(18f),
                endY = p(142f),
            ),
            start = Offset(p(80f), p(18f)),
            end = Offset(p(80f), p(142f)),
            strokeWidth = p(3f),
            cap = StrokeCap.Round,
        )

        // Lit pane
        drawRoundRect(
            color = volt.copy(alpha = 0.14f * intensity),
            topLeft = Offset(p(27f), p(71f)),
            size = Size(p(44f), p(61f)),
            cornerRadius = CornerRadius(p(10f), p(10f)),
        )
        drawRoundRect(
            color = volt.copy(alpha = 0.5f * intensity),
            topLeft = Offset(p(27f), p(71f)),
            size = Size(p(44f), p(61f)),
            cornerRadius = CornerRadius(p(10f), p(10f)),
            style = Stroke(width = p(1f)),
        )

        // Corner brackets
        val bracketColor = ink.copy(alpha = 0.32f * intensity)
        fun bracket(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
            val path = Path().apply {
                moveTo(p(x1), p(y1))
                lineTo(p(x2), p(y2))
                lineTo(p(x3), p(y3))
            }
            drawPath(
                path = path,
                color = bracketColor,
                style = Stroke(width = p(1.5f), cap = StrokeCap.Round),
            )
        }
        bracket(6f, 38f, 6f, 6f, 38f, 6f)
        bracket(122f, 6f, 154f, 6f, 154f, 38f)
        bracket(154f, 122f, 154f, 154f, 122f, 154f)
        bracket(38f, 154f, 6f, 154f, 6f, 122f)
    }
}

/** The floating top bar. */
@Composable
fun SampleTopBar(
    onAdaptive: () -> Unit,
    onBase: () -> Unit,
    onIndex: () -> Unit,
    onGithub: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bp = mullionWindow().bp
    Row(
        modifier = modifier
            .fillMaxWidth()
            .glass(shape = CircleShape, level = GlassLevel.Floating)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrandMark(size = 26.dp, intensity = 0.9f)
        Spacer(Modifier.width(10.dp))
        Text(
            text = "MULLION",
            style = MullionTextStyles.Mono.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            ),
            color = MullionColors.Ink,
        )
        if (!bp.isCompact) {
            Spacer(Modifier.width(14.dp))
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(MullionColors.Mint, CircleShape)
                    .border(1.dp, MullionColors.Mint.copy(alpha = 0.5f), CircleShape),
            )
            Spacer(Modifier.width(7.dp))
            Text(
                text = "26 / 26 ONLINE",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Muted,
            )
        }
        Spacer(Modifier.weight(1f))
        if (!bp.isCompact) {
            TopBarLink("ADAPTIVE 14", onAdaptive)
            TopBarLink("BASE 12", onBase)
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MullionColors.Tonal, CircleShape)
                .border(1.dp, MullionColors.Edge, CircleShape)
                .clickable(onClick = onIndex)
                .padding(horizontal = 9.dp, vertical = 7.dp),
        ) {
            Text(
                text = "☰",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 11.sp),
                color = MullionColors.Ink,
            )
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(MullionColors.VoltHigh, MullionColors.Volt),
                    ),
                    CircleShape,
                )
                .clickable(onClick = onGithub)
                .padding(horizontal = 13.dp, vertical = 7.dp),
        ) {
            Text(
                text = "GITHUB ↗",
                style = MullionTextStyles.MonoSmall.copy(
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MullionColors.OnVolt,
            )
        }
    }
}

@Composable
private fun TopBarLink(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
            color = MullionColors.Muted,
        )
    }
}

/** A four-up statistic strip that becomes 2×2 on phones. */
@Composable
fun StatStrip(modifier: Modifier = Modifier) {
    val bp = mullionWindow().bp
    val stats = listOf(
        Stat("Modules", "26", "Adaptive 14 + Base 12"),
        Stat("Breakpoints", "5", "320 / 480 / 600 / 840 / 1200 dp"),
        Stat("Live demos", "100%", "真实 Compose 渲染 · 可交互"),
        Stat("Baseline", "21", "minSdk · 编译期零反射"),
    )
    if (bp.isCompact) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            stats.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { stat ->
                        StatCard(stat, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    } else {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            stats.forEach { stat ->
                StatCard(stat, modifier = Modifier.weight(1f))
            }
        }
    }
}

private data class Stat(val label: String, val value: String, val caption: String)

@Composable
private fun StatCard(stat: Stat, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .glass(shape = RoundedCornerShape(18.dp), level = GlassLevel.Panel)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Lab(stat.label)
        Spacer(Modifier.height(8.dp))
        Text(
            text = stat.value,
            style = MullionTextStyles.Readout.copy(fontSize = 24.sp),
            color = MullionColors.Ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stat.caption,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
            color = MullionColors.Faint,
        )
    }
}

/** A properties table used under every component tray. */
@Composable
fun PropsCard(
    props: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .glass(shape = RoundedCornerShape(18.dp), level = GlassLevel.Panel)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Lab("Properties / 属性")
        Spacer(Modifier.height(8.dp))
        props.forEachIndexed { index, (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 32.dp)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = key,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
                    color = MullionColors.Faint,
                    modifier = Modifier.width(112.dp),
                    maxLines = 2,
                )
                Text(
                    text = value,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
                    color = MullionColors.Ink,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                )
            }
            if (index != props.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MullionColors.Edge.copy(alpha = 0.5f)),
                )
            }
        }
    }
}

/** A copyable code block with line numbers. */
@Composable
fun CodeCard(
    file: String,
    code: String,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val lines = code.trim('\n').lines()

    Column(
        modifier = modifier
            .glass(shape = RoundedCornerShape(18.dp), level = GlassLevel.Panel),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Lab(file, size = 9)
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        clipboard.setText(AnnotatedString(code))
                        copied = true
                    }
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = if (copied) "COPIED ✓" else "COPY ⧉",
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                    color = if (copied) MullionColors.Mint else MullionColors.Muted,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MullionColors.Edge.copy(alpha = 0.5f)),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
        ) {
            Column(modifier = Modifier.padding(end = 14.dp)) {
                lines.indices.forEach { index ->
                    Text(
                        text = (index + 1).toString(),
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                        color = MullionColors.Faint.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 14.dp),
                    )
                }
            }
            Column {
                lines.forEach { line ->
                    val color = when {
                        line.trimStart().startsWith("//") -> MullionColors.Faint
                        line.trimStart().startsWith("@") -> MullionColors.Amber
                        else -> MullionColors.Ink
                    }
                    Text(
                        text = line.ifEmpty { " " },
                        style = MullionTextStyles.Mono.copy(fontSize = 10.5.sp, lineHeight = 18.sp),
                        color = color,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }
    }
}

/** The demo stage background: a dark plate with a blueprint grid. */
@Composable
fun DemoCanvas(
    height: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(MullionColors.Background.copy(alpha = 0.55f))
            .border(1.dp, MullionColors.Edge.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .demoGrid()
            .padding(12.dp),
        content = content,
    )
}

private fun Modifier.demoGrid(): Modifier = this.then(
    Modifier.background(
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
    ),
)

/** A bordered pane used inside demos. */
@Composable
fun DemoPane(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MullionColors.Ink.copy(alpha = 0.045f))
            .border(1.dp, MullionColors.Ink.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        if (title != null) {
            Lab(title, size = 9)
            Spacer(Modifier.height(8.dp))
        }
        content()
    }
}

/** A placeholder text bar, the cabinet's lingua franca for content. */
@Composable
fun DemoLine(
    fraction: Float,
    modifier: Modifier = Modifier,
    color: Color = MullionColors.Ink.copy(alpha = 0.16f),
    height: Dp = 6.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(fraction)
            .height(height)
            .clip(CircleShape)
            .background(color),
    )
}

/** A row of placeholder bars. */
@Composable
fun DemoLines(
    fractions: List<Float> = listOf(0.9f, 0.68f, 0.44f, 0.74f),
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(7.dp)) {
        fractions.forEach { fraction ->
            DemoLine(fraction)
        }
    }
}

/** A page-level counter used in the hero. */
@Composable
fun CounterRow(modifier: Modifier = Modifier) {
    val bp = mullionWindow().bp
    val stats = listOf(
        "14" to "adaptive",
        "12" to "base",
        "5" to "width classes",
        "21" to "minSdk",
    )
    if (bp.isCompact) {
        Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            stats.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { (value, label) -> CounterChip(value, label) }
                }
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            stats.forEach { (value, label) -> CounterChip(value, label) }
        }
    }
}


@Composable
private fun RowScope.CounterChip(value: String, label: String) {
    Row(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .glass(shape = RoundedCornerShape(12.dp), level = GlassLevel.Chip)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = value,
            style = MullionTextStyles.Mono.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = MullionColors.Volt,
        )
        Text(
            text = label,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
            color = MullionColors.Faint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** A segmented, read-only breakpoint indicator. */
@Composable
fun BreakpointBar(modifier: Modifier = Modifier) {
    val window = mullionWindow()
    val classes = Bp.entries
    Box(
        modifier = modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(18.dp), level = GlassLevel.Panel)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Lab("Window", color = MullionColors.Volt)
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${window.widthDp} dp · ${window.bp.label} · ${window.navigationMode.name}",
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
                    color = MullionColors.Amber,
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                classes.forEach { bp ->
                    val active = bp == window.bp
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (active) {
                                    Brush.horizontalGradient(
                                        listOf(MullionColors.VoltHigh, MullionColors.Volt),
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        listOf(
                                            MullionColors.Ink.copy(alpha = 0.1f),
                                            MullionColors.Ink.copy(alpha = 0.1f),
                                        ),
                                    )
                                },
                            ),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                classes.forEach { bp ->
                    Text(
                        text = bp.label.take(1),
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                        color = if (bp == window.bp) MullionColors.Volt else MullionColors.Faint,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "hinge ${window.hinge.posture.name.uppercase()} · " +
                    (window.hinge.orientation?.name?.uppercase() ?: "NO SEAM") +
                    " · " + window.hinge.separatorWidthDp.toInt() + "dp",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                color = if (window.hinge.isSeparating) MullionColors.Signal else MullionColors.Faint,
            )
        }
    }
}

/** Empty helper used to keep a Row's weight layout balanced. */
@Composable
fun RowScope.Filler() {
    Spacer(Modifier.weight(1f))
}

/** A page-level glass panel with padding. */
@Composable
fun PanelBox(
    modifier: Modifier = Modifier,
    level: GlassLevel = GlassLevel.Panel,
    contentPadding: PaddingValues = PaddingValues(22.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.glass(shape = RoundedCornerShape(level.radius), level = level),
        verticalArrangement = Arrangement.Top,
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

/** A dismissible info strip. */
@Composable
fun NoteCard(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = MullionColors.Signal,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.08f))
            .border(1.dp, accent.copy(alpha = 0.32f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(accent, CircleShape),
        )
        Text(
            text = text,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
            color = accent,
        )
    }
}

/** Shared spacing rhythm. */
val SectionGap: Dp = 26.dp
val CardGap: Dp = 16.dp
val Motion: Int = MullionMotion.PaneMillis
