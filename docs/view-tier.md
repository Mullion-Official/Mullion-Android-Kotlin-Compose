# View tier

> Keep the View screens you already have. Give them the same ruler.

The View tier is intentionally smaller than the Compose tier. It provides **structure** (breakpoints, panes, insets) and **the glass skin** (card/button), not a second copy of all 26 widgets.

## Module

```kotlin
implementation("dev.mullion:view:1.0.0")
// or project(":mullion-view")
```

Public API:

| Class / object | Purpose |
| --- | --- |
| `MullionBreakpointLayout` | FrameLayout with width-class callback and per-child width gates |
| `MullionPaneLayout` | 1/2/3 pane ViewGroup, min-pane aware |
| `MullionGlassLayout` | FrameLayout with the glass gradient, edge and top hairline |
| `MullionCardView` | `MullionGlassLayout` with 12dp radius and 18dp padding |
| `MullionButtonView` | TextView-drawn volt pill: Filled/Tonal/Outlined/Text |
| `ResourceScaler` | resource bucket + width-number metrics |
| `MullionEdgeToEdge` | edge-to-edge + insets listener |
| `MullionPalette` | ARGB palette for custom drawing |

## Breakpoint layout

```xml
<dev.mullion.view.MullionBreakpointLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:mullion="http://schemas.android.com/apk/res-auto"
    android:id="@+id/bp_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Phones -->
    <include
        layout="@layout/phone_content"
        mullion:mullionMaxWidthDp="599" />

    <!-- Tablets, unfolded phones -->
    <include
        layout="@layout/tablet_content"
        mullion:mullionMinWidthDp="600" />
</dev.mullion.view.MullionBreakpointLayout>
```

A child is visible when `widthDp >= minWidthDp && widthDp <= maxWidthDp`; the defaults are `0..Int.MAX_VALUE`. The original declared visibility is restored when the child is in range.

Listeners:

```kotlin
val root = findViewById<MullionBreakpointLayout>(R.id.bp_root)
root.addOnBreakpointChangedListener { bp ->
    Log.d("Mullion", "bp=${bp.label}, width=${root.widthDp}dp")
}
```

The callback fires only when the class changes, not on every pixel.

## Pane layout

```xml
<dev.mullion.view.MullionPaneLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    mullion:mullionMinPaneWidth="280dp"
    mullion:mullionPaneGutter="24dp"
    mullion:mullionMaxPanes="3">

    <include layout="@layout/pane_list" />
    <include layout="@layout/pane_detail" />
    <include layout="@layout/pane_preview" />

</dev.mullion.view.MullionPaneLayout>
```

- Width class → 1 / 2 / 3 panes via the shared `PaneStrategy`.
- Before a pane becomes narrower than `mullionMinPaneWidth`, the layout drops to the previous count.
- Extra children are measured to 0 rather than removed; their view state survives.
- `paneCount` and `breakpoint` are public; `onPaneCountChanged` fires when the count changes.

Programmatic:

```kotlin
paneLayout.setMinPaneWidthDp(300)
paneLayout.setPaneGutterDp(24)
paneLayout.setMaxPanes(2)
```

The View pane layout is horizontal (list/detail/preview). For hinge-aware two-leaf geometry, use Compose `FoldablePane` or compute `Foldables.splitVertically` and set child widths yourself.

## Glass card and button

```xml
<dev.mullion.view.MullionCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="18dp"
    mullion:mullionCornerRadius="12dp"
    mullion:mullionGlassAlpha="0.07"
    mullion:mullionSelected="false">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="MULLION / VIEW"
        android:textColor="#EDF4FA"
        android:typeface="monospace" />

    <dev.mullion.view.MullionButtonView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Confirm"
        mullion:mullionButtonVariant="filled" />
</dev.mullion.view.MullionCardView>
```

- `MullionGlassLayout` accepts `mullionCornerRadius`, `mullionGlassAlpha`, `mullionBorderColor`, `mullionSelected`.
- `MullionButtonView` accepts `mullionButtonVariant` (`filled`, `tonal`, `outlined`, `text`).
- Button text stays a normal `TextView`; `setOnClickListener` and accessibility work as usual.
- `MullionCardView` defaults to 12dp radius, 18dp padding, 120dp minimum height, 10dp elevation.

Java callers can use the getters/setters:

```java
MullionGlassLayout glass = new MullionGlassLayout(context);
glass.setCornerRadiusDp(18f);
glass.setGlassAlpha(0.08f);
glass.setMullionSelected(true);
```

## ResourceScaler

The module ships the same buckets as Compose:

```text
values/dimens.xml         48dp / 32sp / 16dp / 12dp
values-w600dp/dimens.xml  80dp / 44sp / 24dp / 24dp
values-w840dp/dimens.xml 120dp / 56sp / 32dp / 24dp
```

Names: `mullion_spacing_section`, `mullion_type_display`, `mullion_pane_margin`, `mullion_pane_gutter`, `mullion_pane_min_width`, `mullion_radius_panel`.

```kotlin
val sp = ResourceScaler.scaled(context, R.dimen.mullion_spacing_section) // px
val metrics = ResourceScaler.metricsFor(760) // no Context required
val gapPx = ResourceScaler.paneGutterPx(context, 760)
val cols = ResourceScaler.chooseDp(1180, compact = 12, expanded = 24, large = 32)
```

`scaled` uses the platform qualifier bucket. `metricsFor` uses the pure width resolver. Both produce the same numbers; the second is what previews and simulated screens use.

## Edge-to-edge

Call before `setContentView`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    MullionEdgeToEdge.enable(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    MullionEdgeToEdge.install(
        root = findViewById(R.id.root),
        includeIme = true,
        applyPadding = true,
    )
}
```

`enable` sets decor-fits-system-windows to false, makes bars transparent and selects light/dark icon appearance. `install` preserves the root’s original padding and adds system bars + IME. It uses `WindowInsetsCompat`, so API 21–36 share one path.

Use `onInsets` to draw a status/IME band yourself:

```kotlin
MullionEdgeToEdge.install(root, applyPadding = false) { insets ->
    val ime = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
    band.layout(0, root.height - ime, root.width, root.height)
}
```

## Mixing with Compose

`AndroidView` and `ComposeView` both work. The sample embeds a real `MullionCardView` + `MullionButtonView` inside a Compose page through `AndroidView`. The View tier does not need Compose to resolve breakpoints; the Compose tier does not need the View tier.

## Attribute reference

| Attribute | Type | Default | Used by |
| --- | --- | --- | --- |
| `mullionMinWidthDp` | integer | 0 | child of `MullionBreakpointLayout` |
| `mullionMaxWidthDp` | integer | max | child of `MullionBreakpointLayout` |
| `mullionMinPaneWidth` | dimension | 280dp | `MullionPaneLayout` |
| `mullionPaneGutter` | dimension | 24dp | `MullionPaneLayout` |
| `mullionMaxPanes` | integer | 3 | `MullionPaneLayout` |
| `mullionCornerRadius` | dimension | 18dp | `MullionGlassLayout` |
| `mullionGlassAlpha` | float | 0.07 | `MullionGlassLayout` |
| `mullionBorderColor` | color | white 16% | `MullionGlassLayout` |
| `mullionSelected` | boolean | false | `MullionGlassLayout` |
| `mullionButtonVariant` | enum | filled | `MullionButtonView` |

## Limitations

- The View tier does not include the 12 base widgets; Material widgets can be themed alongside it.
- `MullionPaneLayout` is not hinge-aware; use Compose `FoldablePane` for seam geometry.
- The glass recipe is gradient-based (consistent API 21+), not a live backdrop blur.
