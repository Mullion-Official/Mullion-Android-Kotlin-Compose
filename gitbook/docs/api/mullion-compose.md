# MULLION COMPOSE API

> 本页由 `gitbook/scripts/gen_api_docs.py` 从 `mullion-compose` 源码自动生成：
> 每个公开符号给出 Kotlin 签名与释义（释义取自源码 KDoc，含 `@param` / `@return`）。

共 **130** 个公开符号，按包与源文件分组。

## 包 `dev.mullion.compose`

### `MullionCompose.kt`

#### MullionCompose

```kotlin
public object MullionCompose
```

Compose entry point retained for binary-safe builds during early bootstrap.

## 包 `dev.mullion.compose.adaptive`

### `AdaptiveDialog.kt`

#### DialogKind

```kotlin
public enum class DialogKind
```

How an adaptive dialog chooses its container.

#### AdaptiveDialog

```kotlin
@Composable
public fun AdaptiveDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    kind: DialogKind = DialogKind.Auto,
    dismissOnScrimClick: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
)
```

One dialog body, two containers. On phones the body is a centered glass card. From [Bp.Large] it becomes a side panel so the underlying list stays visible and useful. Back and scrim behavior are identical in both forms, and focus moves into the panel when it opens. The dialog is an **inline overlay**: place it in a `Box` after the screen content. This keeps it inside a simulation viewport (and inside a foldable leaf) instead of escaping into a platform window.

| 参数 | 说明 |
| --- | --- |
| `visible` | drives the entry and exit animations. |

### `AdaptiveScaffold.kt`

#### AdaptiveScaffold

```kotlin
@Composable
public fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    mode: NavigationMode? = null,
    drawerOpen: Boolean = true,
    bottomBarInset: Dp = 0.dp,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    rail: @Composable () -> Unit = {},
    drawer: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
)
```

The outer shell. It decides *where* navigation lives, never *what* it is. The three morphologies share one content call site, so state inside [content] survives a width change from phone to tablet. Callers write no width checks.

| 参数 | 说明 |
| --- | --- |
| `mode` | optional override for previews; defaults to the window's mode. |
| `drawerOpen` | whether the wide drawer is expanded. When `false`, [rail] is shown instead, so a device can collapse navigation without leaving the wide class. |
| `topBar` | slot drawn above the content in every mode. |
| `bottomBar` | slot used in [NavigationMode.BottomBar]. |
| `rail` | slot used in [NavigationMode.Rail] and collapsed drawers. |
| `drawer` | slot used in [NavigationMode.Drawer]. |
| `content` | the only content call site; receives zero padding today, and is kept for API symmetry with Material scaffolds. |

### `DockedSheet.kt`

#### DockSide

```kotlin
public enum class DockSide
```

Where a [DockedSheet] is allowed to dock.

#### DockedSheet

```kotlin
@Composable
public fun DockedSheet(
    open: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hinge: HingeInfo? = null,
    dockSide: DockSide = DockSide.Auto,
    minSheetWidth: Dp = 140.dp,
    maxSheetWidth: Dp = 420.dp,
    title: (@Composable () -> Unit)? = null,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit,
)
```

A sheet that never draws across a physical hinge. With a vertical, separating hinge the sheet docks to one leaf and is sized to fit inside it. With a horizontal hinge or no hinge it degrades to a bottom panel, capped so it cannot swallow the screen. The content underneath stays composed; opening and closing only moves the sheet.

| 参数 | 说明 |
| --- | --- |
| `hinge` | pass an explicit hinge for previews; defaults to the window's. |
| `minSheetWidth` | below this width a side dock is rejected and the sheet falls back to the bottom. |
| `sheetContent` | receives a [ColumnScope] so actions can align naturally. |

### `DraggableDivider.kt`

#### DraggableSplitState

```kotlin
@Stable
public class DraggableSplitState(initialFraction: Float = 0.42f)
```

Split position holder, saved across configuration changes. The fraction is clamped to `0.02..0.98`; the component applies a stricter clamp derived from [DraggableDivider.minPaneWidth] at layout time.

#### fraction

```kotlin
public var fraction: Float
```

Current split, `0f` = all second pane, `1f` = all first pane.

#### reset

```kotlin
public fun reset()
```

Restores the cabinet's default 42 / 58 split.

#### Saver

```kotlin
public val Saver: Saver<DraggableSplitState, Float> = Saver(
    save = { it.fraction },
    restore = { DraggableSplitState(it) },
)
```

State restoration used by [rememberDraggableSplitState].

#### rememberDraggableSplitState

```kotlin
@Composable
public fun rememberDraggableSplitState(initialFraction: Float = 0.42f): DraggableSplitState =
```

Creates a split state that survives rotation and process death.

#### DraggableDivider

```kotlin
@Composable
public fun DraggableDivider(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: DraggableSplitState = rememberDraggableSplitState(),
    minPaneWidth: Dp = 120.dp,
    dividerWidth: Dp = 11.dp,
    onSplitChange: ((left: Dp, right: Dp) -> Unit)? = null,
)
```

A draggable two-pane split with a hard minimum pane width. The divider is 11dp wide for the pointer, but only 5dp of it is painted. Drag updates are clamped before they reach layout, so a pane can never become unreadable; releasing simply stays at the clamp. The split is advertised to accessibility services as two custom actions.

| 参数 | 说明 |
| --- | --- |
| `onSplitChange` | reports the resolved leaf widths after every layout. |

### `EdgeToEdgeBox.kt`

#### EdgeToEdgeBox

```kotlin
@Composable
public fun EdgeToEdgeBox(
    modifier: Modifier = Modifier,
    includeIme: Boolean = true,
    applyPadding: Boolean = true,
    simulatedInsets: MullionInsets? = null,
    content: @Composable (PaddingValues) -> Unit,
)
```

The one place window insets are resolved. Status bar, gesture bar and IME are folded into a single [PaddingValues]. API 21 and API 35 behave the same because the calculation goes through Compose's compat window insets, never through reflection.

| 参数 | 说明 |
| --- | --- |
| `includeIme` | whether the keyboard should push content. |
| `applyPadding` | if `true`, the box itself pads its content. Set to `false` when the caller wants to draw chrome behind the inset bands (as the cabinet does) and position children with the supplied values. |
| `simulatedInsets` | overrides the real insets, for previews and the sample. |

### `FoldablePane.kt`

#### FoldablePane

```kotlin
@Composable
public fun FoldablePane(
    start: @Composable () -> Unit,
    end: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    hinge: HingeInfo? = null,
    safeGap: Dp = 24.dp,
    minPaneWidth: Dp = 240.dp,
    singlePane: (@Composable () -> Unit)? = null,
    onHingeChanged: ((HingeInfo) -> Unit)? = null,
)
```

Two leaves separated by a physical seam. Geometry comes from [HingeInfo]: each leaf gets an equal share of the width minus the seam and a 24dp safe gap on both sides of it. Nothing is drawn over the seam. When the available width cannot honor [minPaneWidth], the caller's [singlePane] fallback is shown instead of squeezing both leaves. A flat foldable renders as one continuous surface, which is the point: the layout should not flicker when a device opens to 180°.

| 参数 | 说明 |
| --- | --- |
| `hinge` | the physical seam. Defaults to the window's detected hinge. |
| `onHingeChanged` | called whenever the posture or orientation changes. |

### `MultiPane.kt`

#### MultiPane

```kotlin
@Composable
public fun MultiPane(
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    preview: (@Composable () -> Unit)? = null,
    showDetail: Boolean = false,
    onBack: () -> Unit = {},
    strategy: PaneStrategy = PaneStrategy(),
)
```

List / detail / preview, resolved by a [PaneStrategy] and never by ad-hoc width conditions. At one pane, selecting an item replaces the list with the detail; back returns to the list. At two panes, list and detail are both always visible. At three, the preview docks on the trailing edge. The transition between one-pane states uses the shared X axis.

| 参数 | 说明 |
| --- | --- |
| `showDetail` | only consulted at one pane. Wide layouts always show detail. |
| `onBack` | invoked by the system back gesture while the detail covers the list. |

### `OrbitNav.kt`

#### OrbitNavItem

```kotlin
public data class OrbitNavItem(
    public val label: String,
    public val badge: String? = null,
    public val enabled: Boolean = true,
)
```

One destination in an [OrbitNav]. Routes stay in the caller's domain.

#### OrbitNav

```kotlin
@Composable
public fun OrbitNav(
    items: List<OrbitNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    mode: NavigationMode? = null,
    header: (@Composable () -> Unit)? = null,
    icon: (@Composable (index: Int, selected: Boolean) -> Unit)? = null,
)
```

One navigation data source, three morphologies. The selection index is the same in every mode; switching width never loses which destination is active. Screens should declare [items] once and let the window decide whether they render as a bottom bar, a rail or a drawer.

| 参数 | 说明 |
| --- | --- |
| `mode` | optional override used by previews and the sample cabinet. |
| `icon` | optional leading glyph. When omitted, the first character of the label is used as an orbit marker. |

### `PermissionSheet.kt`

#### MullionPermission

```kotlin
public data class MullionPermission(
    /** Human-readable capability, for example "相机". */
    public val label: String,
    /** The platform permission name, shown as a monospace hint. */
    public val permission: String,
    /** Optional explanation rendered under the permission. */
    public val rationale: String? = null,
    /** Required permissions cannot be skipped, only denied as a group. */
    public val required: Boolean = false,
)
```

A permission request presented as a row of capabilities. The sheet never asks for permissions itself. The host owns the platform request; this component only gives the request a shape that changes with the window: a bottom sheet on compact widths, a trigger-anchored panel on wider ones. The focus target and back behavior are the same in both forms.

#### PermissionAnchor

```kotlin
public enum class PermissionAnchor
```

Where a [PermissionSheet] is anchored.

#### PermissionSheet

```kotlin
@Composable
public fun PermissionSheet(
    visible: Boolean,
    permissions: List<MullionPermission>,
    onDismissRequest: () -> Unit,
    onGrantAll: () -> Unit,
    onDeny: () -> Unit,
    modifier: Modifier = Modifier,
    anchor: PermissionAnchor = PermissionAnchor.Auto,
    title: String = "权限清单",
    confirmLabel: String = "全部允许",
    dismissLabel: String = "仅本次",
    anchorOffset: DpOffset = DpOffset(24.dp, 72.dp),
)
```

The permission panel.

| 参数 | 说明 |
| --- | --- |
| `anchorOffset` | where an anchored panel starts, relative to its parent. Pass the trigger's position if the trigger lives in the same `Box`. |

### `SearchLayout.kt`

#### MullionSearchBar

```kotlin
@Composable
public fun MullionSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索组件 / 断点 / 设备…",
    onSearch: (() -> Unit)? = null,
)
```

A glass search field with a focus ring and a clear affordance. It is deliberately free of navigation concerns: place it in a toolbar or above [SearchLayout].

#### SearchLayout

```kotlin
@Composable
public fun SearchLayout(
    query: String,
    onQueryChange: (String) -> Unit,
    results: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索设备 / 型号 / 断点…",
    preview: (@Composable () -> Unit)? = null,
    filters: (@Composable () -> Unit)? = null,
    resultCount: Int? = null,
    strategy: PaneStrategy = PaneStrategy(),
    onSearch: (() -> Unit)? = null,
)
```

Search results that grow a preview pane and then a filter rail. - one pane: the result list only - two panes: results + preview (or filters when no preview is supplied) - three panes: results + preview + filters Query state lives with the caller, so collapsing the window never clears the search.

### `SharedAxisTransition.kt`

#### SharedAxis

```kotlin
public enum class SharedAxis
```

The axis a shared-axis transition travels along.

#### public fun <T> SharedAxisTransition(

```kotlin
@Composable
public fun <T> SharedAxisTransition(
    targetState: T,
    modifier: Modifier = Modifier,
    axis: SharedAxis = SharedAxis.X,
    durationMillis: Int = MullionMotion.PaneMillis,
    content: @Composable (T) -> Unit,
)
```

A single motion grammar for every width-class change. The old pane leaves along one axis while the new pane enters from the opposite side. 28dp is enough to signal direction without turning a re-layout into a carousel. The container is never left blank mid-transition because enter and exit run together.

| 参数 | 说明 |
| --- | --- |
| `durationMillis` | defaults to the framework's 240ms pane duration. |

### `SizeClassText.kt`

#### SizeClassText

```kotlin
@Composable
public fun SizeClassText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    color: Color = Color.Unspecified,
    maxMeasureCh: Int? = null,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
)
```

Body copy that steps with the width class instead of stretching with it. Font size and line length change in two discrete steps. The measure is capped at a readable number of `ch` characters, measured from the actual typeface rather than guessed from an average glyph width.

| 参数 | 说明 |
| --- | --- |
| `maxMeasureCh` | optional override for the readable measure. |

### `WindowSizeClass.kt`

#### WindowSizeClass

```kotlin
@Composable
public fun WindowSizeClass(content: @Composable (Bp) -> Unit)
```

A zero-cost slot that exposes the current width class. Screens that need a custom decision can use this instead of reading the window directly, which keeps every breakpoint read in one place.

#### rememberBreakpoint

```kotlin
@Composable
public fun rememberBreakpoint(): Bp = currentBreakpoint()
```

Remembers nothing: the class is already a value in the composition local.

#### mullionMetrics

```kotlin
@Composable
public fun mullionMetrics(): MullionMetrics = MullionMetrics.forBp(currentBreakpoint())
```

The shared ruler for Compose code. These values mirror the `values-w600dp` / `values-w840dp` buckets shipped by `mullion-view`, so a Compose screen and an XML screen resolve identically.

#### mullionResourceBucket

```kotlin
@Composable
public fun mullionResourceBucket(): ResourceBucket =
```

The current resource bucket, useful for analytics and debugging.

#### mullionTypeScale

```kotlin
@Composable
public fun mullionTypeScale(): TypeScale = TypeScale.forBp(currentBreakpoint())
```

The current type step.

#### scaledDp

```kotlin
@Composable
public fun scaledDp(
    compact: Dp,
    medium: Dp = compact,
    expanded: Dp = medium,
    large: Dp = expanded,
    xl: Dp = large,
): Dp = when (currentBreakpoint())
```

Picks a dp value for the current width class. ``` val gap = scaledDp(compact = 12.dp, expanded = 24.dp, large = 32.dp) ```

#### scaledSp

```kotlin
@Composable
public fun scaledSp(
    compact: TextUnit,
    medium: TextUnit = compact,
    expanded: TextUnit = medium,
    large: TextUnit = expanded,
    xl: TextUnit = large,
): TextUnit = when (currentBreakpoint())
```

Picks an sp value for the current width class.

#### MullionResources

```kotlin
public object MullionResources
```

Convenience aliases used by documentation samples.

#### metrics

```kotlin
public fun metrics(bp: Bp): MullionMetrics = MullionMetrics.forBp(bp)
```

*(源码未提供 KDoc；以签名为准。)*

#### typeScale

```kotlin
public fun typeScale(bp: Bp): TypeScale = TypeScale.forBp(bp)
```

*(源码未提供 KDoc；以签名为准。)*

#### sectionSpacing

```kotlin
public fun sectionSpacing(bp: Bp): Dp = metrics(bp).sectionSpacingDp.dp
```

*(源码未提供 KDoc；以签名为准。)*

#### displayType

```kotlin
public fun displayType(bp: Bp): TextUnit = metrics(bp).displayTypeSp.sp
```

*(源码未提供 KDoc；以签名为准。)*

## 包 `dev.mullion.compose.base`

### `MullionAvatar.kt`

#### MullionAvatarSize

```kotlin
public enum class MullionAvatarSize(
    public val size: Dp,
    public val fontSize: TextUnit,
)
```

The three documented avatar sizes.

#### MullionAvatar

```kotlin
@Composable
public fun MullionAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: MullionAvatarSize = MullionAvatarSize.M,
    colorIndex: Int = (name.hashCode() and 0x7FFFFFFF) % MullionColors.AvatarPalette.size,
    image: (@Composable () -> Unit)? = null,
)
```

A letter avatar with a deterministic gradient. No network and no image loader: an initial is always available, and the same name yields the same color on every device. Pass [image] to replace the initials with any composable (for example, an AsyncImage).

#### MullionAvatarStack

```kotlin
@Composable
public fun MullionAvatarStack(
    names: List<String>,
    modifier: Modifier = Modifier,
    size: MullionAvatarSize = MullionAvatarSize.M,
    max: Int = 4,
    overlap: Dp = 12.dp,
    extraCount: Int = 0,
)
```

An overlapping avatar stack with an overflow counter.

| 参数 | 说明 |
| --- | --- |
| `max` | number of full avatars to show before collapsing into `+N`. |

### `MullionBadge.kt`

#### MullionBadgeKind

```kotlin
public enum class MullionBadgeKind
```

Visual kind of a badge.

#### MullionBadge

```kotlin
@Composable
public fun MullionBadge(
    modifier: Modifier = Modifier,
    count: Int? = null,
    text: String? = null,
    kind: MullionBadgeKind = when
        text != null -> MullionBadgeKind.Text
        count != null -> MullionBadgeKind.Number
        else -> MullionBadgeKind.Dot
    },
    contentDescription: String? = null,
    content: @Composable () -> Unit,
)
```

A badge attached to any content: dot, number, or short text. Numbers above 99 collapse to `99+`. The badge is drawn in the top-end corner and offset outward so it never covers the content's own glyph.

### `MullionButton.kt`

#### MullionButtonVariant

```kotlin
public enum class MullionButtonVariant
```

Visual weight of a button.

#### MullionButtonSize

```kotlin
public enum class MullionButtonSize(
    public val height: Dp,
    public val horizontalPadding: Dp,
    public val fontSize: TextUnit,
)
```

Physical size of a button. Touch targets stay 48dp regardless.

#### MullionButton

```kotlin
@Composable
public fun MullionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MullionButtonVariant = MullionButtonVariant.Filled,
    size: MullionButtonSize = MullionButtonSize.M,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
)
```

The primary action control. The visual height can be 36 / 44 / 52dp, but the clickable wrapper is always at least 48dp so the control is reachable. Press feedback is a 3% scale, not a ripple: it survives inside scaled previews and reads as physical travel.

#### MullionIconButton

```kotlin
@Composable
public fun MullionIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MullionButtonVariant = MullionButtonVariant.Filled,
    size: MullionButtonSize = MullionButtonSize.M,
    enabled: Boolean = true,
    contentDescription: String? = null,
    content: @Composable () -> Unit,
)
```

A circular icon action. The foreground is supplied by the caller; MULLION does not ship an icon set, which keeps the library free of a 2 MB material-icons dependency.

### `MullionCard.kt`

#### MullionCard

```kotlin
@Composable
public fun MullionCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    contentPadding: PaddingValues? = null,
    content: @Composable ColumnScope.() -> Unit,
)
```

A glass card, optionally tappable. The card is the only base component that owns a shadow; it establishes the cabinet's depth. Hover and focus are intentionally left to the platform, while press feedback is a subtle scale.

### `MullionProgress.kt`

#### MullionProgressShape

```kotlin
public enum class MullionProgressShape
```

Shape of a progress indicator.

#### MullionProgressBar

```kotlin
@Composable
public fun MullionProgressBar(
    progress: Float?,
    modifier: Modifier = Modifier,
    shape: MullionProgressShape = MullionProgressShape.Linear,
    label: String? = null,
    showValue: Boolean = false,
    circularSize: Dp = 26.dp,
    contentDescription: String? = null,
)
```

Progress, determinate or not. Passing `null` switches to an indeterminate loop. The loop is drawn on a canvas rather than animated layout, so it never triggers a recomposition per frame.

### `MullionSegmentedControl.kt`

#### MullionSegmentedControl

```kotlin
@Composable
public fun MullionSegmentedControl(
    segments: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
)
```

A pill segmented control whose segment count is the caller's decision. MULLION does not hide segments for you: an app that wants 2 segments on a phone and 5 on a tablet computes that list from the width class and passes it in. This keeps the control honest and previewable at any width.

### `MullionSelection.kt`

#### MullionCheckbox

```kotlin
@Composable
public fun MullionCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indeterminate: Boolean = false,
    label: String? = null,
)
```

A checkbox with an explicit 48dp row target. Supports the three visual states MULLION documents: unchecked, checked and indeterminate. The check mark is drawn on a canvas, so it stays crisp at any density and does not depend on an icon font.

#### MullionRadio

```kotlin
@Composable
public fun MullionRadio(
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
)
```

A radio button. Selection semantics are owned by the caller's group; this control reports taps through [onSelect].

### `MullionSlider.kt`

#### MullionSlider

```kotlin
@Composable
public fun MullionSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    label: String? = null,
    valueLabel: ((Float) -> String)? = null,
)
```

A single-cursor slider with optional step snapping. The track is 5dp, the thumb 20dp, but the pointer target is the full 32dp high canvas. Tapping anywhere jumps to that value; dragging scrubs. Accessibility services get a progress range plus increment/decrement actions.

| 参数 | 说明 |
| --- | --- |
| `steps` | number of stops *between* the endpoints, as in Material. `0` means continuous. |
| `valueLabel` | formats the readout; defaults to one decimal place. |

### `MullionSnackbar.kt`

#### MullionSnackbarResult

```kotlin
public enum class MullionSnackbarResult
```

Outcome of a snackbar shown through [MullionSnackbarHostState.showSnackbar].

#### MullionSnackbarData

```kotlin
public class MullionSnackbarData internal constructor(
    internal val id: Long,
    public val message: String,
    public val actionLabel: String?,
    public val durationMillis: Long,
    internal val onAction: (() -> Unit)?,
    internal val completion: CompletableDeferred<MullionSnackbarResult>,
)
```

The currently visible snackbar.

#### MullionSnackbarHostState

```kotlin
@Stable
public class MullionSnackbarHostState
```

A queue-free snackbar state. Only one snackbar is visible at a time; showing a new one dismisses the previous one and resumes its coroutine. The host is responsible for the timer, so a configuration change can re-show the same data without losing it.

#### current

```kotlin
public var current: MullionSnackbarData? by mutableStateOf(null)
```

The snackbar to render, or `null`.

#### dismiss

```kotlin
public fun dismiss(result: MullionSnackbarResult = MullionSnackbarResult.Dismissed)
```

Dismisses the current snackbar, if any.

#### rememberMullionSnackbarHostState

```kotlin
@Composable
public fun rememberMullionSnackbarHostState(): MullionSnackbarHostState =
```

Remembers a [MullionSnackbarHostState] across recompositions.

#### MullionSnackbarHost

```kotlin
@Composable
public fun MullionSnackbarHost(
    state: MullionSnackbarHostState,
    modifier: Modifier = Modifier,
)
```

The snackbar host. Placement is part of the adaptive contract: - bottom bar windows: full width, docked above the bar - rail windows: a centered 420dp capsule - drawer windows: a 292dp capsule anchored to the end Place it last in a `Box` so it overlays the screen. It never steals touches outside the snackbar itself.

### `MullionSwitch.kt`

#### MullionSwitch

```kotlin
@Composable
public fun MullionSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
)
```

A 52×30 switch with a 22dp thumb that travels 22dp. The row is at least 48dp tall so it can be toggled comfortably even when the visual track is small. The thumb's motion uses a spring because a switch is a physical promise; a linear slide feels broken.

### `MullionTabs.kt`

#### MullionTab

```kotlin
public data class MullionTab(
    public val label: String,
    public val badge: String? = null,
)
```

One tab label, with an optional count badge.

#### MullionTabs

```kotlin
@Composable
public fun MullionTabs(
    tabs: List<MullionTab>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    overflowAfter: Int? = null,
)
```

Tabs that scroll when they must and collapse into an overflow menu when asked. The component never guesses the width class. The caller passes `overflowAfter` (often derived from `currentBreakpoint()`) when it wants the "fixed + ⋯" behavior. The selected tab is always kept in the visible group by surfacing its label on the overflow trigger.

| 参数 | 说明 |
| --- | --- |
| `overflowAfter` | number of tabs to keep visible before overflow. `null` disables overflow and scrolls instead. |

### `MullionTextField.kt`

#### MullionFieldState

```kotlin
public enum class MullionFieldState
```

Validation state of a field.

#### MullionTextField

```kotlin
@Composable
public fun MullionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    state: MullionFieldState = MullionFieldState.Default,
    supportingText: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textStyle: TextStyle = MullionTextStyles.UI.copy(fontSize = 12.sp),
)
```

A glass field with a monospace value and an uppercase label. Focus is communicated by a volt border plus a 3dp translucent ring, matching the focus tokens in the design system. The ring is drawn inside the field so it can never clip against a neighboring control.

## 包 `dev.mullion.compose.theme`

### `Glass.kt`

#### GlassLevel

```kotlin
public enum class GlassLevel(
    public val radius: Dp,
    public val topAlpha: Float,
    public val midAlpha: Float,
    public val bottomAlpha: Float,
    public val borderAlpha: Float,
    public val elevation: Dp,
)
```

Glass depth. The deeper the layer, the stronger the fill and the border. Compose cannot sample a live backdrop before Android 12, so MULLION builds glass from translucent gradients, a 1dp light edge and a dark ambient shadow. The result is deterministic on API 21 and identical in previews.

#### Modifier.glass

```kotlin
public fun Modifier.glass(
    shape: Shape = RoundedCornerShape(GlassLevel.Panel.radius),
    level: GlassLevel = GlassLevel.Panel,
    borderColor: Color? = null,
): Modifier = composed
```

Draws a MULLION glass surface onto any modifier chain. The modifier clips, fills with a three-stop gradient, draws the 1dp edge and adds a 1dp highlight along the top edge. It does not impose a size, so it can be used by fields, cards, buttons and sheets alike.

#### GlassSurface

```kotlin
@Composable
public fun GlassSurface(
    modifier: Modifier = Modifier,
    level: GlassLevel = GlassLevel.Panel,
    shape: Shape = RoundedCornerShape(level.radius),
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
)
```

A composable glass container.

| 参数 | 说明 |
| --- | --- |
| `level` | depth of the glass. |
| `shape` | corner shape; defaults to the level's radius. |
| `contentAlignment` | alignment used by callers that add a single child. |

#### GlassLevel.shape

```kotlin
public fun GlassLevel.shape(): Shape = RoundedCornerShape(radius)
```

The default rounded shape for a glass level.

### `MullionTheme.kt`

#### LocalMullionDimens

```kotlin
public val LocalMullionDimens: androidx.compose.runtime.ProvidableCompositionLocal<MullionDimens> =
```

*(源码未提供 KDoc；以签名为准。)*

#### MullionTheme

```kotlin
@Composable
public fun MullionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
)
```

Applies the MULLION adaptive color scheme, type scale, shapes and glass tokens. The default follows the system. Pass [darkTheme] explicitly for a kiosk, a design preview or a screenshot test. Every component reads [LocalMullionPalette] through [MullionColors], so a split-screen window can change theme without rebuilding component code.

### `MullionTokens.kt`

#### MullionPalette

```kotlin
@Immutable
public data class MullionPalette(
    public val background: Color,
    public val backgroundLift: Color,
    public val backgroundDeep: Color,
    public val volt: Color,
    public val voltHigh: Color,
    public val signal: Color,
    public val mint: Color,
    public val amber: Color,
    public val ink: Color,
    public val muted: Color,
    public val faint: Color,
    public val onVolt: Color,
    public val edge: Color,
    public val edgeHigh: Color,
    public val edgeTop: Color,
    public val scrim: Color,
    public val tonal: Color,
    public val tonalPressed: Color,
    public val glassTint: Color,
    public val glassAlphaScale: Float,
    public val avatarPalette: List<List<Color>>,
)
```

Semantic color tokens for one theme. Components never read a raw hex value; they read this palette through [LocalMullionPalette] (via [MullionColors]). The same component therefore works in light, dark and split-screen windows.

#### LightMullionPalette

```kotlin
public val LightMullionPalette: MullionPalette = MullionPalette(
    background = Color(0xFFF5F7FA),
    backgroundLift = Color(0xFFFFFFFF),
    backgroundDeep = Color(0xFFE9EEF4),
    volt = Color(0xFF2563EB),
    voltHigh = Color(0xFF60A5FA),
    signal = Color(0xFFD9534F),
    mint = Color(0xFF0E9F6E),
    amber = Color(0xFFB45309),
    ink = Color(0xFF111827),
    muted = Color(0xFF4B5563),
    faint = Color(0xFF8A94A3),
    onVolt = Color(0xFFFFFFFF),
    edge = Color(0x140F172A),
    edgeHigh = Color(0x29111827),
    edgeTop = Color(0x40FFFFFF),
    scrim = Color(0x660F172A),
    tonal = Color(0x0A0F172A),
    tonalPressed = Color(0x140F172A),
    glassTint = Color(0xFF0F172A),
    glassAlphaScale = 0.55f,
    avatarPalette = listOf(
        listOf(Color(0xFF2563EB), Color(0xFF60A5FA)),
        listOf(Color(0xFFB45309), Color(0xFFF0B35A)),
        listOf(Color(0xFF0E9F6E), Color(0xFF6ED7B0)),
        listOf(Color(0xFF6D5BD0), Color(0xFFA99AF5)),
    ),
)
```

*(源码未提供 KDoc；以签名为准。)*

#### DarkMullionPalette

```kotlin
public val DarkMullionPalette: MullionPalette = MullionPalette(
    background = Color(0xFF0F141B),
    backgroundLift = Color(0xFF171D26),
    backgroundDeep = Color(0xFF202936),
    volt = Color(0xFF6EA8FE),
    voltHigh = Color(0xFFA7C4FF),
    signal = Color(0xFFE07A5F),
    mint = Color(0xFF55C7A3),
    amber = Color(0xFFD6A84B),
    ink = Color(0xFFE9EEF5),
    muted = Color(0xFFA6B0BE),
    faint = Color(0xFF75808F),
    onVolt = Color(0xFF0B1220),
    edge = Color(0x24FFFFFF),
    edgeHigh = Color(0x4DFFFFFF),
    edgeTop = Color(0x42FFFFFF),
    scrim = Color(0x99070C12),
    tonal = Color(0x14FFFFFF),
    tonalPressed = Color(0x24FFFFFF),
    glassTint = Color(0xFFFFFFFF),
    glassAlphaScale = 1f,
    avatarPalette = listOf(
        listOf(Color(0xFF6EA8FE), Color(0xFFA7C4FF)),
        listOf(Color(0xFFD6A84B), Color(0xFFE07A5F)),
        listOf(Color(0xFF55C7A3), Color(0xFF9BDFC8)),
        listOf(Color(0xFFA99AF5), Color(0xFF6D5BD0)),
    ),
)
```

*(源码未提供 KDoc；以签名为准。)*

#### LocalMullionPalette

```kotlin
public val LocalMullionPalette: androidx.compose.runtime.ProvidableCompositionLocal<MullionPalette> =
```

*(源码未提供 KDoc；以签名为准。)*

#### MullionColors

```kotlin
public object MullionColors
```

Backwards-compatible token facade. Existing component code keeps writing `MullionColors.Ink`; because these are composable getters, each read resolves the palette of the current theme.

#### Background

```kotlin
public val Background: Color @Composable get() = LocalMullionPalette.current.background
```

*(源码未提供 KDoc；以签名为准。)*

#### BackgroundLift

```kotlin
public val BackgroundLift: Color @Composable get() = LocalMullionPalette.current.backgroundLift
```

*(源码未提供 KDoc；以签名为准。)*

#### BackgroundDeep

```kotlin
public val BackgroundDeep: Color @Composable get() = LocalMullionPalette.current.backgroundDeep
```

*(源码未提供 KDoc；以签名为准。)*

#### Volt

```kotlin
public val Volt: Color @Composable get() = LocalMullionPalette.current.volt
```

*(源码未提供 KDoc；以签名为准。)*

#### VoltHigh

```kotlin
public val VoltHigh: Color @Composable get() = LocalMullionPalette.current.voltHigh
```

*(源码未提供 KDoc；以签名为准。)*

#### Signal

```kotlin
public val Signal: Color @Composable get() = LocalMullionPalette.current.signal
```

*(源码未提供 KDoc；以签名为准。)*

#### Mint

```kotlin
public val Mint: Color @Composable get() = LocalMullionPalette.current.mint
```

*(源码未提供 KDoc；以签名为准。)*

#### Amber

```kotlin
public val Amber: Color @Composable get() = LocalMullionPalette.current.amber
```

*(源码未提供 KDoc；以签名为准。)*

#### Ink

```kotlin
public val Ink: Color @Composable get() = LocalMullionPalette.current.ink
```

*(源码未提供 KDoc；以签名为准。)*

#### Muted

```kotlin
public val Muted: Color @Composable get() = LocalMullionPalette.current.muted
```

*(源码未提供 KDoc；以签名为准。)*

#### Faint

```kotlin
public val Faint: Color @Composable get() = LocalMullionPalette.current.faint
```

*(源码未提供 KDoc；以签名为准。)*

#### OnVolt

```kotlin
public val OnVolt: Color @Composable get() = LocalMullionPalette.current.onVolt
```

*(源码未提供 KDoc；以签名为准。)*

#### Edge

```kotlin
public val Edge: Color @Composable get() = LocalMullionPalette.current.edge
```

*(源码未提供 KDoc；以签名为准。)*

#### EdgeHigh

```kotlin
public val EdgeHigh: Color @Composable get() = LocalMullionPalette.current.edgeHigh
```

*(源码未提供 KDoc；以签名为准。)*

#### EdgeTop

```kotlin
public val EdgeTop: Color @Composable get() = LocalMullionPalette.current.edgeTop
```

*(源码未提供 KDoc；以签名为准。)*

#### Scrim

```kotlin
public val Scrim: Color @Composable get() = LocalMullionPalette.current.scrim
```

*(源码未提供 KDoc；以签名为准。)*

#### GlassTop

```kotlin
public val GlassTop: Color @Composable get() = LocalMullionPalette.current.glassTint.copy(alpha = 0.17f)
```

*(源码未提供 KDoc；以签名为准。)*

#### GlassMid

```kotlin
public val GlassMid: Color @Composable get() = LocalMullionPalette.current.glassTint.copy(alpha = 0.07f)
```

*(源码未提供 KDoc；以签名为准。)*

#### GlassBottom

```kotlin
public val GlassBottom: Color @Composable get() = LocalMullionPalette.current.glassTint.copy(alpha = 0.10f)
```

*(源码未提供 KDoc；以签名为准。)*

#### Tonal

```kotlin
public val Tonal: Color @Composable get() = LocalMullionPalette.current.tonal
```

*(源码未提供 KDoc；以签名为准。)*

#### TonalPressed

```kotlin
public val TonalPressed: Color @Composable get() = LocalMullionPalette.current.tonalPressed
```

*(源码未提供 KDoc；以签名为准。)*

#### AvatarPalette

```kotlin
public val AvatarPalette: List<List<Color>> @Composable get() = LocalMullionPalette.current.avatarPalette
```

*(源码未提供 KDoc；以签名为准。)*

#### Transparent

```kotlin
public val Transparent: Color = Color(0x00000000)
```

Constant, not theme dependent.

#### MullionFonts

```kotlin
public object MullionFonts
```

Named type families used across the components.

#### Sans

```kotlin
public val Sans: FontFamily = FontFamily.SansSerif
```

*(源码未提供 KDoc；以签名为准。)*

#### Mono

```kotlin
public val Mono: FontFamily = FontFamily.Monospace
```

*(源码未提供 KDoc；以签名为准。)*

#### MullionTextStyles

```kotlin
public object MullionTextStyles
```

Reusable text styles. [UI], [UISmall] and [UILabel] are the product-facing defaults. [Mono] remains available for IDs, logs and device readouts, but it is not the voice of the whole application.

#### Label

```kotlin
public val Label: TextStyle = TextStyle(
    fontFamily = MullionFonts.Sans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    letterSpacing = 0.4.sp,
    lineHeight = 14.sp,
)
```

*(源码未提供 KDoc；以签名为准。)*

#### LabelSmall

```kotlin
public val LabelSmall: TextStyle = Label.copy(fontSize = 9.sp, letterSpacing = 0.3.sp)
```

*(源码未提供 KDoc；以签名为准。)*

#### UI

```kotlin
public val UI: TextStyle = TextStyle(
    fontFamily = MullionFonts.Sans,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 19.sp,
)
```

*(源码未提供 KDoc；以签名为准。)*

#### UISmall

```kotlin
public val UISmall: TextStyle = UI.copy(fontSize = 11.sp, lineHeight = 16.sp)
```

*(源码未提供 KDoc；以签名为准。)*

#### UILabel

```kotlin
public val UILabel: TextStyle = UI.copy(
    fontSize = 11.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 16.sp,
)
```

*(源码未提供 KDoc；以签名为准。)*

#### Readout

```kotlin
public val Readout: TextStyle = TextStyle(
    fontFamily = MullionFonts.Sans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
)
```

*(源码未提供 KDoc；以签名为准。)*

#### Mono

```kotlin
public val Mono: TextStyle = TextStyle(
    fontFamily = MullionFonts.Mono,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 18.sp,
)
```

*(源码未提供 KDoc；以签名为准。)*

#### MonoSmall

```kotlin
public val MonoSmall: TextStyle = Mono.copy(fontSize = 10.5.sp, lineHeight = 16.sp)
```

*(源码未提供 KDoc；以签名为准。)*

#### Body

```kotlin
public val Body: TextStyle = TextStyle(
    fontFamily = MullionFonts.Sans,
    fontSize = 15.sp,
    lineHeight = 25.sp,
    letterSpacing = 0.1.sp,
)
```

*(源码未提供 KDoc；以签名为准。)*

#### BodyLarge

```kotlin
public val BodyLarge: TextStyle = Body.copy(fontSize = 18.sp, lineHeight = 28.sp)
```

*(源码未提供 KDoc；以签名为准。)*

#### MullionMotion

```kotlin
public object MullionMotion
```

Motion is part of the structure.

#### Settle

```kotlin
public val Settle: Easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
```

*(源码未提供 KDoc；以签名为准。)*

#### Standard

```kotlin
public val Standard: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
```

*(源码未提供 KDoc；以签名为准。)*

#### NavMorph

```kotlin
public val NavMorph: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
```

*(源码未提供 KDoc；以签名为准。)*

#### MullionDimens

```kotlin
@Immutable
public data class MullionDimens(
    public val touchTarget: Dp = 48.dp,
    public val control: Dp = 44.dp,
    public val controlSmall: Dp = 36.dp,
    public val controlLarge: Dp = 52.dp,
    public val field: Dp = 42.dp,
    public val radiusCard: Dp = 12.dp,
    public val radiusPanel: Dp = 18.dp,
    public val radiusWindow: Dp = 28.dp,
    public val gutter: Dp = 24.dp,
    public val railWidth: Dp = 80.dp,
    public val drawerWidth: Dp = 216.dp,
    public val dividerWidth: Dp = 11.dp,
    /** Maximum readable content width for a page. */
    public val pageMaxWidth: Dp = 1180.dp,
    /** Outer horizontal page padding. */
    public val screenPadding: Dp = 16.dp,
    /** Inner card padding. */
    public val cardPadding: Dp = 16.dp,
    /** Vertical gap between major sections. */
    public val sectionGap: Dp = 16.dp,
    /** Height reserved for a floating bottom navigation bar. */
    public val bottomBarInset: Dp = 88.dp,
    /** Height of an item in the bottom bar / rail / drawer. */
    public val navItemHeight: Dp = 52.dp,
    public val railItemHeight: Dp = 62.dp,
    public val drawerItemHeight: Dp = 44.dp,
    public val dialogMinWidth: Dp = 280.dp,
    public val dialogMaxWidth: Dp = 520.dp,
    public val dialogScreenPadding: Dp = 16.dp,
    public val sheetMaxWidth: Dp = 420.dp,
    public val paneDetailMin: Dp = 320.dp,
)
```

Layout geometry shared by controls and shells. These are the *only* places dp numbers should be authored. Screens should not pass `padding = 16.dp` or `maxWidth = 1180.dp`; they read the current [MullionDimens] with [mullionDimens], which is derived from the active [Bp], the window width and the font scale.

#### Default

```kotlin
public val Default: MullionDimens = MullionDimens()
```

*(源码未提供 KDoc；以签名为准。)*

#### forBp

```kotlin
public fun forBp(bp: Bp, fontScale: Float = 1f): MullionDimens
```

Derives the ruler from the window class. Values are steps, not a linear function of width, so typography and touch targets stay sane at every size.

#### mullionDimens

```kotlin
@Composable
public fun mullionDimens(): MullionDimens = LocalMullionDimens.current
```

The current responsive ruler.

## 包 `dev.mullion.compose.window`

### `MullionWindow.kt`

#### LocalMullionWindow

```kotlin
public val LocalMullionWindow: androidx.compose.runtime.ProvidableCompositionLocal<MullionWindowState?> =
```

The window state seen by every MULLION composable. `null` means "measure the real window". The sample cabinet overrides this local to simulate 375 / 760 / 1180dp viewports without rebuilding the tree.

#### rememberMullionWindowState

```kotlin
@Composable
public fun rememberMullionWindowState(): MullionWindowState
```

Measures the real Compose window, including system insets and fold geometry. The result is a single immutable value: system bars, IME, window size and hinge arrive together, so a resize produces one recomposition instead of four.

#### mullionWindow

```kotlin
@Composable
public fun mullionWindow(): MullionWindowState
```

Returns the window state: a composition-local override when present, the real window otherwise.

#### MullionWindowProvider

```kotlin
@Composable
public fun MullionWindowProvider(
    window: MullionWindowState? = null,
    content: @Composable () -> Unit,
)
```

Provides a window state to a subtree. Pass a value to simulate a viewport (the sample does this for every tray), or omit it to measure the real window.

#### currentBreakpoint

```kotlin
@Composable
public fun currentBreakpoint(): Bp = mullionWindow().bp
```

The current width class. This is the only breakpoint call a screen needs.

#### currentNavigationMode

```kotlin
@Composable
public fun currentNavigationMode(): NavigationMode = mullionWindow().navigationMode
```

The current navigation morphology.

#### currentHinge

```kotlin
@Composable
public fun currentHinge(): HingeInfo = mullionWindow().hinge
```

The current hinge, or [HingeInfo.None].

#### currentInsets

```kotlin
@Composable
public fun currentInsets(): MullionInsets = mullionWindow().insets
```

The current system insets, including the IME.

#### currentWindowWidthDp

```kotlin
@Composable
public fun currentWindowWidthDp(): Int = mullionWindow().widthDp
```

The window width in dp, including system insets.
