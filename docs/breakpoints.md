# Breakpoints, panes and the shared ruler

> One table decides navigation, pane count, resource bucket and type step.

## The five classes

| Class | Lower bound | Design baseline | Navigation | Primary panes | Resource bucket |
| --- | --- | --- | --- | --- | --- |
| `Compact` | 0dp | 320dp | `BottomBar` | 1 | `values/` |
| `Medium` | 480dp | 480dp | `BottomBar` | 1 | `values/` |
| `Expanded` | 600dp | 600dp | `Rail` | 2 | `values-w600dp/` |
| `Large` | 840dp | 840dp | `Drawer` | 3 | `values-w840dp/` |
| `XL` | 1200dp | 1200dp | `Drawer` | 3 | `values-w840dp/` |

`Compact` starts at 0 so an unmeasured Compose surface still resolves to a legal class; the visual system is tuned from 320dp. `baselineWidthDp` is what the documentation rail displays.

```kotlin
Bp.of(0)      // Compact
Bp.of(479)    // Compact
Bp.of(480)    // Medium
Bp.of(600)    // Expanded
Bp.of(840)    // Large
Bp.of(1200)   // XL
Bp.Compact.baselineWidthDp // 320
```

## Why these numbers

- **480** is where one-handed phone layouts stop benefiting from wider text; Medium still uses one pane, but the bottom bar has more room.
- **600** is the smallest width where two panes can both stay above 280dp. Navigation becomes a rail.
- **840** is the width where a persistent drawer leaves enough content for a third surface.
- **1200** is where the content column should stop stretching; XL adds breathing room rather than more density.

The sample puts 375, 760 and 1180 in the trays because they are representative, not because the framework special-cases them. The same code handles 599, 601, 839 or 841.

## The single resolver

```kotlin
public enum class Bp(public val minWidthDp: Int, ...) {
    Compact(0, 320, "Compact"),
    Medium(480, 480, "Medium"),
    Expanded(600, 600, "Expanded"),
    Large(840, 840, "Large"),
    XL(1200, 1200, "XL");

    companion object {
        fun of(widthDp: Int): Bp = when {
            widthDp >= 1200 -> XL
            widthDp >= 840 -> Large
            widthDp >= 600 -> Expanded
            widthDp >= 480 -> Medium
            else -> Compact
        }
    }
}
```

There is no reflection and no map lookup. Thresholds are compile-time constants; R8 can inline the whole `when`. Resource buckets are resolved by the Android resource system at configuration time (`values-w600dp`, `values-w840dp`), which is also lookup-free from app code.

### Compose

```kotlin
val bp = currentBreakpoint()
val window = mullionWindow()
val nav = currentNavigationMode()
val panes = window.paneCount()
val metrics = mullionMetrics()          // 48/80/120dp
val type = mullionTypeScale()           // 15/17/19sp
```

`WindowSizeClass { bp -> ... }` is a zero-cost slot if you prefer to keep the read in one place.

### View

```kotlin
class MyView(context: Context) : FrameLayout(context) {
    private val layout = MullionBreakpointLayout(context)

    init {
        layout.addOnBreakpointChangedListener { bp ->
            // update labels or analytics
        }
    }
}
```

```kotlin
val widthDp = (width / resources.displayMetrics.density).toInt()
val metrics = ResourceScaler.metricsFor(widthDp)
val px = ResourceScaler.sectionSpacingPx(context, widthDp)
```

## PaneStrategy

Pane count is not hardcoded into layouts. It is a value object:

```kotlin
data class PaneStrategy(
    val counts: Map<Bp, Int> = DEFAULT_COUNTS, // 1,1,2,3,3
    val maxPanes: Int = 3,
    val gutterDp: Int = 24,
)
```

```kotlin
val strategy = PaneStrategy(maxPanes = 2)
val plan = strategy.plan(Bp.Large, listOf(PaneSlot.List, PaneSlot.Detail, PaneSlot.Preview))
// plan.count == 2
// plan.slots == [List, Detail]
```

`PaneStrategy.SINGLE_PANE` is useful for focus modes. `PanePlan` is immutable and carries the gutter, so a layout never invents its own spacing.

`MultiPane` and `SearchLayout` both consume the same strategy. That is why a search page and a notes page collapse at the same width.

## Navigation

```kotlin
NavigationMode.forBp(Bp.Compact)  // BottomBar
NavigationMode.forBp(Bp.Expanded) // Rail
NavigationMode.forBp(Bp.Large)    // Drawer
```

`AdaptiveScaffold` reads this once. `OrbitNav` renders the same `items` into the matching morphology. The selected destination is an index in the caller's data, not a mode-specific state, so crossing a threshold never loses the active tab.

## Metrics and resource buckets

| Token | Compact / Medium | Expanded | Large / XL |
| --- | --- | --- | --- |
| `spacing.section` | 48dp | 80dp | 120dp |
| `type.display` | 32sp | 44sp | 56sp |
| `pane.margin` | 16dp | 24dp | 32dp |
| `pane.gutter` | 12dp | 24dp | 24dp |

These are duplicated intentionally in two forms:

1. `mullion-view/src/main/res/values*/dimens.xml` for XML and `ResourceScaler.scaled(...)`.
2. `MullionMetrics` in `mullion-core` for Compose, previews, tests and simulated windows.

They must stay in sync; `ResourceScaler.metricsFor` is the single source for width-number-driven layouts, and the XML buckets are validated by resource tests/documentation.

## Type is a step, not a slope

`SizeClassText` uses `TypeScale`:

| Class | Font | Line height | Max measure |
| --- | --- | --- | --- |
| Compact / Medium | 15sp | 27sp | 62ch |
| Expanded | 17sp | 30.6sp | 68ch |
| Large / XL | 19sp | 34.2sp | 72ch |

The measure is measured from the actual typeface (`rememberTextMeasurer` measures `"0"`), not an average glyph guess. The text is capped at that width so line length never grows with the window.

## Testing the ruler

```kotlin
@Test fun `classifies every boundary exactly once`() {
    assertEquals(Bp.Compact, Bp.of(479))
    assertEquals(Bp.Medium, Bp.of(480))
    assertEquals(Bp.Expanded, Bp.of(600))
    assertEquals(Bp.Large, Bp.of(840))
    assertEquals(Bp.XL, Bp.of(1200))
}
```

`mullion-core` ships 33 unit tests covering boundaries, pane plans, fold geometry, buckets, insets and type steps. Run:

```bash
./gradlew :mullion-core:testDebugUnitTest
```

## Migration from width checks

Before:

```kotlin
if (LocalConfiguration.current.screenWidthDp >= 600) {
    Row { List(); Detail() }
} else {
    List()
}
```

After:

```kotlin
MultiPane(
    list = { List() },
    detail = { Detail() },
)
```

If a screen genuinely needs a different policy, pass a different `PaneStrategy`; do not add a second width table.

## Design notes

- The ruler is **width**, not device category. A folded phone, a tablet in split-screen and a desktop window all resolve by their current width.
- Height is available in `MullionWindowState.heightDp`, but is not part of the five-class ruler. Landscape phones keep their width class; layouts that need height should read it explicitly.
- `MullionWindowState` is immutable. Window changes produce a new state object; this makes previews and screenshot tests deterministic.
