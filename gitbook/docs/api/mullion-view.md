# MULLION VIEW API

> 本页由 `gitbook/scripts/gen_api_docs.py` 从 `mullion-view` 源码自动生成：
> 每个公开符号给出 Kotlin 签名与释义（释义取自源码 KDoc，含 `@param` / `@return`）。

共 **46** 个公开符号，按包与源文件分组。

## 包 `dev.mullion.view`

### `MullionBreakpointLayout.kt`

#### MullionBreakpointLayout

```kotlin
public open class MullionBreakpointLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr)
```

A FrameLayout that publishes its width class and can gate children by width. Children declare `mullionMinWidthDp` and/or `mullionMaxWidthDp`; outside that range they are `GONE`, inside it they return to their declared visibility. This replaces a pile of `values-wXXXdp` layout duplicates with one XML tree. Listeners fire only when the class changes, not on every pixel of a resize.

#### breakpoint

```kotlin
public var breakpoint: Bp = Bp.Compact
```

The last measured width class.

#### widthDp

```kotlin
public var widthDp: Int = 0
```

Width in dp from the last measure pass.

#### addOnBreakpointChangedListener

```kotlin
public fun addOnBreakpointChangedListener(listener: (Bp) -> Unit)
```

*(源码未提供 KDoc；以签名为准。)*

#### removeOnBreakpointChangedListener

```kotlin
public fun removeOnBreakpointChangedListener(listener: (Bp) -> Unit)
```

*(源码未提供 KDoc；以签名为准。)*

#### LayoutParams

```kotlin
public class LayoutParams : FrameLayout.LayoutParams
```

Per-child width gates. Defaults keep every child visible at every width.

#### minWidthDp

```kotlin
public var minWidthDp: Int = 0
```

*(源码未提供 KDoc；以签名为准。)*

#### maxWidthDp

```kotlin
public var maxWidthDp: Int = Int.MAX_VALUE
```

*(源码未提供 KDoc；以签名为准。)*

### `MullionButtonView.kt`

#### MullionButtonVariant

```kotlin
public enum class MullionButtonVariant
```

Button variants for the View tier.

#### MullionButtonView

```kotlin
public open class MullionButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : TextView(context, attrs, defStyleAttr)
```

The View-compatible mullion button. It is a single TextView with no background drawable, which means it can be dropped into an existing XML screen without a theme migration. The pill, border and press wash are drawn on the canvas; text remains a normal TextView and keeps all accessibility behavior.

#### variant

```kotlin
public var variant: MullionButtonVariant = MullionButtonVariant.Filled
```

Current variant.

### `MullionEdgeToEdge.kt`

#### MullionEdgeToEdge

```kotlin
public object MullionEdgeToEdge
```

Edge-to-edge for View screens, with one insets calculation for status bars, gesture bars and the IME. On API 21 the compat layer reports the IME as zero until the keyboard is attached; once it is, content lifts by exactly the IME height. The listener preserves the view's original padding and adds the insets, so it can be installed on any existing root.

#### enable

```kotlin
@JvmStatic
public fun enable(activity: Activity)
```

Draws behind the system bars. Call before `setContentView`.

#### install

```kotlin
@JvmStatic
@JvmOverloads
public fun install(
    root: View,
    includeIme: Boolean = true,
    applyPadding: Boolean = true,
    consume: Boolean = false,
    onInsets: ((WindowInsetsCompat) -> Unit)? = null,
)
```

Installs an insets listener that pads the root by system bars and (when [includeIme]) the keyboard.

| 参数 | 说明 |
| --- | --- |
| `consume` | when `true`, descendants will not receive the same insets. |
| `onInsets` | optional observer for drawing inset bands. |

#### clear

```kotlin
@JvmStatic
public fun clear(root: View)
```

Removes the listener. Padding is left as-is.

### `MullionGlassLayout.kt`

#### MullionGlassLayout

```kotlin
public open class MullionGlassLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr)
```

A ViewGroup that paints the MULLION glass recipe. Gradient fill, 1dp edge, top hairline and an optional selected volt border. Children are clipped to the rounded outline on API 21+. No bitmap capture is used, so glass stays crisp through rotation and density changes.

#### setCornerRadiusDp

```kotlin
public fun setCornerRadiusDp(dp: Float)
```

Sets the corner radius in dp.

#### setGlassAlpha

```kotlin
public fun setGlassAlpha(alpha: Float)
```

Sets the glass fill intensity, roughly `0.02..0.16`.

#### setMullionBorderColor

```kotlin
public fun setMullionBorderColor(color: Int)
```

Overrides the 1dp border color.

#### setMullionSelected

```kotlin
public fun setMullionSelected(isSelected: Boolean)
```

Draws a volt border for selected cards.

#### getCornerRadiusDp

```kotlin
public fun getCornerRadiusDp(): Float = cornerRadiusPx / resources.displayMetrics.density
```

Current corner radius in dp.

#### MullionCardView

```kotlin
public open class MullionCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MullionGlassLayout(context, attrs, defStyleAttr)
```

A glass card: 12dp radius, 18dp content padding and a 120dp minimum height.

### `MullionPalette.kt`

#### MullionViewColors

```kotlin
public data class MullionViewColors(
    @ColorInt public val background: Int,
    @ColorInt public val backgroundLift: Int,
    @ColorInt public val backgroundDeep: Int,
    @ColorInt public val volt: Int,
    @ColorInt public val voltHigh: Int,
    @ColorInt public val signal: Int,
    @ColorInt public val mint: Int,
    @ColorInt public val amber: Int,
    @ColorInt public val ink: Int,
    @ColorInt public val muted: Int,
    @ColorInt public val faint: Int,
    @ColorInt public val onVolt: Int,
    @ColorInt public val edge: Int,
    @ColorInt public val edgeHigh: Int,
    @ColorInt public val tonal: Int,
    @ColorInt public val glassTint: Int,
    public val glassScale: Float,
)
```

Resolved View colors for the current configuration.

#### MullionPalette

```kotlin
public object MullionPalette
```

View-tier palette. Compose reads `MullionColors`; XML/View screens resolve the same semantic tokens from `values` / `values-night` through [of]. The constants remain as the legacy dark fallback for existing callers that read them directly. New code should use [of] and react to `onConfigurationChanged`.

#### of

```kotlin
@JvmStatic
public fun of(context: Context): MullionViewColors
```

Resolves the light or night palette for this context.

### `MullionPaneLayout.kt`

#### MullionPaneLayout

```kotlin
public open class MullionPaneLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr)
```

A one / two / three pane ViewGroup driven by the shared width policy. Children are placed left to right with a gutter; the number of panes comes from [PaneStrategy]. Extra children are measured to zero rather than removed, so their state survives a resize. Useful for porting an existing View screen to adaptive layouts without rewriting it in Compose.

#### paneCount

```kotlin
public var paneCount: Int = 1
```

Number of panes laid out in the last measure.

#### breakpoint

```kotlin
public var breakpoint: Bp = Bp.Compact
```

The width class used by the last measure.

#### onPaneCountChanged

```kotlin
public var onPaneCountChanged: ((Int) -> Unit)? = null
```

Called when the pane count changes.

#### setMinPaneWidthDp

```kotlin
public fun setMinPaneWidthDp(dp: Int)
```

Sets the minimum width a pane may have before the layout drops a pane.

#### setPaneGutterDp

```kotlin
public fun setPaneGutterDp(dp: Int)
```

Sets the gutter between panes.

#### setMaxPanes

```kotlin
public fun setMaxPanes(panes: Int)
```

Caps the number of panes regardless of width class.

### `MullionView.kt`

#### MullionView

```kotlin
public object MullionView
```

View entry point retained for binary-safe builds during early bootstrap.

### `ResourceScaler.kt`

#### ResourceScaler

```kotlin
public object ResourceScaler
```

Resource resolution that does not need a new `values-wXXXdp` directory per design decision. There are two layers: 1. [scaled] reads a dimension from the `values/`, `values-w600dp/` or `values-w840dp/` bucket. The platform picks the bucket at configuration time; there is no reflection. 2. [metricsFor] resolves the same values from a *width number*. This is what previews, simulated viewports and tests use, and it produces identical numbers to the bucket files. Existing projects can migrate one dimension at a time: `values-w820dp/dimens.xml` → [metricsFor] or the MULLION bucket resources.

#### bucketFor

```kotlin
public fun bucketFor(widthDp: Int): ResourceBucket = ResourceBucket.forWidth(widthDp)
```

The bucket a width belongs to.

#### qualifierFor

```kotlin
public fun qualifierFor(widthDp: Int): String = bucketFor(widthDp).qualifier
```

The `values-…` qualifier for a width, useful in diagnostics.

#### metricsFor

```kotlin
public fun metricsFor(widthDp: Int): MullionMetrics = MullionMetrics.forWidth(widthDp)
```

The full ruler for a width, without a Context.

#### typeScaleFor

```kotlin
public fun typeScaleFor(widthDp: Int): TypeScale = TypeScale.forWidth(widthDp)
```

The type step for a width, without a Context.

#### scaled

```kotlin
public fun scaled(context: Context, @DimenRes resId: Int): Float =
```

Reads a dimension that already has a `values-w600dp` / `values-w840dp` variant, in pixels.

#### sectionSpacingPx

```kotlin
public fun sectionSpacingPx(context: Context, widthDp: Int): Float =
```

Section spacing in pixels for a simulated width.

#### displayTypePx

```kotlin
public fun displayTypePx(context: Context, widthDp: Int): Float =
```

Display type size in pixels for a simulated width.

#### paneGutterPx

```kotlin
public fun paneGutterPx(context: Context, widthDp: Int): Float =
```

Pane gutter in pixels for a simulated width.

#### paneMarginPx

```kotlin
public fun paneMarginPx(context: Context, widthDp: Int): Float =
```

Pane margin in pixels for a simulated width.

#### dpToPx

```kotlin
public fun dpToPx(context: Context, dp: Int): Float =
```

Converts dp to pixels.

#### spToPx

```kotlin
public fun spToPx(context: Context, sp: Float): Float =
```

Converts sp to pixels, honoring the user's font scale.

#### chooseDp

```kotlin
public fun chooseDp(
    widthDp: Int,
    compact: Int,
    expanded: Int = compact,
    large: Int = expanded,
): Int = when (bucketFor(widthDp))
```

Picks a dimension value by width class without a resource lookup.
