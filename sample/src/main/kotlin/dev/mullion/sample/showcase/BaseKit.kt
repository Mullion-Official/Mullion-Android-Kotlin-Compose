package dev.mullion.sample.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.base.MullionAvatar
import dev.mullion.compose.base.MullionAvatarSize
import dev.mullion.compose.base.MullionAvatarStack
import dev.mullion.compose.base.MullionBadge
import dev.mullion.compose.base.MullionButton
import dev.mullion.compose.base.MullionButtonSize
import dev.mullion.compose.base.MullionButtonVariant
import dev.mullion.compose.base.MullionCard
import dev.mullion.compose.base.MullionCheckbox
import dev.mullion.compose.base.MullionFieldState
import dev.mullion.compose.base.MullionIconButton
import dev.mullion.compose.base.MullionProgressBar
import dev.mullion.compose.base.MullionProgressShape
import dev.mullion.compose.base.MullionRadio
import dev.mullion.compose.base.MullionSegmentedControl
import dev.mullion.compose.base.MullionSlider
import dev.mullion.compose.base.MullionSnackbarHost
import dev.mullion.compose.base.MullionSnackbarResult
import dev.mullion.compose.base.MullionSwitch
import dev.mullion.compose.base.MullionTab
import dev.mullion.compose.base.MullionTabs
import dev.mullion.compose.base.MullionTextField
import dev.mullion.compose.base.rememberMullionSnackbarHostState
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.LocalMullionWindow
import dev.mullion.compose.window.currentBreakpoint
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.Bp
import dev.mullion.core.MullionViewport
import dev.mullion.core.MullionWindowState
import dev.mullion.core.PaneStrategy
import dev.mullion.sample.ui.Lab
import dev.mullion.sample.ui.SampleBody
import dev.mullion.sample.ui.SectionHeader
import dev.mullion.sample.ui.SampleChip
import dev.mullion.sample.ui.WidthToggle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** One base component in the workbench. */
data class BaseSpec(
    val number: Int,
    val name: String,
    val tag: String,
    val foot: String,
    val demo: @Composable () -> Unit,
)

val baseSpecs: List<BaseSpec> = listOf(
    BaseSpec(
        number = 15,
        name = "Button",
        tag = "Compose",
        foot = "variant 4 · size 36/44/52 · motion 120ms · touch 48dp",
        demo = { BaseButtonDemo() },
    ),
    BaseSpec(
        number = 16,
        name = "TextField",
        tag = "Compose",
        foot = "state 3 · h 42 · radius 12 · focus glow",
        demo = { BaseTextFieldDemo() },
    ),
    BaseSpec(
        number = 17,
        name = "ProgressBar",
        tag = "Compose",
        foot = "linear | circular · track h 6 · loop 1300ms",
        demo = { BaseProgressDemo() },
    ),
    BaseSpec(
        number = 18,
        name = "Checkbox & Radio",
        tag = "Compose",
        foot = "hit target 48dp · check 160ms · state 3",
        demo = { BaseSelectionDemo() },
    ),
    BaseSpec(
        number = 19,
        name = "Switch",
        tag = "Compose",
        foot = "track 52×30 · thumb 22 · travel 22dp · 180ms",
        demo = { BaseSwitchDemo() },
    ),
    BaseSpec(
        number = 20,
        name = "Slider",
        tag = "Compose",
        foot = "track 5 · thumb 20 · hit 22 · step snaps",
        demo = { BaseSliderDemo() },
    ),
    BaseSpec(
        number = 21,
        name = "SegmentedControl",
        tag = "Compose",
        foot = "段数 2|3|5 · h 38 · 150ms · 随断点增长",
        demo = { BaseSegmentedDemo() },
    ),
    BaseSpec(
        number = 22,
        name = "Badge",
        tag = "Compose",
        foot = "dot 11 · num 20 · text h 18 · 99+ collapse",
        demo = { BaseBadgeDemo() },
    ),
    BaseSpec(
        number = 23,
        name = "Avatar",
        tag = "Compose",
        foot = "S 28 / M 40 / L 56 · stack overlap -12 · 字母回退",
        demo = { BaseAvatarDemo() },
    ),
    BaseSpec(
        number = 24,
        name = "Card",
        tag = "Compose",
        foot = "radius 12 · min-h 120 · press scale · 1|2|3 栏",
        demo = { BaseCardDemo() },
    ),
    BaseSpec(
        number = 25,
        name = "Tabs",
        tag = "Compose",
        foot = "h 38 · indicator 2dp · scroll → overflow menu",
        demo = { BaseTabsDemo() },
    ),
    BaseSpec(
        number = 26,
        name = "Snackbar",
        tag = "Compose",
        foot = "h 52 · radius 14 · auto-dismiss 3200ms · 带撤销",
        demo = { BaseSnackbarDemo() },
    ),
)

/**
 * The twelve base components share one simulated ruler.
 *
 * The grid itself follows the real window (1/2/3 columns); the controls inside
 * each card receive a simulated width so their adaptive behavior can be
 * inspected without resizing the device.
 */
@Composable
fun BaseKitSection() {
    val realBp = mullionWindow().bp
    var viewport by rememberSaveable {
        mutableIntStateOf(if (realBp.isCompact) MullionViewport.Phone.widthDp else MullionViewport.Fold.widthDp)
    }
    val columns = when {
        realBp.isCompact -> 1
        realBp == Bp.Expanded -> 2
        else -> 3
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            kicker = "Base Kit / 15–26",
            title = "基础件工作台",
            description = "十二个基础件共享同一把标尺。切换 375 / 760 / 1180，输入框收列、进度条换形、分段器长段、徽标升级、卡片分栏——一次按键，整面墙同时收束。",
            trailing = {
                WidthToggle(width = viewport, onChange = { viewport = it })
            },
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Lab("Shared ruler", color = MullionColors.Volt)
            Spacer(Modifier.weight(1f))
            Text(
                text = "viewport ${viewport}dp · grid $columns col · ${baseSpecs.size} modules",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Faint,
            )
        }

        baseSpecs.chunked(columns).forEach { rowSpecs ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                rowSpecs.forEach { spec ->
                    Box(modifier = Modifier.weight(1f)) {
                        BaseCard(spec = spec, viewport = viewport)
                    }
                }
                repeat(columns - rowSpecs.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun BaseCard(spec: BaseSpec, viewport: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(20.dp), level = GlassLevel.Panel)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "%02d".format(spec.number),
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Volt,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = spec.name,
                style = MullionTextStyles.Mono.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MullionColors.Ink,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = spec.tag.uppercase(),
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                color = MullionColors.Faint,
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(236.dp),
        ) {
            CompositionLocalProvider(
                LocalMullionWindow provides MullionWindowState(
                    widthDp = viewport,
                    heightDp = 900,
                ),
            ) {
                Box(Modifier.fillMaxSize()) {
                    spec.demo()
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MullionColors.Edge.copy(alpha = 0.5f)),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = spec.foot,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
            color = MullionColors.Faint,
        )
    }
}

/* ------------------------------------------------------------------ */
/* Base demos                                                         */
/* ------------------------------------------------------------------ */

@Composable
private fun BaseButtonDemo() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MullionButton("主要操作", {}, size = MullionButtonSize.M)
            MullionButton("次要操作", {}, variant = MullionButtonVariant.Tonal, size = MullionButtonSize.M)
            MullionButton("描边", {}, variant = MullionButtonVariant.Outlined, size = MullionButtonSize.M)
            MullionButton("文本", {}, variant = MullionButtonVariant.Text, size = MullionButtonSize.M)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MullionButton("S 36", {}, size = MullionButtonSize.S)
            MullionButton("M 44", {}, size = MullionButtonSize.M)
            MullionButton("L 52", {}, size = MullionButtonSize.L)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MullionIconButton(onClick = {}, contentDescription = "新增") {
                Text("+", style = MullionTextStyles.Mono.copy(fontSize = 18.sp), color = MullionColors.OnVolt)
            }
            MullionIconButton(
                onClick = {},
                variant = MullionButtonVariant.Tonal,
                contentDescription = "更多",
            ) {
                Text("⋯", style = MullionTextStyles.Mono.copy(fontSize = 18.sp), color = MullionColors.Ink)
            }
            MullionButton("LOADING", {}, loading = true, size = MullionButtonSize.S)
        }
    }
}

@Composable
private fun BaseTextFieldDemo() {
    var name by remember { mutableStateOf("Pixel 8 Pro") }
    var threshold by remember { mutableStateOf("840dp") }
    var pkg by remember { mutableStateOf("dev.bench.") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MullionTextField(
            value = name,
            onValueChange = { name = it },
            label = "设备名称",
            placeholder = "Pixel 8 Pro",
        )
        MullionTextField(
            value = threshold,
            onValueChange = { threshold = it },
            label = "断点阈值",
        )
        MullionTextField(
            value = pkg,
            onValueChange = { pkg = it },
            label = "包名",
            state = MullionFieldState.Error,
            supportingText = "包名需以 dev.mullion 开头",
        )
    }
}

@Composable
private fun BaseProgressDemo() {
    var progress by remember { mutableFloatStateOf(0.62f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(90)
            progress += 0.01f
            if (progress >= 1f) progress = 0.25f
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MullionProgressBar(progress = progress, label = "Determinate", showValue = true)
        MullionProgressBar(progress = null, label = "Indeterminate")
        Row(verticalAlignment = Alignment.CenterVertically) {
            MullionProgressBar(
                progress = null,
                shape = MullionProgressShape.Circular,
                circularSize = 30.dp,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Circular · 900ms",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
                color = MullionColors.Faint,
            )
        }
        MullionProgressBar(
            progress = progress,
            shape = MullionProgressShape.Circular,
            circularSize = 30.dp,
            contentDescription = "构建进度",
        )
    }
}

@Composable
private fun BaseSelectionDemo() {
    var landscape by remember { mutableStateOf(true) }
    var hinge by remember { mutableStateOf(false) }
    var edgeToEdge by remember { mutableStateOf(true) }
    var radio by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        MullionCheckbox(landscape, { landscape = it }, label = "横屏适配")
        MullionCheckbox(hinge, { hinge = it }, label = "铰链避让")
        MullionCheckbox(edgeToEdge, { edgeToEdge = it }, label = "Edge-to-Edge")
        MullionCheckbox(
            checked = false,
            onCheckedChange = {},
            indeterminate = true,
            label = "半选状态",
        )
        Spacer(Modifier.height(4.dp))
        Row {
            MullionRadio((radio == 0), { radio = 0 }, label = "Compact", modifier = Modifier.weight(1f))
            MullionRadio((radio == 1), { radio = 1 }, label = "Expanded", modifier = Modifier.weight(1f))
            MullionRadio((radio == 2), { radio = 2 }, label = "XL", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun BaseSwitchDemo() {
    var auto by remember { mutableStateOf(true) }
    var grid by remember { mutableStateOf(false) }
    var dev by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        MullionSwitch(auto, { auto = it }, label = "自动收束")
        MullionSwitch(grid, { grid = it }, label = "显示标定网格")
        MullionSwitch(dev, { dev = it }, label = "开发者模式")
    }
}

@Composable
private fun BaseSliderDemo() {
    var gutter by remember { mutableFloatStateOf(24f) }
    var display by remember { mutableFloatStateOf(44f) }
    var alpha by remember { mutableFloatStateOf(88f) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MullionSlider(
            value = gutter,
            onValueChange = { gutter = it },
            valueRange = 0f..48f,
            label = "Pane.gutter",
            valueLabel = { "${it.roundToInt()}dp" },
        )
        MullionSlider(
            value = display,
            onValueChange = { display = it },
            valueRange = 24f..72f,
            label = "Type.display",
            valueLabel = { "${it.roundToInt()}sp" },
        )
        MullionSlider(
            value = alpha,
            onValueChange = { alpha = it },
            valueRange = 0f..100f,
            label = "Glass alpha",
            valueLabel = { "${it.roundToInt()}%" },
        )
    }
}

@Composable
private fun BaseSegmentedDemo() {
    val bp = currentBreakpoint()
    val segments = when {
        bp.isCompact -> listOf("Compact", "Medium")
        bp == Bp.Expanded -> listOf("Compact", "Medium", "Expanded")
        else -> listOf("Compact", "Medium", "Expanded", "Large", "XL")
    }
    var selected by remember(bp) { mutableIntStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MullionSegmentedControl(
            segments = segments,
            selectedIndex = selected.coerceIn(0, segments.lastIndex),
            onSelected = { selected = it },
        )
        Text(
            text = "段数随断点增长：${segments.size} 段 · ${bp.label}",
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
            color = MullionColors.Faint,
        )
        Text(
            text = "2 → 3 → 5",
            style = MullionTextStyles.Mono.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = MullionColors.Volt,
        )
    }
}

@Composable
private fun BaseBadgeDemo() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MullionBadge(kind = dev.mullion.compose.base.MullionBadgeKind.Dot) {
            MullionAvatar("Lin")
        }
        MullionBadge(count = 3) {
            MullionAvatar("Ming", colorIndex = 1)
        }
        MullionBadge(text = "NEW") {
            MullionAvatar("Dev", colorIndex = 2)
        }
        MullionBadge(count = 120) {
            MullionAvatar("Sora", colorIndex = 3)
        }
    }
}

@Composable
private fun BaseAvatarDemo() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        MullionAvatarStack(
            names = listOf("Lin Wei", "Ming Zhao", "Dev Patel", "Sora Kim", "Kai Ito"),
            size = MullionAvatarSize.M,
            max = 4,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MullionAvatar("S", size = MullionAvatarSize.S, colorIndex = 0)
            MullionAvatar("M", size = MullionAvatarSize.M, colorIndex = 1)
            MullionAvatar("L", size = MullionAvatarSize.L, colorIndex = 2)
            Text(
                text = "S 28 / M 40 / L 56",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Faint,
            )
        }
    }
}

@Composable
private fun BaseCardDemo() {
    val bp = currentBreakpoint()
    val compact = bp.isCompact
    val cards = listOf(
        "Compact" to "375dp 单栏收束",
        "Expanded" to "760dp 双栏分格",
        "XL" to "1180dp 三栏并列",
    )
    if (compact) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            cards.take(2).forEach { (title, body) ->
                MullionCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                ) {
                    Text(
                        text = title.uppercase(),
                        style = MullionTextStyles.LabelSmall,
                        color = MullionColors.Volt,
                    )
                    Text(
                        text = body,
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
                        color = MullionColors.Muted,
                    )
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            cards.forEach { (title, body) ->
                MullionCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                ) {
                    Text(
                        text = title.uppercase(),
                        style = MullionTextStyles.LabelSmall,
                        color = MullionColors.Volt,
                    )
                    Text(
                        text = body,
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                        color = MullionColors.Muted,
                    )
                }
            }
        }
    }
}

@Composable
private fun BaseTabsDemo() {
    val bp = currentBreakpoint()
    val tabs = listOf(
        "规格", "组件", "矩阵", "性能", "接入", "快照", "令牌"
    ).map { MullionTab(it) }
    var selected by remember { mutableIntStateOf(0) }
    val overflowAfter = when {
        bp.isCompact -> null
        bp == Bp.Expanded -> 4
        else -> 5
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MullionTabs(
            tabs = tabs,
            selectedIndex = selected,
            onSelected = { selected = it },
            overflowAfter = overflowAfter,
        )
        Text(
            text = tabs[selected].label,
            style = MullionTextStyles.Mono.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            color = MullionColors.Ink,
        )
        Text(
            text = if (overflowAfter == null) "scroll" else "fixed + overflow at ${overflowAfter}",
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
            color = MullionColors.Faint,
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MullionColors.Edge.copy(alpha = 0.5f)),
        )
    }
}

@Composable
private fun BaseSnackbarDemo() {
    val hostState = rememberMullionSnackbarHostState()
    val scope = rememberCoroutineScope()
    var lastAction by remember { mutableStateOf("idle") }
    val currentBp = mullionWindow().bp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MullionButton(
                text = "触发 Snackbar",
                onClick = {
                    scope.launch {
                        val result = hostState.showSnackbar(
                            message = "已切换到 Bp ${currentBp.baselineWidthDp} · ${currentBp.label}",
                            actionLabel = "撤销",
                        )
                        lastAction = when (result) {
                            MullionSnackbarResult.ActionPerformed -> "action"
                            MullionSnackbarResult.Dismissed -> "dismissed"
                        }
                    }
                },
                variant = MullionButtonVariant.Outlined,
                size = MullionButtonSize.S,
            )
            Text(
                text = "last: $lastAction",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Faint,
            )
            Text(
                text = "auto-dismiss 3200ms · 带撤销",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Faint,
            )
        }
        MullionSnackbarHost(state = hostState)
    }
}
