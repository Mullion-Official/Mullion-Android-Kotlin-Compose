# MULLION CORE API

> 本页由 `gitbook/scripts/gen_api_docs.py` 从 `mullion-core` 源码自动生成：
> 每个公开符号给出 Kotlin 签名与释义（释义取自源码 KDoc，含 `@param` / `@return`）。

共 **94** 个公开符号，按包与源文件分组。

## 包 `dev.mullion.core`

### `Bp.kt`

#### Bp

```kotlin
public enum class Bp(
    public val minWidthDp: Int,
    public val baselineWidthDp: Int,
    public val label: String,
)
```

The five width classes MULLION is built around. The thresholds are ordinary compile-time constants. [of] is a pure `when` expression, so the Kotlin compiler can inline and constant-fold it. There is no reflection and no resource lookup on the hot path. ``` 0 ─────── 480 ─────── 600 ─────── 840 ─────── 1200 ──────▶ dp Compact   Medium     Expanded     Large        XL 1 pane    1 pane     2 panes      3 panes      3 panes bottom    bottom     rail         drawer       drawer ``` [minWidthDp] is the lower bound of the class. [baselineWidthDp] is the design width used by the cabinet rail; [Compact] starts at 0 but is tuned from 320dp.

#### isCompact

```kotlin
public val isCompact: Boolean
```

`true` for phones held in portrait.

#### isExpanded

```kotlin
public val isExpanded: Boolean
```

`true` when more than one primary pane can be shown.

#### next

```kotlin
public val next: Bp?
```

The next class, or `null` for [XL].

#### previous

```kotlin
public val previous: Bp?
```

The previous class, or `null` for [Compact].

#### THRESHOLDS_DP

```kotlin
public val THRESHOLDS_DP: List<Int> = listOf(480, 600, 840, 1200)
```

All lower-bound thresholds, ascending.

#### DESIGN_TICKS

```kotlin
public val DESIGN_TICKS: List<Int> = listOf(320, 480, 600, 840, 1200)
```

Rail ticks used by the sample and documentation.

#### of

```kotlin
public fun of(widthDp: Int): Bp = when
```

Classifies a window width. Negative widths are treated as zero so a surface that has not been measured yet still resolves to a legal class.

#### of

```kotlin
public fun of(widthDp: Float): Bp = of(widthDp.toInt())
```

Float convenience overload; rounds toward zero class boundaries.

#### ceiling

```kotlin
public fun ceiling(widthDp: Int): Bp = nextUp(of(widthDp)) ?: XL
```

The class immediately above [widthDp], or [XL].

#### floor

```kotlin
public fun floor(widthDp: Int): Bp = of(widthDp)
```

The class immediately below [widthDp], or [Compact].

#### nextUp

```kotlin
public fun nextUp(bp: Bp): Bp? = bp.next
```

Returns the next wider class, or `null` at [XL].

### `Foldables.kt`

#### HingeOrientation

```kotlin
public enum class HingeOrientation
```

Physical orientation of a fold or hinge.

#### HingePosture

```kotlin
public enum class HingePosture
```

The three postures MULLION gives first-class layout semantics.

#### DpRect

```kotlin
public data class DpRect(
    public val left: Float,
    public val top: Float,
    public val right: Float,
    public val bottom: Float,
)
```

A dp rectangle, origin at the top-left of the window.

#### width

```kotlin
public val width: Float
```

*(源码未提供 KDoc；以签名为准。)*

#### height

```kotlin
public val height: Float
```

*(源码未提供 KDoc；以签名为准。)*

#### centerX

```kotlin
public val centerX: Float
```

*(源码未提供 KDoc；以签名为准。)*

#### centerY

```kotlin
public val centerY: Float
```

*(源码未提供 KDoc；以签名为准。)*

#### HingeInfo

```kotlin
public data class HingeInfo(
    public val posture: HingePosture = HingePosture.Flat,
    public val orientation: HingeOrientation? = null,
    public val separatorWidthDp: Float = 0f,
    public val separatorHeightDp: Float = 0f,
    public val bounds: DpRect? = null,
)
```

A physical hinge, in dp. [separatorWidthDp] and [separatorHeightDp] describe the dead area that content must not cross.

#### isSeparating

```kotlin
public val isSeparating: Boolean
```

`true` when the hinge occupies physical space.

#### isVerticalSeparator

```kotlin
public val isVerticalSeparator: Boolean
```

`true` when the seam is vertical and separating.

#### isHorizontalSeparator

```kotlin
public val isHorizontalSeparator: Boolean
```

`true` when the seam is horizontal and separating.

#### withPosture

```kotlin
public fun withPosture(posture: HingePosture): HingeInfo = copy(posture = posture)
```

Returns this hinge with a new posture.

#### None

```kotlin
public val None: HingeInfo = HingeInfo()
```

No hinge, or a fully flat foldable.

#### vertical

```kotlin
public fun vertical(
    separatorWidthDp: Float = 0f,
    posture: HingePosture = HingePosture.HalfOpened,
): HingeInfo = HingeInfo(
    posture = posture,
    orientation = HingeOrientation.Vertical,
    separatorWidthDp = separatorWidthDp,
)
```

A vertical book-style fold.

#### horizontal

```kotlin
public fun horizontal(
    separatorHeightDp: Float = 0f,
    posture: HingePosture = HingePosture.HalfOpened,
): HingeInfo = HingeInfo(
    posture = posture,
    orientation = HingeOrientation.Horizontal,
    separatorHeightDp = separatorHeightDp,
)
```

A horizontal clamshell fold.

#### FoldSplit

```kotlin
public data class FoldSplit(
    public val totalWidthDp: Float,
    public val paneWidthDp: Float,
    public val hingeWidthDp: Float,
    public val gutterDp: Float,
)
```

The result of splitting a width around a vertical hinge. [paneWidthDp] is the width available to each of the two leaves. [gutterDp] is the breathing room kept on each side of the physical seam. Panes are never asked to draw over [hingeStartDp]..[hingeEndDp].

#### startPaneStartDp

```kotlin
public val startPaneStartDp: Float
```

Left edge of the first leaf.

#### startPaneEndDp

```kotlin
public val startPaneEndDp: Float
```

Right edge of the first leaf.

#### hingeStartDp

```kotlin
public val hingeStartDp: Float
```

Left edge of the separator (including its safe gutter when separating).

#### hingeEndDp

```kotlin
public val hingeEndDp: Float
```

Right edge of the separator.

#### endPaneStartDp

```kotlin
public val endPaneStartDp: Float
```

Left edge of the second leaf.

#### endPaneEndDp

```kotlin
public val endPaneEndDp: Float
```

Right edge of the second leaf.

#### fits

```kotlin
public fun fits(minPaneDp: Float): Boolean = paneWidthDp >= minPaneDp
```

`true` when both leaves are at least [minPaneDp] wide.

#### Foldables

```kotlin
public object Foldables
```

Hinge-aware geometry helpers shared by every layout surface.

#### splitVertically

```kotlin
public fun splitVertically(
    availableWidthDp: Float,
    hinge: HingeInfo?,
    safeGapDp: Float = DEFAULT_SAFE_GAP_DP,
): FoldSplit
```

Splits [availableWidthDp] into two equal leaves around a vertical hinge. A flat or absent hinge produces two equal panes with no dead seam, which keeps content continuous when a device unfolds. A horizontal hinge also yields equal panes; it is the caller's job to switch to a stacked layout.

#### sideSheetWidth

```kotlin
public fun sideSheetWidth(
    availableWidthDp: Float,
    hinge: HingeInfo?,
    fraction: Float = 0.44f,
    minPaneDp: Float = 240f,
    safeGapDp: Float = DEFAULT_SAFE_GAP_DP,
): Float?
```

The width a side sheet should occupy so it never crosses a vertical seam. Returns `null` when a side dock would leave less than [minPaneDp], which tells the caller to fall back to a bottom sheet.

#### canSplit

```kotlin
public fun canSplit(
    availableWidthDp: Float,
    hinge: HingeInfo?,
    minPaneDp: Float,
    safeGapDp: Float = DEFAULT_SAFE_GAP_DP,
): Boolean = splitVertically(availableWidthDp, hinge, safeGapDp).fits(minPaneDp)
```

`true` when a vertical seam leaves enough room for [minPaneDp] on both sides. Used by DockedSheet, FoldablePane and MultiPane to decide between a split layout and a single-pane fallback.

### `Insets.kt`

#### MullionInsets

```kotlin
public data class MullionInsets(
    public val leftDp: Int = 0,
    public val topDp: Int = 0,
    public val rightDp: Int = 0,
    public val bottomDp: Int = 0,
    public val imeDp: Int = 0,
)
```

Window insets expressed in dp so they can be reasoned about without a `Density`. Compose converts px to dp at the edge of the framework.

#### horizontalDp

```kotlin
public val horizontalDp: Int
```

Sum of the horizontal insets.

#### verticalDp

```kotlin
public val verticalDp: Int
```

Sum of the vertical insets, including the IME.

#### effectiveBottomDp

```kotlin
public val effectiveBottomDp: Int
```

The effective bottom padding once the keyboard has claimed space.

#### isEmpty

```kotlin
public val isEmpty: Boolean
```

`true` when every inset is zero.

#### withIme

```kotlin
public fun withIme(imeDp: Int): MullionInsets = copy(imeDp = imeDp.coerceAtLeast(0))
```

Returns insets with the IME replaced.

#### Zero

```kotlin
public val Zero: MullionInsets = MullionInsets()
```

*(源码未提供 KDoc；以签名为准。)*

#### MullionWindowState

```kotlin
public data class MullionWindowState(
    public val widthDp: Int,
    public val heightDp: Int,
    public val insets: MullionInsets = MullionInsets.Zero,
    public val hinge: HingeInfo = HingeInfo.None,
    public val fontScale: Float = 1f,
)
```

The raw measurements a layout needs to adapt. Immutable by design: a window change produces a new value and Compose recomposes exactly once.

#### bp

```kotlin
public val bp: Bp
```

Width class of the full window.

#### navigationMode

```kotlin
public val navigationMode: NavigationMode
```

Navigation morphology for this window.

#### contentWidthDp

```kotlin
public val contentWidthDp: Int
```

Width left after horizontal system insets.

#### contentHeightDp

```kotlin
public val contentHeightDp: Int
```

Height left after the top inset and the effective bottom inset.

#### plan

```kotlin
public fun plan(strategy: PaneStrategy, slots: List<PaneSlot>): PanePlan =
```

Resolves a plan using [strategy] and this window's width class.

#### paneCount

```kotlin
public fun paneCount(strategy: PaneStrategy = PaneStrategy()): Int =
```

Pane count under [strategy].

#### of

```kotlin
public fun of(widthDp: Int, heightDp: Int): MullionWindowState =
```

A state for a window with no insets and no hinge.

### `Metrics.kt`

#### ResourceBucket

```kotlin
public enum class ResourceBucket(
    public val qualifier: String,
    public val label: String,
)
```

Which resource qualifier bucket a width belongs to.

#### forWidth

```kotlin
public fun forWidth(widthDp: Int): ResourceBucket = when
```

Buckets a width without touching the resource system.

#### forBp

```kotlin
public fun forBp(bp: Bp): ResourceBucket = when (bp)
```

Buckets a width class.

#### MullionMetrics

```kotlin
public data class MullionMetrics(
    /** Vertical space between major sections. */
    public val sectionSpacingDp: Int,
    /** Display type size in sp. */
    public val displayTypeSp: Int,
    /** Outer pane margin. */
    public val paneMarginDp: Int,
    /** Space between panes. */
    public val paneGutterDp: Int,
)
```

The shared ruler every MULLION control snaps to. These values match the `values/`, `values-w600dp/` and `values-w840dp/` buckets shipped by `mullion-view`, so Compose and XML resolve to the same numbers.

#### Compact

```kotlin
public val Compact: MullionMetrics = MullionMetrics(
    sectionSpacingDp = 48,
    displayTypeSp = 32,
    paneMarginDp = 16,
    paneGutterDp = 12,
)
```

*(源码未提供 KDoc；以签名为准。)*

#### Medium

```kotlin
public val Medium: MullionMetrics = Compact
```

*(源码未提供 KDoc；以签名为准。)*

#### Expanded

```kotlin
public val Expanded: MullionMetrics = MullionMetrics(
    sectionSpacingDp = 80,
    displayTypeSp = 44,
    paneMarginDp = 24,
    paneGutterDp = 24,
)
```

*(源码未提供 KDoc；以签名为准。)*

#### Large

```kotlin
public val Large: MullionMetrics = MullionMetrics(
    sectionSpacingDp = 120,
    displayTypeSp = 56,
    paneMarginDp = 32,
    paneGutterDp = 24,
)
```

*(源码未提供 KDoc；以签名为准。)*

#### XL

```kotlin
public val XL: MullionMetrics = Large
```

*(源码未提供 KDoc；以签名为准。)*

#### forBp

```kotlin
public fun forBp(bp: Bp): MullionMetrics = when (bp)
```

Metrics for a width class.

#### forWidth

```kotlin
public fun forWidth(widthDp: Int): MullionMetrics = forBp(Bp.of(widthDp))
```

Metrics for a raw window width.

#### forBucket

```kotlin
public fun forBucket(bucket: ResourceBucket): MullionMetrics = when (bucket)
```

Metrics for a resource bucket.

#### TypeScale

```kotlin
public data class TypeScale(
    public val fontSizeSp: Float,
    public val lineHeightSp: Float,
    public val maxMeasureCh: Int,
)
```

Type steps used by `SizeClassText`. Sizes are a step function of the width class, never a linear function of the window width: body copy stays legible, and the measure stays inside the readable range.

#### Compact

```kotlin
public val Compact: TypeScale = TypeScale(15f, 27f, 62)
```

*(源码未提供 KDoc；以签名为准。)*

#### Medium

```kotlin
public val Medium: TypeScale = Compact
```

*(源码未提供 KDoc；以签名为准。)*

#### Expanded

```kotlin
public val Expanded: TypeScale = TypeScale(17f, 30.6f, 68)
```

*(源码未提供 KDoc；以签名为准。)*

#### Large

```kotlin
public val Large: TypeScale = TypeScale(19f, 34.2f, 72)
```

*(源码未提供 KDoc；以签名为准。)*

#### XL

```kotlin
public val XL: TypeScale = Large
```

*(源码未提供 KDoc；以签名为准。)*

#### forBp

```kotlin
public fun forBp(bp: Bp): TypeScale = when (bp)
```

*(源码未提供 KDoc；以签名为准。)*

#### forWidth

```kotlin
public fun forWidth(widthDp: Int): TypeScale = forBp(Bp.of(widthDp))
```

*(源码未提供 KDoc；以签名为准。)*

### `Mullion.kt`

#### Mullion

```kotlin
public object Mullion
```

Coordinates and build metadata for the MULLION visual framework.

### `MullionViewport.kt`

#### MullionViewport

```kotlin
public enum class MullionViewport(
    public val label: String,
    public val widthDp: Int,
    public val heightDp: Int,
)
```

Semantic preview widths for design tools, screenshot tests and sample trays. Product code should not pass raw dp numbers around to trigger adaptation. It should either use the real window, or choose one of these named contexts. The numbers live here, once, next to the breakpoint ruler.

#### bp

```kotlin
public val bp: Bp
```

*(源码未提供 KDoc；以签名为准。)*

#### metrics

```kotlin
public val metrics: MullionMetrics
```

*(源码未提供 KDoc；以签名为准。)*

### `NavigationMode.kt`

#### NavigationMode

```kotlin
public enum class NavigationMode
```

How primary navigation is presented. Navigation is a property of the window, not of a screen. Screens declare the same destinations and MULLION decides whether they become a bottom bar, a rail or a drawer.

#### forBp

```kotlin
public fun forBp(bp: Bp): NavigationMode = when (bp)
```

The single source of truth for navigation morphology. - `0..599dp` → [BottomBar] - `600..839dp` → [Rail] - `840dp+` → [Drawer]

#### forWidth

```kotlin
public fun forWidth(widthDp: Int): NavigationMode = forBp(Bp.of(widthDp))
```

Convenience overload that classifies [widthDp] first.

### `PaneStrategy.kt`

#### PaneSlot

```kotlin
public enum class PaneSlot
```

The slots a multi-pane layout can show. Slots are intentionally generic so the same strategy powers list/detail/preview, search/filters, or any custom three-surface composition.

#### PanePlan

```kotlin
public data class PanePlan(
    public val slots: List<PaneSlot>,
    public val gutterDp: Int,
)
```

A resolved pane composition.

| 参数 | 说明 |
| --- | --- |
| `slots` | the slots that are currently visible, in visual order. |
| `gutterDp` | the resolved space between panes. It comes from the active width bucket unless the caller explicitly overrides it. |

#### count

```kotlin
public val count: Int
```

Number of visible panes.

#### isSinglePane

```kotlin
public val isSinglePane: Boolean
```

`true` when only one pane is visible.

#### shows

```kotlin
public fun shows(slot: PaneSlot): Boolean = slot in slots
```

`true` when [slot] is part of this plan.

#### PaneStrategy

```kotlin
public data class PaneStrategy(
    public val counts: Map<Bp, Int> = DEFAULT_COUNTS,
    public val maxPanes: Int = 3,
    public val gutterDp: Int? = null,
)
```

Decides how many panes a width can carry. The public [counts] map is the whole contract, which makes the policy easy to test, document and override. Defaults reproduce the MULLION cabinet: | Bp | panes | | --- | --- | | Compact / Medium | 1 | | Expanded | 2 | | Large / XL | 3 | The count is clamped to `1..maxPanes` and to the number of slots supplied to [plan]. A custom layout can therefore cap panes at two without touching any screen code.

#### countFor

```kotlin
public fun countFor(bp: Bp): Int =
```

Number of panes allowed by the class, before slot clamping.

#### countForWidth

```kotlin
public fun countForWidth(widthDp: Int): Int = countFor(Bp.of(widthDp))
```

Number of panes allowed at [widthDp].

#### plan

```kotlin
public fun plan(bp: Bp, slots: List<PaneSlot>): PanePlan
```

Resolves the visible slots for [bp], preserving the caller's order. When fewer slots are supplied than the class allows, all supplied slots are shown. The first slot is always the "primary" pane.

#### planForWidth

```kotlin
public fun planForWidth(widthDp: Int, slots: List<PaneSlot>): PanePlan =
```

Resolves the visible slots for a raw window width.

#### DEFAULT_COUNTS

```kotlin
public val DEFAULT_COUNTS: Map<Bp, Int> = mapOf(
    Bp.Compact to 1,
    Bp.Medium to 1,
    Bp.Expanded to 2,
    Bp.Large to 3,
    Bp.XL to 3,
)
```

Cabinet defaults: 1 / 1 / 2 / 3 / 3.

#### TWO_PANE

```kotlin
public val TWO_PANE: PaneStrategy = PaneStrategy(maxPanes = 2)
```

A reading-oriented strategy that never exceeds two panes.

#### SINGLE_PANE

```kotlin
public val SINGLE_PANE: PaneStrategy = PaneStrategy(
    counts = Bp.entries.associateWith { 1 },
    maxPanes = 1,
)
```

A single-pane strategy useful for focus modes.
