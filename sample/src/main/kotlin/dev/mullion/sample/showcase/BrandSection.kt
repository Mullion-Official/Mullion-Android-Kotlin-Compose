package dev.mullion.sample.showcase

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.base.MullionSlider
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionFonts
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.Bp
import dev.mullion.core.MullionMetrics
import dev.mullion.core.NavigationMode
import dev.mullion.core.PaneStrategy
import dev.mullion.core.ResourceBucket
import dev.mullion.core.TypeScale
import dev.mullion.sample.ui.BrandMark
import dev.mullion.sample.ui.CounterRow
import dev.mullion.sample.ui.Lab
import dev.mullion.sample.ui.PageSection
import dev.mullion.sample.ui.SampleBody
import dev.mullion.sample.ui.SampleChip
import dev.mullion.sample.ui.StatStrip
import kotlin.math.roundToInt

/** The naming/hero block. */
@Composable
fun HeroSection() {
    val bp = mullionWindow().bp
    val compact = bp.isCompact

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(28.dp), level = GlassLevel.Window)
            .padding(if (compact) 20.dp else 30.dp),
    ) {
        if (compact) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BrandMark(size = 112.dp)
                Spacer(Modifier.height(8.dp))
                Lab("Logo / 窗棂标")
            }
            Spacer(Modifier.height(20.dp))
        }

        val textBlock: @Composable () -> Unit = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Lab("Open-source naming / 开源命名", color = MullionColors.Volt)
                SampleChip("v1.0.0 · Apache-2.0")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "MULLION",
                style = TextStyle(
                    fontFamily = MullionFonts.Sans,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 42.sp else 60.sp,
                    letterSpacing = (-1.5).sp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MullionColors.Ink,
                            MullionColors.VoltHigh,
                            MullionColors.Volt,
                        ),
                    ),
                ),
            )
            Text(
                text = "/ˈmʌl.jən/ · 窗棂 · 分格梃 · One window. Every pane.",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp),
                color = MullionColors.Faint,
                modifier = Modifier.padding(top = 4.dp),
            )
            Spacer(Modifier.height(14.dp))
            SampleBody(
                text = "一樘窗如何分格，由窗棂决定。Mullion 是把一扇大窗分成若干玻璃格的结构件——它不发光、不抢眼，却决定了窗格如何排布、如何收束、如何避让铰链。组件库之于应用界面，正是这根窗棂：320dp 到 1200dp 之间，一切分格由它定。",
                size = 15,
                color = MullionColors.Muted,
            )
            Spacer(Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("core", "compose", "view", "material3").forEach {
                    SampleChip("dev.mullion:$it")
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .background(
                        MullionColors.Background.copy(alpha = 0.45f),
                        RoundedCornerShape(12.dp),
                    )
                    .border(1.dp, MullionColors.Edge.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 13.dp, vertical = 9.dp),
            ) {
                Text(
                    text = "implementation(\"dev.mullion:core:1.0.0\")",
                    style = MullionTextStyles.Mono.copy(fontSize = 9.5.sp),
                    color = MullionColors.Mint,
                    softWrap = false,
                    maxLines = 1,
                )
            }
        }

        if (compact) {
            textBlock()
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BrandMark(size = 152.dp)
                    Spacer(Modifier.height(10.dp))
                    Lab("Logo / 窗棂标")
                }
                Spacer(Modifier.width(34.dp))
                Column(modifier = Modifier.weight(1f)) {
                    textBlock()
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        CounterRow()
    }
}

/** The big claim under the hero. */
@Composable
fun ClaimSection() {
    val bp = mullionWindow().bp
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "26 个组件，各自封在一块会呼吸的玻璃里。",
            style = TextStyle(
                fontFamily = MullionFonts.Sans,
                fontWeight = FontWeight.Black,
                fontSize = if (bp.isCompact) 24.sp else 34.sp,
                lineHeight = if (bp.isCompact) 32.sp else 44.sp,
                letterSpacing = (-0.6).sp,
                brush = Brush.linearGradient(
                    listOf(
                        MullionColors.Ink,
                        MullionColors.VoltHigh,
                        MullionColors.Volt,
                    ),
                ),
            ),
        )
        Text(
            text = "14 个自适应件管「怎么分格」，12 个基础件管「格里长什么样」。",
            style = MullionTextStyles.Body.copy(
                fontSize = if (bp.isCompact) 14.sp else 17.sp,
            ),
            color = MullionColors.Muted,
            modifier = Modifier.padding(top = 8.dp),
        )
        Spacer(Modifier.height(18.dp))
        StatStrip()
    }
}

/** Candidate-name table + how-to-use card. */
@Composable
fun NamingAndUsage() {
    val bp = mullionWindow().bp
    val naming: @Composable () -> Unit = { NamingCard() }
    val usage: @Composable () -> Unit = { UsageCard() }

    if (bp.isCompact) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            naming()
            usage()
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.weight(1.12f)) { naming() }
            Box(Modifier.weight(0.88f)) { usage() }
        }
    }
}

@Composable
private fun NamingCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(18.dp), level = GlassLevel.Panel)
            .padding(20.dp),
    ) {
        Lab("Candidates / 命名对照")
        Spacer(Modifier.height(12.dp))
        val headers = listOf("候选" to 1.1f, "词义" to 1.1f, "取舍" to 1.9f)
        Row(modifier = Modifier.fillMaxWidth()) {
            headers.forEach { (header, weight) ->
                Text(
                    text = header,
                    style = MullionTextStyles.LabelSmall,
                    color = MullionColors.Faint,
                    modifier = Modifier.weight(weight),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        val rows = listOf(
            Triple("Mullion ✓", "窗棂 / 分格梃", "结构决定排布 · 选定"),
            Triple("Oriel", "凸窗（三面采光）", "形态优雅 · 语义偏软"),
            Triple("Casement", "平开窗（铰链开合）", "直指折叠屏 · 范围偏窄"),
            Triple("Haze", "磨砂 / 玻璃雾面", "呼应材质 · 缺结构义"),
        )
        rows.forEachIndexed { index, (name, meaning, choice) ->
            val picked = index == 0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
            ) {
                Text(
                    text = name,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp),
                    color = if (picked) MullionColors.Volt else MullionColors.Ink,
                    modifier = Modifier.weight(1.1f),
                )
                Text(
                    text = meaning,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp),
                    color = if (picked) MullionColors.Volt else MullionColors.Muted,
                    modifier = Modifier.weight(1.1f),
                )
                Text(
                    text = choice,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                    color = if (picked) MullionColors.Volt else MullionColors.Faint,
                    modifier = Modifier.weight(1.9f),
                )
            }
            if (index != rows.lastIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MullionColors.Edge.copy(alpha = 0.5f)),
                )
            }
        }
    }
}

@Composable
private fun UsageCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(18.dp), level = GlassLevel.Panel)
            .padding(20.dp),
    ) {
        Lab("Usage / 实测指引")
        Spacer(Modifier.height(12.dp))
        listOf(
            "点击 375 / 760 / 1180 改变模拟窗口宽度",
            "控件可直接交互：输入、拖动、开合、切换",
            "zoom 读数为真实渲染缩放比，非截图",
            "所有断点判定与 Compose / View API 一一对应",
        ).forEachIndexed { index, line ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "%02d".format(index + 1),
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                    color = MullionColors.Volt,
                    modifier = Modifier.width(30.dp),
                )
                Text(
                    text = line,
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp),
                    color = MullionColors.Muted,
                )
            }
        }
    }
}

/** The five-class rail, with a live marker for the actual window. */
@Composable
fun BreakpointRail() {
    val window = mullionWindow()
    val current = window.widthDp.coerceIn(320, 1200)
    val fraction = (current - 320) / 880f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(18.dp), level = GlassLevel.Panel)
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Lab("Breakpoint rail / 断点轨", color = MullionColors.Volt)
            Spacer(Modifier.weight(1f))
            Text(
                text = "Bp ${window.widthDp} · ${window.bp.label}",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                color = MullionColors.Amber,
            )
        }
        Spacer(Modifier.height(8.dp))

        val edgeHigh = MullionColors.EdgeHigh
        val volt = MullionColors.Volt
        val signal = MullionColors.Signal
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(64.dp)) {
            val track = maxWidth - 20.dp
            Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                val y = size.height - 8.dp.toPx()
                drawLine(
                    color = edgeHigh,
                    start = Offset(10.dp.toPx(), y),
                    end = Offset(size.width - 10.dp.toPx(), y),
                    strokeWidth = 1.dp.toPx(),
                )
                Bp.DESIGN_TICKS.forEach { tick ->
                    val x = 10.dp.toPx() + (size.width - 20.dp.toPx()) *
                        ((tick - 320) / 880f)
                    val active = tick == window.bp.baselineWidthDp
                    drawLine(
                        color = if (active) volt else signal,
                        start = Offset(x, y - 7.dp.toPx()),
                        end = Offset(x, y + 4.dp.toPx()),
                        strokeWidth = if (active) 2.dp.toPx() else 1.dp.toPx(),
                    )
                }
                val markerX = 10.dp.toPx() + (size.width - 20.dp.toPx()) * fraction
                drawCircle(
                    color = volt,
                    radius = 4.dp.toPx(),
                    center = Offset(markerX, y),
                )
                drawCircle(
                    color = volt.copy(alpha = 0.2f),
                    radius = 9.dp.toPx(),
                    center = Offset(markerX, y),
                )
            }
            Bp.DESIGN_TICKS.forEach { tick ->
                val tickFraction = (tick - 320) / 880f
                val x = (track * tickFraction).coerceIn(0.dp, track - 34.dp)
                Column(
                    modifier = Modifier
                        .offset(x = x + 10.dp, y = 40.dp)
                        .width(34.dp),
                ) {
                    Text(
                        text = tick.toString(),
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                        color = if (tick == window.bp.baselineWidthDp) {
                            MullionColors.Volt
                        } else {
                            MullionColors.Faint
                        },
                    )
                    Text(
                        text = Bp.of(tick).label.take(3).uppercase(),
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 7.5.sp),
                        color = MullionColors.Faint,
                    )
                }
            }
        }
    }
}

/**
 * A live breakpoint lab: drag a simulated width and watch class, navigation,
 * pane count, bucket and type step move together.
 */
@Composable
fun BreakpointLab() {
    var width by rememberSaveable { mutableIntStateOf(Bp.Large.baselineWidthDp) }
    val bp = Bp.of(width)
    val nav = NavigationMode.forBp(bp)
    val panes = PaneStrategy().countFor(bp)
    val bucket = ResourceBucket.forBp(bp)
    val type = TypeScale.forBp(bp)
    val metrics = MullionMetrics.forBp(bp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(22.dp), level = GlassLevel.Window)
            .padding(22.dp),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Lab("Breakpoint lab / 断点实验室", color = MullionColors.Volt)
            SampleChip("${width}dp · ${bp.label}")
        }
        Spacer(Modifier.height(10.dp))
        SampleBody(
            "拖动游标。下面每一个数字都由 mullion-core 的同一份判定函数给出，和运行时没有任何旁路。",
            size = 13,
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Bp.entries.forEach { option ->
                val active = option == bp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(
                            color = if (active) {
                                MullionColors.Volt.copy(alpha = 0.15f)
                            } else {
                                MullionColors.Ink.copy(alpha = 0.04f)
                            },
                            shape = RoundedCornerShape(10.dp),
                        )
                        .border(
                            width = 1.dp,
                            color = if (active) {
                                MullionColors.Volt.copy(alpha = 0.65f)
                            } else {
                                MullionColors.Edge.copy(alpha = 0.6f)
                            },
                            shape = RoundedCornerShape(10.dp),
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = option.label.take(1),
                            style = MullionTextStyles.MonoSmall.copy(fontSize = 11.sp),
                            color = if (active) MullionColors.Volt else MullionColors.Muted,
                        )
                        Text(
                            text = option.baselineWidthDp.toString(),
                            style = MullionTextStyles.MonoSmall.copy(fontSize = 7.sp),
                            color = MullionColors.Faint,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        MullionSlider(
            value = width.toFloat(),
            onValueChange = { width = it.roundToInt().coerceIn(320, 1200) },
            valueRange = 320f..1200f,
            label = "Simulated width",
            valueLabel = { "${it.roundToInt()} dp" },
        )

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LabReadout("Class", bp.label, Modifier.weight(1f))
            LabReadout("Navigation", nav.name, Modifier.weight(1f))
            LabReadout("Panes", "$panes", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LabReadout("Bucket", bucket.qualifier, Modifier.weight(1.4f))
            LabReadout(
                "Section / Type",
                "${metrics.sectionSpacingDp}dp / ${type.fontSizeSp.toInt()}sp",
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun LabReadout(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MullionColors.Ink.copy(alpha = 0.04f), RoundedCornerShape(10.dp))
            .border(1.dp, MullionColors.Edge.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Lab(label, size = 8)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp),
            color = MullionColors.Ink,
        )
    }
}

/** Footer: brand, license and the closing line. */
@Composable
fun FooterSection() {
    val bp = mullionWindow().bp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(28.dp), level = GlassLevel.Window)
            .padding(if (bp.isCompact) 24.dp else 36.dp),
    ) {
        if (bp.isCompact) {
            BrandMark(size = 72.dp)
            Spacer(Modifier.height(14.dp))
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandMark(size = 86.dp)
                Spacer(Modifier.width(22.dp))
                Column {
                    Text(
                        text = "MULLION",
                        style = TextStyle(
                            fontFamily = MullionFonts.Sans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 42.sp,
                            letterSpacing = (-1).sp,
                            brush = Brush.linearGradient(
                                listOf(MullionColors.Ink, MullionColors.Volt),
                            ),
                        ),
                    )
                    Text(
                        text = "One window. Every pane. / 一樘窗，处处合缝。",
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp),
                        color = MullionColors.Faint,
                    )
                }
            }
            Spacer(Modifier.height(26.dp))
        }
        Text(
            text = "dev.mullion:core 1.0.0 · Kotlin 2.2 · Compose 1.9 · minSdk 21 · Apache-2.0",
            style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
            color = MullionColors.Faint,
        )
        Text(
            text = "26/26 组件 · 14 自适应件 + 12 基础件 · 每个实验格均为真实 Compose 渲染 · blur 深度即窗格层级",
            style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
            color = MullionColors.Faint,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = "Bp ${mullionWindow().widthDp} · ${mullionWindow().bp.label}",
            style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
            color = MullionColors.Amber,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

/** A shared single-column wrapper used by the page. */
@Composable
fun StackSection(content: @Composable () -> Unit) {
    PageSection {
        content()
    }
}
