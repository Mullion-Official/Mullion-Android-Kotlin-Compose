package dev.mullion.sample.showcase

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.adaptive.AdaptiveDialog
import dev.mullion.compose.adaptive.AdaptiveScaffold
import dev.mullion.compose.adaptive.DockSide
import dev.mullion.compose.adaptive.DockedSheet
import dev.mullion.compose.adaptive.DraggableDivider
import dev.mullion.compose.adaptive.EdgeToEdgeBox
import dev.mullion.compose.adaptive.FoldablePane
import dev.mullion.compose.adaptive.MultiPane
import dev.mullion.compose.adaptive.OrbitNav
import dev.mullion.compose.adaptive.OrbitNavItem
import dev.mullion.compose.adaptive.MullionPermission
import dev.mullion.compose.adaptive.PermissionAnchor
import dev.mullion.compose.adaptive.PermissionSheet
import dev.mullion.compose.adaptive.SearchLayout
import dev.mullion.compose.adaptive.SharedAxis
import dev.mullion.compose.adaptive.SharedAxisTransition
import dev.mullion.compose.adaptive.SizeClassText
import dev.mullion.compose.adaptive.rememberDraggableSplitState
import dev.mullion.compose.base.MullionButton
import dev.mullion.compose.base.MullionButtonSize
import dev.mullion.compose.base.MullionButtonVariant
import dev.mullion.compose.base.MullionSegmentedControl
import dev.mullion.compose.base.MullionSlider
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.Bp
import dev.mullion.core.HingeInfo
import dev.mullion.core.HingePosture
import dev.mullion.core.MullionInsets
import dev.mullion.core.MullionMetrics
import dev.mullion.core.NavigationMode
import dev.mullion.core.PaneStrategy
import dev.mullion.core.ResourceBucket
import dev.mullion.core.TypeScale
import dev.mullion.sample.ui.CodeCard
import dev.mullion.sample.ui.DemoCanvas
import dev.mullion.sample.ui.DemoLine
import dev.mullion.sample.ui.DemoLines
import dev.mullion.sample.ui.DemoPane
import dev.mullion.sample.ui.Lab
import dev.mullion.sample.ui.NoteCard
import dev.mullion.sample.ui.PropsCard
import dev.mullion.sample.ui.SampleBody
import dev.mullion.sample.ui.SampleChip
import dev.mullion.sample.ui.SimulatedViewport
import dev.mullion.sample.ui.WidthToggle
import kotlin.math.roundToInt

/** One adaptive component in the cabinet. */
data class AdaptiveSpec(
    val number: Int,
    val name: String,
    val tags: List<String>,
    val description: String,
    val properties: List<Pair<String, String>>,
    val code: String,
    val demo: @Composable () -> Unit,
)

private val demoNavItems = listOf(
    OrbitNavItem("工作台", badge = "3"),
    OrbitNavItem("消息"),
    OrbitNavItem("设备"),
    OrbitNavItem("设置"),
)

/** All fourteen adaptive modules, in the order of the design index. */
val adaptiveSpecs: List<AdaptiveSpec> = listOf(
    AdaptiveSpec(
        number = 1,
        name = "AdaptiveScaffold",
        tags = listOf("Compose", "Layout", "Nav"),
        description = "外层容器按断点决定导航形态：Compact 显示底部栏，Expanded 显示左轨，XL 展开抽屉。调用方不再书写任何宽度判定。",
        properties = listOf(
            "nav.type" to "BottomBar | Rail | Drawer",
            "content.pane" to "SINGLE | DUAL | TRIPLE",
            "motion.navMorph" to "240ms · cubic-bezier(.2,0,0,1)",
            "insets.handle" to "statusBars + imePadding",
        ),
        code = """
            @Composable
            fun AdaptiveScaffold(
                topBar: @Composable () -> Unit,
                content: @Composable (PaddingValues) -> Unit,
            ) {
                val bp = currentBreakpoint()
                Scaffold(
                    topBar = topBar,
                    bottomBar = { if (bp < Bp.Expanded) OrbitBar() },
                    content = content,
                )
            }
        """.trimIndent(),
        demo = { DemoAdaptiveScaffold() },
    ),
    AdaptiveSpec(
        number = 2,
        name = "DockedSheet",
        tags = listOf("Compose", "Foldable", "Sheet"),
        description = "面板永不跨缝绘制。铰链竖置时停靠到铰链任一侧，铰链横置或无铰链时降级为底部面板。开合过程中内容宽度连续。",
        properties = listOf(
            "hinge.axis" to "Vertical | Horizontal | None",
            "dock.side" to "Start | End | Bottom",
            "motion.dock" to "380ms · cubic-bezier(.22,1,.36,1)",
            "min.pane" to "240dp",
        ),
        code = """
            DockedSheet(
                open = sheetOpen,
                onDismissRequest = { sheetOpen = false },
                hinge = windowInfo.hinge,
                dockSide = DockSide.Auto,
                sheetContent = { SheetContent() },
            ) { ScreenContent() }
        """.trimIndent(),
        demo = { DemoDockedSheet() },
    ),
    AdaptiveSpec(
        number = 3,
        name = "OrbitNav",
        tags = listOf("Compose", "Navigation"),
        description = "一条导航数据源，三态渲染。切换阈值就是断点本身，不额外引入动画状态机；选中项在三态间保持同一语义。",
        properties = listOf(
            "items" to "List<OrbitNavItem>",
            "state" to "BottomBar | Rail | Drawer",
            "motion.morph" to "240ms · shared axis",
            "indicator" to "volt glow",
        ),
        code = """
            OrbitNav(
                items = navItems,
                selectedIndex = current,
                onSelect = { current = it },
            )
            // < 600dp   BottomBar
            // 600–839dp Rail
            // 840dp+    Drawer
        """.trimIndent(),
        demo = { DemoOrbitNav() },
    ),
    AdaptiveSpec(
        number = 4,
        name = "SizeClassText",
        tags = listOf("Compose", "View", "Type"),
        description = "同一段文字按 sizeClass 阶梯缩放，而非按屏幕宽度线性缩放。行宽同时收束，保证每行字数落在可读区间。",
        properties = listOf(
            "type.compact" to "15sp / 62ch",
            "type.expanded" to "17sp / 68ch",
            "type.xl" to "19sp / 72ch",
            "scaling" to "阶梯式 · 非线性",
        ),
        code = """
            @Composable
            fun SizeClassText(text: String) {
                val bp = currentBreakpoint()
                Text(
                    text = text,
                    fontSize = when (bp) {
                        Bp.Compact, Bp.Medium -> 15.sp
                        Bp.Expanded -> 17.sp
                        else -> 19.sp
                    },
                )
            }
        """.trimIndent(),
        demo = { DemoSizeClassText() },
    ),
    AdaptiveSpec(
        number = 5,
        name = "SearchBar",
        tags = listOf("Compose", "Layout"),
        description = "单行搜索 → 结果 + 预览双栏 → 加筛选列三栏。断点切换无跳变，查询状态在收束过程中始终保留。",
        properties = listOf(
            "layout.375" to "single row",
            "layout.760" to "list + preview",
            "layout.1180" to "list + preview + filters",
            "motion.sweep" to "240ms",
        ),
        code = """
            SearchLayout(
                query = query,
                onQueryChange = { query = it },
                results = { DeviceList() },
                preview = { DevicePreview() },
                filters = { DeviceFilters() },
            )
            // paneStrategy(bp) 自动收束，无 if (width)
        """.trimIndent(),
        demo = { DemoSearchLayout() },
    ),
    AdaptiveSpec(
        number = 6,
        name = "MultiPane",
        tags = listOf("Compose", "Layout", "Core"),
        description = "列表 / 详情 / 预览三栏联动。1 栏时详情覆盖列表，2 栏时列表常驻，3 栏时预览落位。栏宽由 paneStrategy 唯一决定。",
        properties = listOf(
            "pane.375" to "list",
            "pane.760" to "list + detail",
            "pane.1180" to "list + detail + preview",
            "gutter" to "24dp",
        ),
        code = """
            MultiPane(
                list = { NoteList() },
                detail = { NoteDetail() },
                preview = { NotePreview() },
                showDetail = selected != null,
                onBack = { selected = null },
            )
            // 编译期解析栏数 · 布局耗时 0.8ms/帧
        """.trimIndent(),
        demo = { DemoMultiPane() },
    ),
    AdaptiveSpec(
        number = 7,
        name = "AdaptiveDialog",
        tags = listOf("Compose", "Dialog"),
        description = "紧凑态居中弹窗，宽态侧滑面板。同一份内容两种容器，焦点管理与返回键行为一致。",
        properties = listOf(
            "kind.compact" to "Centered dialog",
            "kind.expanded" to "Side panel",
            "scrim" to "blur 3px · 180ms",
            "motion" to "340ms · cubic-bezier(.22,1,.36,1)",
        ),
        code = """
            AdaptiveDialog(
                visible = open,
                onDismissRequest = { open = false },
                kind = if (currentBreakpoint() < Bp.Large)
                    DialogKind.Centered else DialogKind.SidePanel,
            ) { DialogContent() }
        """.trimIndent(),
        demo = { DemoAdaptiveDialog() },
    ),
    AdaptiveSpec(
        number = 8,
        name = "ResourceScaler",
        tags = listOf("View", "Resource"),
        description = "资源按断点取值，替代 values-w820dp 目录。数值在编译期解析，运行时零反射，可直接混入既有 View 工程。",
        properties = listOf(
            "spacing.section" to "48dp / 80dp / 120dp",
            "type.display" to "32sp / 44sp / 56sp",
            "resolution" to "编译期 · 无反射",
            "migration" to "替换 values-w820dp",
        ),
        code = """
            ResourceScaler.scaled(R.dimen.spacing_section)
            // 320–599dp -> 48dp
            // 600–839dp -> 80dp
            // 840dp+   -> 120dp
        """.trimIndent(),
        demo = { DemoResourceScaler() },
    ),
    AdaptiveSpec(
        number = 9,
        name = "EdgeToEdgeBox",
        tags = listOf("Compose", "View", "Insets"),
        description = "状态栏 / 手势条 / IME / 折叠屏 inset 一次接管。键盘弹出时内容自动上收，API 21 起行为一致。",
        properties = listOf(
            "insets" to "statusBars + navigationBars + ime",
            "ime.push" to "134px · 380ms",
            "padding" to "由 inset 值直接派生",
            "api" to "21+ 一致",
        ),
        code = """
            EdgeToEdgeBox { padding ->
                Column(Modifier.padding(padding)) {
                    ScreenContent()
                }
            }
        """.trimIndent(),
        demo = { DemoEdgeToEdge() },
    ),
    AdaptiveSpec(
        number = 10,
        name = "DraggableDivider",
        tags = listOf("Compose", "Layout"),
        description = "分栏拖拽，最小栏宽强制钳制。越界不越界、不抖动，松手回弹 220ms，读数实时给出两栏实际宽度。",
        properties = listOf(
            "min.pane" to "120dp",
            "clamp" to "强制 · 越界回弹",
            "motion.bounce" to "220ms · spring",
            "hit.area" to "11dp / pointer",
        ),
        code = """
            DraggableDivider(
                state = rememberDraggableSplitState(),
                minPaneWidth = 120.dp,
                first = { PaneA() },
                second = { PaneB() },
            )
        """.trimIndent(),
        demo = { DemoDraggableDivider() },
    ),
    AdaptiveSpec(
        number = 11,
        name = "WindowSizeClass",
        tags = listOf("Compose", "Core"),
        description = "320 / 480 / 600 / 840 / 1200 五档断点对象。拖动下方游标即可看到断点归属切换，与运行时判定逻辑完全一致。",
        properties = listOf(
            "objects" to "Compact / Medium / Expanded / Large / XL",
            "thresholds" to "320 / 480 / 600 / 840 / 1200 dp",
            "api" to "currentBreakpoint()",
            "resolution" to "编译期内联",
        ),
        code = """
            val bp = currentBreakpoint()
            when (bp) {
                Bp.Compact, Bp.Medium -> BottomBar()
                Bp.Expanded -> Rail()
                Bp.Large, Bp.XL -> Drawer()
            }
        """.trimIndent(),
        demo = { DemoWindowSizeClass() },
    ),
    AdaptiveSpec(
        number = 12,
        name = "FoldablePane",
        tags = listOf("Compose", "Foldable"),
        description = "铰链状态驱动布局策略：FLAT 全幅、HALF 避让上屏、TENT 呈现斜切。状态切换即刻重测量，不等下一帧。",
        properties = listOf(
            "states" to "FLAT | HALF_OPENED | TENT",
            "hinge.safeGap" to "24dp",
            "motion.remeasure" to "420ms",
            "detect" to "WindowInfoTracker",
        ),
        code = """
            FoldablePane(
                hinge = windowInfo.hinge,
                start = { LeafA() },
                end = { LeafB() },
                onHingeChanged = { remeasure() },
            )
        """.trimIndent(),
        demo = { DemoFoldablePane() },
    ),
    AdaptiveSpec(
        number = 13,
        name = "SharedAxisTransition",
        tags = listOf("Compose", "Motion"),
        description = "跨断点切换用同一条轴变换：旧栏左移淡出、新栏右移切入。240ms 收束，容器尺寸变化不再闪白。",
        properties = listOf(
            "axis" to "X | Y | Z",
            "duration" to "240ms · cubic-bezier(.4,0,.2,1)",
            "enter" to "+28px fade-in",
            "exit" to "-28px fade-out",
        ),
        code = """
            SharedAxisTransition(
                targetState = bp,
                axis = SharedAxis.X,
                durationMillis = 240,
            ) { Pane(it) }
        """.trimIndent(),
        demo = { DemoSharedAxis() },
    ),
    AdaptiveSpec(
        number = 14,
        name = "PermissionSheet",
        tags = listOf("Compose", "Sheet", "Permissions"),
        description = "紧凑态底部弹层、宽态锚定到触发按钮旁。同一份权限清单，两种落位，语义与焦点顺序不变。",
        properties = listOf(
            "anchor.compact" to "Bottom sheet",
            "anchor.wide" to "Trigger-anchored panel",
            "motion" to "340ms · cubic-bezier(.22,1,.36,1)",
            "focus" to "保持原触发点",
        ),
        code = """
            PermissionSheet(
                visible = open,
                permissions = permissions,
                anchor = if (currentBreakpoint().isCompact)
                    PermissionAnchor.Bottom else PermissionAnchor.Trigger,
                onGrantAll = { launchPermissionRequest() },
            )
        """.trimIndent(),
        demo = { DemoPermissionSheet() },
    ),
)

/** One full cabinet card: header, live tray, properties and code. */
@Composable
fun AdaptiveCard(spec: AdaptiveSpec) {
    val bp = mullionWindow().bp
    val defaultWidth = if (bp.isCompact) 375 else 760
    var width by rememberSaveable(spec.number) { mutableIntStateOf(defaultWidth) }
    var codeOpen by rememberSaveable(spec.number) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(28.dp), level = GlassLevel.Window)
            .padding(if (bp.isCompact) 18.dp else 24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Lab("Component ${"%02d".format(spec.number)}", color = MullionColors.Volt)
            Spacer(Modifier.weight(1f))
            sampleTags(spec.tags)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = spec.name,
            style = MullionTextStyles.Mono.copy(
                fontSize = if (bp.isCompact) 19.sp else 24.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = MullionColors.Ink,
        )
        Spacer(Modifier.height(6.dp))
        SampleBody(spec.description, size = 13)

        Spacer(Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glass(shape = RoundedCornerShape(20.dp), level = GlassLevel.Panel),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WidthToggle(width = width, onChange = { width = it })
                Spacer(Modifier.weight(1f))
                if (!bp.isCompact) {
                    Text(
                        text = "REAL COMPOSE · ${width}dp",
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                        color = MullionColors.Faint,
                    )
                    Spacer(Modifier.width(10.dp))
                }
                SampleChip(
                    text = if (codeOpen) "HIDE CODE" else "CODE",
                    selected = codeOpen,
                    onClick = { codeOpen = !codeOpen },
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MullionColors.Edge.copy(alpha = 0.6f)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                SimulatedViewport(widthDp = width) {
                    spec.demo()
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        if (bp.isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PropsCard(spec.properties)
                if (codeOpen) {
                    CodeCard(file = "${spec.name}.kt", code = spec.code)
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                PropsCard(
                    props = spec.properties,
                    modifier = Modifier.weight(0.42f),
                )
                if (codeOpen) {
                    CodeCard(
                        file = "${spec.name}.kt",
                        code = spec.code,
                        modifier = Modifier.weight(0.58f),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(0.58f)
                            .heightIn(min = 74.dp)
                            .glass(shape = RoundedCornerShape(18.dp), level = GlassLevel.Panel)
                            .clickable { codeOpen = true }
                            .padding(18.dp),
                    ) {
                        Column {
                            Lab("Kotlin API", color = MullionColors.Volt)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "点击展开 ${spec.name}.kt 的调用示例 ⧉",
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                                color = MullionColors.Faint,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun sampleTags(tags: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        tags.forEach { tag ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MullionColors.Ink.copy(alpha = 0.06f), CircleShape)
                    .border(1.dp, MullionColors.Edge.copy(alpha = 0.7f), CircleShape)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = tag.uppercase(),
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                    color = MullionColors.Muted,
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* Demos                                                              */
/* ------------------------------------------------------------------ */

@Composable
private fun DemoAdaptiveScaffold() {
    var selected by remember { mutableIntStateOf(0) }
    val nav: @Composable () -> Unit = {
        OrbitNav(
            items = demoNavItems,
            selectedIndex = selected,
            onSelect = { selected = it },
        )
    }

    DemoCanvas(height = 350.dp) {
        AdaptiveScaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "WORKBENCH",
                        style = MullionTextStyles.Mono.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                        ),
                        color = MullionColors.Ink,
                    )
                    Spacer(Modifier.weight(1f))
                    SampleChip("Bp ${mullionWindow().bp.label.take(3)}")
                }
            },
            bottomBar = nav,
            rail = nav,
            drawer = nav,
        ) { _ ->
            Box(Modifier.fillMaxSize().padding(12.dp)) {
                DemoPane(modifier = Modifier.fillMaxSize(), title = demoNavItems[selected].label) {
                    DemoLines(listOf(0.9f, 0.72f, 0.45f, 0.8f, 0.62f))
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(MullionColors.Volt.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                .border(1.dp, MullionColors.Volt.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                        )
                        Box(
                            Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(MullionColors.Ink.copy(alpha = 0.05f), RoundedCornerShape(10.dp)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoDockedSheet() {
    var open by remember { mutableStateOf(true) }
    var half by remember { mutableStateOf(true) }
    val hinge = if (half) HingeInfo.vertical(20f) else HingeInfo.None

    DemoCanvas(height = 350.dp) {
        Box(Modifier.fillMaxSize()) {
            DockedSheet(
                open = open,
                onDismissRequest = { open = false },
                hinge = hinge,
                dockSide = DockSide.Auto,
                sheetContent = {
                    Lab("SHEET CONTENT", color = MullionColors.Volt)
                    Spacer(Modifier.height(10.dp))
                    DemoLines(listOf(0.88f, 0.64f, 0.42f))
                    Spacer(Modifier.height(14.dp))
                    MullionButton(
                        text = "确认",
                        onClick = { open = false },
                        size = MullionButtonSize.S,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    MullionButton(
                        text = "铰链 ${if (half) "HALF" else "FLAT"}",
                        onClick = { half = !half },
                        variant = MullionButtonVariant.Text,
                        size = MullionButtonSize.S,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                content = {
                    Column(Modifier.fillMaxSize().padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Lab(
                                if (half) "HINGE: HALF_OPENED / SEAM 20dp" else "HINGE: FLAT",
                                color = MullionColors.Signal,
                                size = 8,
                            )
                            Spacer(Modifier.weight(1f))
                            if (!open) {
                                SampleChip("打开面板", onClick = { open = true })
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        DemoPane(modifier = Modifier.fillMaxSize()) {
                            DemoLines(listOf(0.92f, 0.7f, 0.5f, 0.8f, 0.36f, 0.6f))
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun DemoOrbitNav() {
    var selected by remember { mutableIntStateOf(1) }
    val mode = mullionWindow().navigationMode
    val nav: @Composable (Modifier) -> Unit = { modifier ->
        OrbitNav(
            items = demoNavItems,
            selectedIndex = selected,
            onSelect = { selected = it },
            modifier = modifier,
        )
    }

    DemoCanvas(height = 340.dp) {
        Row(Modifier.fillMaxSize()) {
            if (mode != NavigationMode.BottomBar) {
                nav(Modifier)
            }
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Box(Modifier.weight(1f).fillMaxWidth().padding(12.dp)) {
                    DemoPane(modifier = Modifier.fillMaxSize(), title = demoNavItems[selected].label) {
                        Text(
                            text = when (mode) {
                                NavigationMode.BottomBar -> "BottomBar · 一份数据源"
                                NavigationMode.Rail -> "Rail · 选中态保持"
                                NavigationMode.Drawer -> "Drawer · 语义不变"
                            },
                            style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                            color = MullionColors.Muted,
                        )
                        Spacer(Modifier.height(12.dp))
                        DemoLines(listOf(0.86f, 0.62f, 0.42f))
                    }
                }
                if (mode == NavigationMode.BottomBar) {
                    Box(Modifier.padding(12.dp)) {
                        nav(Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoSizeClassText() {
    val bp = mullionWindow().bp
    val scale = TypeScale.forBp(bp)
    DemoCanvas(height = 330.dp) {
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            SizeClassText(
                text = "同一段文字，按 sizeClass 阶梯缩放，而不是按屏幕宽度线性拉伸。行宽同时收束，保证每行字数落在可读区间；段落节奏不因窗口尺寸变化而崩坏。",
                color = MullionColors.Ink,
            )
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MetricBlock("Font", "${scale.fontSizeSp.toInt()}sp", Modifier.weight(1f))
                MetricBlock("Measure", "${scale.maxMeasureCh}ch", Modifier.weight(1f))
                MetricBlock("Scaling", "阶梯", Modifier.weight(1f), accent = true)
            }
        }
    }
}

@Composable
private fun MetricBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
) {
    Column(modifier = modifier) {
        Lab(label, size = 8)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MullionTextStyles.Mono.copy(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
            color = if (accent) MullionColors.Volt else MullionColors.Ink,
        )
    }
}

private data class DemoDevice(val name: String, val width: String)

private val demoDevices = listOf(
    DemoDevice("Pixel 8 Pro", "412dp"),
    DemoDevice("Galaxy Z Fold5", "344dp"),
    DemoDevice("Pixel Tablet", "840dp"),
    DemoDevice("Xiaomi Pad 6", "800dp"),
    DemoDevice("Galaxy Tab S9", "820dp"),
)

@Composable
private fun DemoSearchLayout() {
    var query by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val filtered = demoDevices.filter {
        query.isBlank() || it.name.contains(query, ignoreCase = true)
    }
    val selectedDevice = demoDevices[selected]

    DemoCanvas(height = 360.dp) {
        SearchLayout(
            query = query,
            onQueryChange = { query = it },
            resultCount = filtered.size,
            placeholder = "搜索设备 / 型号 / 断点…",
            results = {
                Column(Modifier.fillMaxSize()) {
                    filtered.forEach { device ->
                        val active = device == selectedDevice
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (active) MullionColors.Volt.copy(alpha = 0.12f)
                                    else MullionColors.Ink.copy(alpha = 0.04f),
                                    RoundedCornerShape(10.dp),
                                )
                                .border(
                                    1.dp,
                                    if (active) MullionColors.Volt.copy(alpha = 0.55f)
                                    else MullionColors.Edge.copy(alpha = 0.5f),
                                    RoundedCornerShape(10.dp),
                                )
                                .clickable { selected = demoDevices.indexOf(device) }
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = device.name,
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.5.sp),
                                color = if (active) MullionColors.Ink else MullionColors.Muted,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = device.width,
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                                color = if (active) MullionColors.Volt else MullionColors.Faint,
                            )
                        }
                    }
                }
            },
            preview = {
                DemoPane(modifier = Modifier.fillMaxSize(), title = "Preview") {
                    Text(
                        text = selectedDevice.name,
                        style = MullionTextStyles.Mono.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        color = MullionColors.Ink,
                    )
                    Text(
                        text = selectedDevice.width + " · 820dp window",
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                        color = MullionColors.Volt,
                    )
                    Spacer(Modifier.height(14.dp))
                    DemoLines(listOf(0.9f, 0.64f, 0.42f, 0.72f))
                }
            },
            filters = {
                DemoPane(modifier = Modifier.fillMaxSize(), title = "Filters") {
                    listOf("Phone", "Tablet", "Foldable", ">= 840dp").forEachIndexed { index, label ->
                        SampleChip(
                            text = label,
                            selected = index == 0,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                    }
                }
            },
        )
    }
}

private val demoNotes = listOf(
    "折叠屏策略" to "铰链竖置时，详情不跨缝；面板停靠任一侧。",
    "分栏阈值" to "PaneStrategy 决定栏数，屏幕代码不再出现宽度判断。",
    "铰链避让" to "内容与物理缝隙之间永远保留 24dp 安全距离。",
    "IME inset" to "键盘弹出时，内容上收而不是被遮挡。",
)

@Composable
private fun DemoMultiPane() {
    var showDetail by remember { mutableStateOf(false) }
    var selected by remember { mutableIntStateOf(0) }
    val bp = mullionWindow().bp

    DemoCanvas(height = 380.dp) {
        MultiPane(
            list = {
                DemoPane(modifier = Modifier.fillMaxSize(), title = "List") {
                    demoNotes.forEachIndexed { index, (title, _) ->
                        val active = index == selected
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(CircleShape)
                                .background(
                                    if (active) MullionColors.Volt.copy(alpha = 0.1f)
                                    else Color.Transparent,
                                    CircleShape,
                                )
                                .border(
                                    1.dp,
                                    if (active) MullionColors.Volt.copy(alpha = 0.5f)
                                    else MullionColors.Edge.copy(alpha = 0.45f),
                                    CircleShape,
                                )
                                .clickable {
                                    selected = index
                                    showDetail = true
                                }
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                        ) {
                            Text(
                                text = "0${index + 1}",
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
                                color = MullionColors.Faint,
                                modifier = Modifier.width(22.dp),
                            )
                            Text(
                                text = title,
                                style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                                color = if (active) MullionColors.Ink else MullionColors.Muted,
                            )
                        }
                    }
                }
            },
            detail = {
                DemoPane(modifier = Modifier.fillMaxSize(), title = "Detail") {
                    if (bp.isCompact) {
                        SampleChip("← 返回列表", onClick = { showDetail = false })
                        Spacer(Modifier.height(8.dp))
                    }
                    Text(
                        text = demoNotes[selected].first,
                        style = MullionTextStyles.Mono.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        color = MullionColors.Ink,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = demoNotes[selected].second,
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                        color = MullionColors.Muted,
                    )
                    Spacer(Modifier.height(16.dp))
                    DemoLines(listOf(0.9f, 0.7f, 0.46f, 0.82f))
                }
            },
            preview = {
                DemoPane(modifier = Modifier.fillMaxSize(), title = "Preview") {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MullionColors.Volt.copy(alpha = 0.08f))
                            .border(1.dp, MullionColors.Volt.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    )
                    Spacer(Modifier.height(12.dp))
                    DemoLines(listOf(0.8f, 0.52f))
                }
            },
            showDetail = showDetail,
            onBack = { showDetail = false },
        )
    }
}

@Composable
private fun DemoAdaptiveDialog() {
    var open by remember { mutableStateOf(true) }

    DemoCanvas(height = 380.dp) {
        Box(Modifier.fillMaxSize()) {
            DemoPane(modifier = Modifier.fillMaxSize(), title = "Underlying content") {
                DemoLines(listOf(0.9f, 0.66f, 0.4f, 0.74f))
                Spacer(Modifier.height(16.dp))
                MullionButton(
                    text = "打开弹窗",
                    onClick = { open = true },
                    size = MullionButtonSize.S,
                )
            }
            AdaptiveDialog(
                visible = open,
                onDismissRequest = { open = false },
            ) {
                Lab("DIALOG CONTENT", color = MullionColors.Volt)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (mullionWindow().bp >= Bp.Large) {
                        "侧滑面板 · 窄屏居中"
                    } else {
                        "居中弹窗 · 宽屏侧滑"
                    },
                    style = MullionTextStyles.Mono.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = MullionColors.Ink,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "同一份 body，两种容器。返回键与 scrim 行为一致。",
                    style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                    color = MullionColors.Muted,
                )
                Spacer(Modifier.height(14.dp))
                DemoLines(listOf(0.9f, 0.58f))
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MullionButton("确定", { open = false }, size = MullionButtonSize.S)
                    MullionButton(
                        text = "取消",
                        onClick = { open = false },
                        variant = MullionButtonVariant.Tonal,
                        size = MullionButtonSize.S,
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoResourceScaler() {
    val bp = mullionWindow().bp
    val metrics = MullionMetrics.forBp(bp)
    val bucket = ResourceBucket.forBp(bp)

    DemoCanvas(height = 330.dp) {
        DemoPane(modifier = Modifier.fillMaxSize(), title = "values bucket: ${bucket.qualifier}") {
            val rows = listOf(
                Triple("spacing.section", metrics.sectionSpacingDp, "dp"),
                Triple("type.display", metrics.displayTypeSp, "sp"),
                Triple("pane.margin", metrics.paneMarginDp, "dp"),
                Triple("pane.gutter", metrics.paneGutterDp, "dp"),
            )
            rows.forEach { (key, value, unit) ->
                val fraction by animateFloatAsState(
                    targetValue = value / 120f,
                    animationSpec = tween(240),
                    label = "ResourceScaler.$key",
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = key,
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
                        color = MullionColors.Faint,
                        modifier = Modifier.width(104.dp),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(11.dp)
                            .clip(CircleShape)
                            .background(MullionColors.Ink.copy(alpha = 0.08f), CircleShape),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction.coerceIn(0.02f, 1f))
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            MullionColors.Volt.copy(alpha = 0.85f),
                                            MullionColors.Volt.copy(alpha = 0.25f),
                                        ),
                                    ),
                                    CircleShape,
                                ),
                        )
                    }
                    Text(
                        text = "$value$unit",
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                        color = MullionColors.Volt,
                        modifier = Modifier.width(52.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            NoteCard(
                text = "View 侧同一组值：ResourceScaler.scaled(R.dimen.mullion_spacing_section)",
                accent = MullionColors.Mint,
            )
        }
    }
}

@Composable
private fun DemoEdgeToEdge() {
    var ime by remember { mutableStateOf(false) }
    val insets = MullionInsets(
        topDp = 28,
        bottomDp = 24,
        imeDp = if (ime) 134 else 0,
    )

    DemoCanvas(height = 360.dp) {
        EdgeToEdgeBox(
            modifier = Modifier.fillMaxSize(),
            applyPadding = false,
            simulatedInsets = insets,
        ) { padding ->
            Box(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = "content padding = inset",
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                        color = MullionColors.Faint,
                    )
                    Spacer(Modifier.height(8.dp))
                    DemoLines(listOf(0.9f, 0.68f, 0.44f, 0.76f))
                    Spacer(Modifier.height(10.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MullionColors.Volt.copy(alpha = 0.08f))
                            .border(1.dp, MullionColors.Volt.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                    )
                }

                Band(
                    text = "STATUS BAR · 28dp",
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = MullionColors.Signal,
                )
                Band(
                    text = "GESTURE NAV · 24dp",
                    modifier = Modifier.align(Alignment.BottomCenter),
                    color = MullionColors.Signal,
                )
                if (ime) {
                    Band(
                        text = "IME · KEYBOARD 110dp",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-24).dp),
                        color = MullionColors.Volt,
                    )
                }
                Box(Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                    SampleChip(
                        text = if (ime) "收起键盘" else "弹出键盘",
                        selected = ime,
                        onClick = { ime = !ime },
                    )
                }
            }
        }
    }
}

@Composable
private fun Band(text: String, modifier: Modifier, color: Color) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 8.sp),
            color = color,
        )
    }
}

@Composable
private fun DemoDraggableDivider() {
    val state = rememberDraggableSplitState(0.42f)
    var leftDp by remember { mutableFloatStateOf(0f) }
    var rightDp by remember { mutableFloatStateOf(0f) }

    DemoCanvas(height = 380.dp) {
        Column(Modifier.fillMaxSize()) {
            DraggableDivider(
                first = {
                    DemoPane(modifier = Modifier.fillMaxSize(), title = "Pane A") {
                        DemoLines(listOf(0.9f, 0.64f, 0.42f, 0.74f))
                    }
                },
                second = {
                    DemoPane(modifier = Modifier.fillMaxSize(), title = "Pane B") {
                        DemoLines(listOf(0.82f, 0.58f))
                    }
                },
                state = state,
                minPaneWidth = 120.dp,
                onSplitChange = { left, right ->
                    leftDp = left.value
                    rightDp = right.value
                },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "SPLIT ${(leftDp / (leftDp + rightDp).coerceAtLeast(1f) * 100).roundToInt()}%" +
                    " / ${(rightDp / (leftDp + rightDp).coerceAtLeast(1f) * 100).roundToInt()}%" +
                    " · A ${leftDp.roundToInt()}dp · B ${rightDp.roundToInt()}dp · CLAMP OK",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 8.5.sp),
                color = MullionColors.Amber,
            )
        }
    }
}

@Composable
private fun DemoWindowSizeClass() {
    var simulated by remember { mutableIntStateOf(840) }
    val bp = Bp.of(simulated)
    val nav = NavigationMode.forBp(bp)
    val panes = PaneStrategy().countFor(bp)

    DemoCanvas(height = 360.dp) {
        Column(Modifier.fillMaxSize().padding(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricBlock("Class", bp.label, Modifier.weight(1f))
                MetricBlock("Nav", nav.name, Modifier.weight(1f))
                MetricBlock("panes", "$panes", Modifier.weight(1f), accent = true)
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Bp.entries.forEach { option ->
                    val active = option == bp
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (active) MullionColors.Volt.copy(alpha = 0.14f)
                                else MullionColors.Ink.copy(alpha = 0.04f),
                                RoundedCornerShape(8.dp),
                            )
                            .border(
                                1.dp,
                                if (active) MullionColors.Volt.copy(alpha = 0.65f)
                                else MullionColors.Edge.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = option.label.take(1),
                            style = MullionTextStyles.MonoSmall.copy(fontSize = 10.sp),
                            color = if (active) MullionColors.Volt else MullionColors.Muted,
                        )
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            MullionSlider(
                value = simulated.toFloat(),
                onValueChange = { simulated = it.roundToInt().coerceIn(320, 1200) },
                valueRange = 320f..1200f,
                label = "Window width",
                valueLabel = { "${it.roundToInt()} dp" },
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "拖到 600 变轨、840 开抽屉、1200 进 XL。判定函数与运行时共用。",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Faint,
            )
        }
    }
}

@Composable
private fun DemoFoldablePane() {
    var mode by remember { mutableIntStateOf(1) }
    val hinge = when (mode) {
        0 -> HingeInfo.None
        1 -> HingeInfo.vertical(20f, HingePosture.HalfOpened)
        else -> HingeInfo.vertical(20f, HingePosture.Tent)
    }
    val label = when (mode) {
        0 -> "FLAT"
        1 -> "HALF_OPENED"
        else -> "TENT"
    }

    DemoCanvas(height = 360.dp) {
        Column(Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MullionSegmentedControl(
                    segments = listOf("FLAT", "HALF", "TENT"),
                    selectedIndex = mode,
                    onSelected = { mode = it },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(10.dp))
                Lab(label, color = MullionColors.Signal, size = 8)
            }
            Spacer(Modifier.height(10.dp))
            Box(Modifier.weight(1f)) {
                FoldablePane(
                    hinge = hinge,
                    start = {
                        DemoPane(modifier = Modifier.fillMaxSize(), title = "Leaf A") {
                            DemoLines(listOf(0.9f, 0.64f, 0.42f))
                        }
                    },
                    end = {
                        DemoPane(modifier = Modifier.fillMaxSize(), title = "Leaf B") {
                            DemoLines(listOf(0.82f, 0.56f))
                        }
                    },
                    singlePane = {
                        DemoPane(modifier = Modifier.fillMaxSize(), title = "Single pane") {
                            NoteCard("窗格不足 240dp · 单栏避让，不挤压", accent = MullionColors.Signal)
                            Spacer(Modifier.height(10.dp))
                            DemoLines(listOf(0.82f, 0.52f))
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun DemoSharedAxis() {
    var pane by remember { mutableIntStateOf(0) }
    var axis by remember { mutableStateOf(SharedAxis.X) }

    DemoCanvas(height = 350.dp) {
        Column(Modifier.fillMaxSize()) {
            MullionSegmentedControl(
                segments = listOf("PANE A", "PANE B"),
                selectedIndex = pane,
                onSelected = { pane = it },
            )
            Spacer(Modifier.height(10.dp))
            Box(Modifier.weight(1f)) {
                SharedAxisTransition(
                    targetState = pane,
                    axis = axis,
                ) { visiblePane ->
                    DemoPane(
                        modifier = Modifier.fillMaxSize(),
                        title = if (visiblePane == 0) "Pane A · Compact" else "Pane B · Expanded",
                    ) {
                        DemoLines(
                            if (visiblePane == 0) listOf(0.9f, 0.68f, 0.44f)
                            else listOf(0.8f, 0.56f),
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "axis ${axis.name} · 240ms",
                            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                            color = MullionColors.Faint,
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SharedAxis.entries.forEach { option ->
                    SampleChip(
                        text = "AXIS ${option.name}",
                        selected = axis == option,
                        onClick = { axis = option },
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoPermissionSheet() {
    var visible by remember { mutableStateOf(true) }
    var granted by remember { mutableStateOf("等待授权") }
    val permissions = listOf(
        MullionPermission("相机", "CAMERA", "用于扫描设备二维码", required = true),
        MullionPermission("位置", "ACCESS_FINE_LOCATION", "用于发现附近设备"),
        MullionPermission("通知", "POST_NOTIFICATIONS", "构建完成时提醒"),
    )

    DemoCanvas(height = 380.dp) {
        Box(Modifier.fillMaxSize()) {
            DemoPane(modifier = Modifier.fillMaxSize(), title = "Trigger surface") {
                Text(
                    text = granted,
                    style = MullionTextStyles.Mono.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                    color = MullionColors.Volt,
                )
                Spacer(Modifier.height(10.dp))
                MullionButton(
                    text = "申请相机权限",
                    onClick = { visible = true },
                    size = MullionButtonSize.S,
                )
                Spacer(Modifier.height(12.dp))
                DemoLines(listOf(0.82f, 0.56f))
            }
            PermissionSheet(
                visible = visible,
                permissions = permissions,
                onDismissRequest = { visible = false },
                onGrantAll = {
                    granted = "已允许：CAMERA + LOCATION + NOTIFICATIONS"
                    visible = false
                },
                onDeny = {
                    granted = "仅本次允许"
                    visible = false
                },
                anchor = PermissionAnchor.Auto,
                anchorOffset = DpOffset(20.dp, 62.dp),
            )
        }
    }
}
