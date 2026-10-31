# Architecture

> One ruler, one window state, two render tiers.

## Modules

```text
┌────────────────────────────────────────────────────────────┐
│ sample                                                     │
│ 26 live demos · simulated viewports · real AndroidView     │
└───────────────┬───────────────────────────┬────────────────┘
                │                           │
┌───────────────▼──────────────┐  ┌─────────▼─────────────────┐
│ mullion-compose              │  │ mullion-view              │
│ theme + window provider      │  │ BreakpointLayout          │
│ 14 adaptive + 12 base        │  │ PaneLayout / GlassLayout  │
│                              │  │ ButtonView / ResourceScaler│
└───────────────┬──────────────┘  └─────────┬─────────────────┘
                │                           │
                └────────────┬──────────────┘
                             ▼
                 ┌────────────────────────┐
                 │ mullion-core           │
                 │ Bp / PaneStrategy      │
                 │ HingeInfo / Foldables  │
                 │ Metrics / WindowState  │
                 └────────────────────────┘
```

`mullion-core` is an Android library with no UI dependencies. It contains only values and pure functions:

- `Bp` and the five thresholds.
- `NavigationMode` and `PaneStrategy` / `PanePlan`.
- `HingeInfo`, `HingePosture`, `DpRect`, `FoldSplit`, `Foldables`.
- `MullionMetrics`, `TypeScale`, `ResourceBucket`.
- `MullionInsets`, `MullionWindowState`.
- `Mullion` version metadata.

This module is the testable heart. It has 33 unit tests and no Compose/View imports, so it can be published and consumed by either tier (or by a Kotlin/JVM tool).

## One window state

```kotlin
MullionWindowState(
    widthDp = 840,
    heightDp = 1024,
    insets = MullionInsets(leftDp = 0, topDp = 24, ...),
    hinge = HingeInfo.vertical(separatorWidthDp = 20f),
)
```

The state is immutable and derived from:

- Compose’s `LocalWindowInfo.containerSize` (px → dp).
- `WindowInsets.systemBars` and `WindowInsets.ime` (compat on API 21).
- `WindowInfoTracker` / `FoldingFeature` (Window 1.4).
- The user’s font scale.

A resize or fold change produces one new object, so components recompose once with a consistent width/inset/hinge triple. There is no global singleton and no static window cache.

### Compose provider

```kotlin
MullionWindowProvider { App() }
```

- When no state is supplied, measures the real window.
- When a state is supplied, it becomes the simulated window for the subtree.
- Nested providers preserve the nearest state.

`LocalMullionWindow` is a `staticCompositionLocalOf<MullionWindowState?>`. A `null` means “measure the real window”; this is what lets the sample simulate 375/760/1180 without rebuilding the tree.

### View provider

`MullionBreakpointLayout` measures its own width in `onMeasure`, publishes the class, and gates children by their `mullionMinWidthDp` / `mullionMaxWidthDp` attributes. `MullionPaneLayout` uses `PaneStrategy` directly and drops a pane before it becomes narrower than `minPaneWidth`.

The View tier does not depend on the Compose provider; it resolves the same `mullion-core` values from its own measured width. This is deliberate: existing XML screens should not need a Compose runtime.

## Why no `if (width)` in screens

The framework exposes **structure**, not just values:

- `AdaptiveScaffold` owns nav morphology.
- `MultiPane` / `SearchLayout` own pane count.
- `DockedSheet` / `FoldablePane` own seam geometry.
- `EdgeToEdgeBox` owns inset math.

A screen describes *what* it has (list, detail, preview, nav items) and the framework decides *how many/how* are visible. If a product needs a different policy, it passes a different `PaneStrategy`, not a second threshold table.

## Compose rendering

### Theme

`MullionTheme` installs:

- A dark Material 3 color scheme with volt/signal/mint/amber semantics.
- A type scale with monospace labels and a humanist sans body.
- Shapes (6/12/18/22/28dp).
- `MullionDimens` via `LocalMullionDimens`.

### Glass

`Modifier.glass(shape, level)`:

1. Clips to the shape.
2. Fills with a three-stop white gradient.
3. Draws a 1dp edge.
4. Draws a 1dp top hairline.

`GlassLevel` (Chip/Panel/Floating/Window) controls radii, alpha and elevation. This is a deterministic, API-21-safe recipe. It is not a live backdrop blur; Android’s `RenderEffect` is API 31+ and would make API 21–30 screenshots diverge, which is exactly the adaptation problem this library is trying to avoid.

### Motion

- 120ms press scale.
- 180ms state colors.
- 240ms pane shared-axis.
- 340–380ms sheets/dialogs.
- 3200ms snackbar.

Motion is implemented with Compose animation APIs (`AnimatedContent`, `AnimatedVisibility`, `animate*AsState`), not frame callbacks.

## View rendering

`MullionGlassLayout` draws the same gradient/edge recipe in `draw()` before children; `MullionButtonView` draws its pill, border and press wash on a `TextView` canvas. Both are reflection-free and work on API 21.

`ResourceScaler` has two layers:

1. `scaled(context, resId)` reads a platform-selected qualifier bucket (`values-w600dp`, `values-w840dp`).
2. `metricsFor(widthDp)` resolves the same numbers from a width number for previews/tests/simulated screens.

This mirrors the design requirement “编译期解析 · 运行时零反射”: qualifiers are resolved by the platform at configuration time, and the width→value mapping is a pure Kotlin function.

## Data flow for a resize

```text
Window resize / fold change
        │
        ▼
MullionWindowState (new immutable value)
        │
        ├─▶ AdaptiveScaffold.navigationMode
        ├─▶ MultiPane / SearchLayout pane plan
        ├─▶ DockedSheet / FoldablePane geometry
        ├─▶ EdgeToEdgeBox insets
        └─▶ SizeClassText / metrics / type scale
```

No component writes back into the window state. State changes are driven by the platform provider or by a simulated provider in tests.

## Testing strategy

- **Core**: pure unit tests for every threshold, pane plan, fold split, bucket and type step.
- **Compose**: the sample is the live integration surface; every tray can simulate 375/760/1180 and toggle hinge/IME/dialog states.
- **View**: resource buckets and layout attributes are compile-time checked; the sample embeds a real `AndroidView` with `MullionCardView` / `MullionButtonView`.

The sample was built and installed on a Pixel 7 API 36 emulator at 411dp, 800dp and 1280dp. The screenshots in `docs/images` come from that run.

## Boundaries and non-goals

- MULLION does not replace Material 3; it supplies adaptive structure and a glass skin.
- It does not ship an icon set. Icons are slots; the demo draws its own minimal glyphs.
- It does not request permissions; `PermissionSheet` is a host-driven UI.
- It does not claim live backdrop blur on API 21.
- It models one hinge in 1.0; multi-fold is a future extension.

## Adding a new adaptive component

1. Put the pure decision in `mullion-core` if it is reusable (width → count, geometry, token).
2. Add a composable in `mullion-compose/adaptive` that reads `mullionWindow()` and exposes an override parameter for previews.
3. Add a View equivalent only if it is structural (layout/insets); keep it in `mullion-view`.
4. Add a sample card and, if pure, a core unit test.
5. Add it to `docs/components.md` and the static index.

The bar for adding a component is not “we can draw it”; it is “it removes a width/inset/hinge decision from application code.”
