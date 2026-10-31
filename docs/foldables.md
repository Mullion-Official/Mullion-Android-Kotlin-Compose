# Foldables and hinges

> A hinge is geometry. Content must not be asked to pretend it is not there.

## The model

```kotlin
enum class HingeOrientation { Vertical, Horizontal }
enum class HingePosture { Flat, HalfOpened, Tent }

data class HingeInfo(
    val posture: HingePosture,
    val orientation: HingeOrientation?,
    val separatorWidthDp: Float,
    val separatorHeightDp: Float,
    val bounds: DpRect?,
)
```

- `HingeInfo.None` — no hinge, or a flat foldable.
- `HingeInfo.vertical(separatorWidthDp = 20f)` — book-style fold.
- `HingeInfo.horizontal(separatorHeightDp = 40f)` — clamshell fold.
- `posture` is semantic; `orientation` answers "is there a seam to avoid?".
- `isSeparating` is true only when orientation is non-null and posture is not `Flat`.

`HingePosture.Tent` is a first-class value because layout may want to reserve space even when the OS only reports `HALF_OPENED`.

## Detecting a real hinge

```kotlin
MullionWindowProvider {
    // reads WindowInfoTracker.windowLayoutInfo(activity)
    // converts FoldingFeature -> HingeInfo in dp
    App()
}
```

`currentHinge()` returns the last detected value. The provider also supplies the window width, height and insets; a fold change and a resize arrive as one immutable `MullionWindowState`.

The Flow is collected while the composition is alive. There is no static global state, so two activities cannot leak each other's window.

### Window 1.4 caveat

AndroidX Window 1.4 exposes `FLAT` and `HALF_OPENED`, not a reliable `TENT` flag. MULLION maps `HALF_OPENED` to `HingePosture.HalfOpened`. If a device integration knows tent mode (sensor, OEM SDK, a settings toggle), inject it:

```kotlin
MullionWindowProvider(
    window = MullionWindowState(
        widthDp = 760,
        heightDp = 900,
        hinge = HingeInfo.vertical(20f)
            .withPosture(HingePosture.Tent),
    ),
) {
    App()
}
```

The sample does exactly this for its FLAT / HALF / TENT toggle; the geometry is real, only the posture classification is supplied by the host.

## Geometry

`Foldables.splitVertically` reserves the seam and a safe gap on both sides:

```kotlin
val split = Foldables.splitVertically(
    availableWidthDp = 800f,
    hinge = HingeInfo.vertical(separatorWidthDp = 20f),
    safeGapDp = 24f,
)

split.paneWidthDp     // (800 - 20 - 48) / 2 = 366dp
split.hingeStartDp    // 390dp
split.hingeEndDp      // 410dp
split.endPaneStartDp  // 434dp
```

The left leaf occupies `0..366`, the right leaf starts at `434`. Content never paints on `366..434`.

```kotlin
Foldables.canSplit(700f, HingeInfo.vertical(400f), minPaneDp = 280f) // false
```

A flat foldable is continuous:

```kotlin
val flat = Foldables.splitVertically(800f, HingeInfo.None)
// paneWidthDp == 400f, hingeWidthDp == 0f
```

A horizontal seam does not split columns; the caller should switch to a stacked composition.

## FoldablePane

```kotlin
FoldablePane(
    hinge = currentHinge(),
    start = { LeafA() },
    end = { LeafB() },
    safeGap = 24.dp,
    minPaneWidth = 240.dp,
    singlePane = { CombinedPane() },
    onHingeChanged = { hinge -> analytics.track(hinge.posture) },
)
```

Behavior:

| Posture / geometry | Layout |
| --- | --- |
| flat / no hinge | two equal leaves, no seam gap |
| vertical separating | two leaves with seam + 2 × safeGap; dashed signal strip in the gap |
| horizontal separating | two stacked leaves with seam + 2 × safeGap |
| not enough width | `singlePane` (or `start` if omitted) |

The dashed strip is diagnostic, not required for correctness: the widths already exclude the seam.

The sample shows the important failure mode at 375dp: the two 375 leaves would be ~153dp each, below the 240dp minimum, so it falls back to a single pane instead of squeezing text.

## DockedSheet

```kotlin
DockedSheet(
    open = open,
    onDismissRequest = { open = false },
    hinge = currentHinge(),
    dockSide = DockSide.Auto,
    minSheetWidth = 140.dp,
    maxSheetWidth = 420.dp,
    sheetContent = { FilterBody() },
) {
    ScreenContent()
}
```

- `Auto` + vertical separating hinge + window ≥ Expanded → dock to the end edge.
- The sheet width is `min(window × 0.44, maxSheetWidth, leafWidth)`, so it stays inside one leaf.
- Horizontal hinge / no hinge / too narrow → bottom sheet.
- A horizontal seam caps the bottom sheet height using the hinge bounds when available.

At 760dp with a 20dp seam, the window is 736dp (after 12dp demo padding), each leaf is 334dp, and the sheet uses `min(323, 420, 334) = 323dp`. At 375dp it falls back to the bottom because the window is compact, even though a narrow side leaf would technically fit.

`DockSide.Start` / `DockSide.End` force a side if you want one on a non-foldable tablet; explicit overrides are allowed but still clamp to the leaf.

## Follow the seam, not the device

Do not branch on `isFoldable`. Branch on:

```kotlin
hinge.isVerticalSeparator
hinge.isHorizontalSeparator
Foldables.canSplit(widthDp, hinge, minPaneDp)
```

A tablet in split-screen can be narrower than a folded phone; width and geometry answer the real question.

## Testing

`mullion-core` tests the pure geometry:

```bash
./gradlew :mullion-core:testDebugUnitTest
```

Covered cases include: no hinge, vertical seam, flat foldable with a stale hinge, horizontal seam, side-sheet minimums, `canSplit` rejection and posture preservation.

For visual verification, open the sample's **DockedSheet** and **FoldablePane** cards and switch the simulated width to 375 / 760 / 1180. The FoldablePane tray also toggles FLAT / HALF / TENT.

## Limitations

- Window 1.4 does not expose a reliable `TENT` state; inject it if your device integration knows better.
- The framework models one hinge. Multi-fold devices need a future `HingeSet`; the geometry helpers are small enough to extend without changing component APIs.
- The sample simulates hinge state with values; a real device path is the `WindowInfoTracker` provider in `mullion-compose`.
