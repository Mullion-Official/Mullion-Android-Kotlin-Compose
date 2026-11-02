# 核心 API 速查（中文）

这是按使用频率整理的中文释义。**完整签名、默认值、参数表在自动生成的
[Core](mullion-core.md) / [Compose](mullion-compose.md) / [View](mullion-view.md) API 页面**。

## 一、断点与窗口

| API | 释义 |
| --- | --- |
| `Bp.of(widthDp)` | 把窗口宽度（dp）映射到 `Compact / Medium / Expanded / Large / XL` 五档宽度类；纯函数，无反射。 |
| `Bp.baselineWidthDp` | 该档位的设计基线宽度（320 / 480 / 600 / 840 / 1200）。 |
| `currentBreakpoint()` | Composable：读取当前窗口的 `Bp`。业务里只需要这一句，不写 `if (width > 600)`。 |
| `currentNavigationMode()` | Composable：读取当前导航形态 `BottomBar / Rail / Drawer`。 |
| `currentHinge()` | Composable：读取当前折叠屏铰链几何，无折叠时为 `HingeInfo.None`。 |
| `currentInsets()` | Composable：读取系统栏 + IME 的 dp inset。 |
| `mullionWindow()` | Composable：读取完整 `MullionWindowState`（宽高、Bp、导航、pane 数、铰链、insets）。 |
| `MullionWindowProvider(window)` | Composable：为子树注入/模拟窗口状态；预览、截图、组件托盘使用。 |
| `MullionWindowState.of(widthDp, heightDp)` | 纯函数：由尺寸构造窗口状态，便于单元测试。 |

## 二、分栏与折叠

| API | 释义 |
| --- | --- |
| `PaneStrategy()` | 分栏策略。默认 1/1/2/3/3 栏；`gutterDp = null` 表示由 `MullionMetrics` 按 Bp 自动取栏距。 |
| `PaneStrategy.plan(bp, slots)` | 解析某宽度类下实际显示的 `PaneSlot` 列表与栏距。 |
| `PaneStrategy.countForWidth(widthDp)` | 某原始宽度允许的栏数（尚未按 slot 截断）。 |
| `Foldables.splitVertically(...)` | 竖铰链下把可用宽度分给左右窗格，并保留安全缝；窗格不足时返回不可分。 |
| `Foldables.sideSheetWidth(...)` | 为 `DockedSheet` 计算单侧窗格的可用宽度。 |
| `HingeInfo` | 铰链姿态、方向、缝宽与 bounds 的不可变几何模型。 |
| `AdaptiveScaffold` | 自适应外壳：按 `Bp` 在 BottomBar / Rail / Drawer 间切换，并提供内容槽。 |
| `MultiPane` | 1/2/3 栏主从布局；内容宽度不足时主动收起预览栏，避免详情被压扁。 |
| `SearchLayout` | 搜索/结果/预览/筛选四槽布局，按实际约束逐级收起尾部栏。 |
| `DockedSheet` | 抽屉面板；有铰链时停靠在单侧窗格，绝不跨缝。 |
| `FoldablePane` | 双窗格容器；窗格不足时退回单栏。 |

## 三、主题与标尺

| API | 释义 |
| --- | --- |
| `MullionTheme(darkTheme, content)` | 注入明暗两套语义 token、Material 3 colorScheme、字体与圆角；默认跟随系统。 |
| `MullionColors` | Composable token 门面：`Background / Ink / Volt / Signal / Mint / Amber / Edge` 等。 |
| `MullionPalette` | 一套主题的不可变调色板；`LightMullionPalette` / `DarkMullionPalette`。 |
| `mullionDimens()` | 当前响应式标尺：页面最大宽度、页边距、卡片内边距、栏距、导航尺寸、对话框上限。 |
| `MullionDimens.forBp(bp, fontScale)` | 纯函数：由宽度类派生尺寸，业务不传 dp。 |
| `Modifier.glass(shape, level)` | 玻璃配方：裁切 + 三层 tint 渐变 + 1dp 边框 + 顶部发丝高光。 |
| `MullionCard` | 玻璃卡片；`contentPadding` 默认读当前标尺。 |
| `MullionTextStyles` | `UI / UISmall / UILabel / Body / Mono` 等文本样式；业务默认用 Sans。 |

## 四、导航与反馈

| API | 释义 |
| --- | --- |
| `OrbitNav(items, selectedIndex, onSelect)` | 一套导航数据，三种形态（BottomBar / Rail / Drawer）；宽度变化不丢选中态。 |
| `OrbitNavItem(label, badge)` | 导航目的地；badge 用于告警/未读数。 |
| `AdaptiveDialog` | 同一弹窗体：窄屏居中、宽屏变侧滑面板，支持 scrim/返回键/焦点。 |
| `PermissionSheet` | 权限解释面板；确认后由宿主发起真实系统权限请求。 |
| `MullionSnackbarHost` | 自适应落位的 Snackbar：底栏窗口贴底、Rail 居中、Drawer 靠右。 |
| `DraggableDivider` | 可拖拽分栏分隔条，带最小 pane 宽度约束。 |
| `SharedAxisTransition` | 页面/内容切换的共享轴转场。 |
| `EdgeToEdgeBox` | 统一解析状态栏、导航栏、IME inset；可选是否给内容加 padding。 |

## 五、基础组件

`MullionButton`、`MullionTextField`、`MullionProgressBar`、`MullionCheckbox` / `MullionRadio`、`MullionSwitch`、`MullionSlider`、`MullionSegmentedControl`、`MullionBadge`、`MullionAvatar`、`MullionCard`、`MullionTabs`、`MullionSnackbar`。

完整参数、默认值与释义见 [Compose API](mullion-compose.md)。

## 六、View 层（Android-only）

| API | 释义 |
| --- | --- |
| `MullionBreakpointLayout` | XML 容器：按子 View 的 `mullionMinWidthDp / mullionMaxWidthDp` 显隐。 |
| `MullionPaneLayout` | XML 容器：按宽度自动 1/2/3 栏，低于最小 pane 宽度时降栏。 |
| `MullionGlassLayout` | 玻璃背景 ViewGroup，渐变/边框随明暗主题解析。 |
| `MullionCardView` / `MullionButtonView` | 可直接放进既有 XML 的卡片与按钮。 |
| `ResourceScaler` | 按当前宽度选 `values / w600dp / w840dp` 资源桶。 |
| `MullionEdgeToEdge` | View 层 edge-to-edge：透明系统栏 + inset 监听。 |
| `MullionPalette.of(context)` | View 侧的明暗 token 解析入口。 |

完整签名见 [View API](mullion-view.md)。
