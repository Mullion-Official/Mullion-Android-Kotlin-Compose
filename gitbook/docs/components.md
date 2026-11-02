# The 26 modules

All components live in `dev.mullion.compose` (Compose) or `dev.mullion.view` (View). The sample in this repository renders every one of them live.

- [Adaptive Kit 01–14](#adaptive-kit-0114)
- [Base Kit 15–26](#base-kit-1526)
- [Design tokens](#design-tokens)

---

## Adaptive Kit 01–14

### 01 · AdaptiveScaffold

`dev.mullion.compose.adaptive.AdaptiveScaffold`

```kotlin
AdaptiveScaffold(
    topBar = { AppBar() },
    bottomBar = { OrbitNav(items, selected, onSelect) },
    rail = { OrbitNav(items, selected, onSelect) },
    drawer = { OrbitNav(items, selected, onSelect) },
    drawerOpen = true,
    mode = null, // override for previews
) { padding ->
    Content(padding)
}
```

- Compact/Medium: top bar + content + bottom bar.
- Expanded: rail + top bar + content.
- Large/XL: drawer (or rail when `drawerOpen = false`) + top bar + content.
- `content` is one call site; its state survives nav morphs.
- `mode` exists for previews and tests; production code should omit it.

### 02 · DockedSheet

```kotlin
DockedSheet(
    open = open,
    onDismissRequest = { open = false },
    hinge = currentHinge(),
    dockSide = DockSide.Auto, // Start / End / Bottom
    minSheetWidth = 140.dp,
    maxSheetWidth = 420.dp,
    title = { Text("Filters") },
    sheetContent = { FilterBody() },
) {
    ScreenContent()
}
```

Side sheet fits inside one fold leaf. Horizontal/no hinge falls back to bottom. Never paints over the seam. Scrim click and back dismiss.

### 03 · OrbitNav

```kotlin
OrbitNav(
    items = listOf(OrbitNavItem("Workbench"), OrbitNavItem("Devices", badge = "3")),
    selectedIndex = selected,
    onSelect = { selected = it },
    header = { BrandHeader() },
    icon = { index, active -> MyIcon(index, active) },
)
```

One data source, three morphologies. Icons are optional; without them the component draws an orbit mark from the label. Selection is an index, so mode changes keep the active destination.

### 04 · SizeClassText

```kotlin
SizeClassText(
    text = paragraph,
    maxMeasureCh = null,
    style = null,
    color = Color.Unspecified,
)
```

Font and measure step with the width class (15/17/19sp; 62/68/72ch). Measure is measured from the real typeface, then the Text is capped with `widthIn(max = ...)`.

### 05 · SearchLayout / MullionSearchBar

```kotlin
SearchLayout(
    query = query,
    onQueryChange = { query = it },
    results = { ResultList() },
    preview = { ResultPreview() },
    filters = { FilterRail() },
    resultCount = results.size,
    placeholder = "Search devices…",
)
```

One/two/three panes driven by `PaneStrategy`. At 375 only results; at 600+ results + preview; at 840+ results + preview + filters. Query state belongs to the caller.

`MullionSearchBar` is reusable standalone; it has a focus ring, clear affordance and IME search action.

### 06 · MultiPane

```kotlin
MultiPane(
    list = { NoteList() },
    detail = { NoteDetail() },
    preview = { NotePreview() },
    showDetail = detailOpen,
    onBack = { detailOpen = false },
    strategy = PaneStrategy(),
)
```

- One pane: `showDetail` switches list ↔ detail with a shared-axis X transition; system back calls `onBack`.
- Two panes: list width 34% (280–380dp), detail fills the rest.
- Three panes: preview docks at 26% (240–320dp).
- Gutter comes from `PanePlan`.

### 07 · AdaptiveDialog

```kotlin
Box(Modifier.fillMaxSize()) {
    Screen()
    AdaptiveDialog(
        visible = open,
        onDismissRequest = { open = false },
        kind = DialogKind.Auto, // Centered / SidePanel
    ) {
        DialogBody()
    }
}
```

Inline overlay, not a platform `Dialog`. This keeps the dialog inside a simulated viewport or a fold leaf. At Large+ it becomes a side panel with a 36%-width cap (320–480dp). Back and scrim behavior are shared.

### 08 · ResourceScaler

Compose helpers:

```kotlin
val metrics = mullionMetrics()       // MullionMetrics.forBp
val gap = scaledDp(compact = 12.dp, expanded = 24.dp, large = 32.dp)
val type = mullionTypeScale()
```

View helpers:

```kotlin
ResourceScaler.scaled(context, R.dimen.mullion_spacing_section)
ResourceScaler.metricsFor(widthDp)
ResourceScaler.sectionSpacingPx(context, widthDp)
```

Ships `values/`, `values-w600dp/`, `values-w840dp/` buckets for `spacing_section`, `type_display`, `pane_margin`, `pane_gutter`, `pane_min_width`. `ResourceScaler` is the migration path away from hand-written `values-w820dp`.

### 09 · EdgeToEdgeBox

```kotlin
EdgeToEdgeBox(
    includeIme = true,
    applyPadding = true,
    simulatedInsets = null,
) { padding ->
    Column(Modifier.padding(padding)) { Screen() }
}
```

Computes status + nav + IME once. `applyPadding = false` lets you draw chrome behind the bands while positioning children with the supplied `PaddingValues`. `simulatedInsets` powers the sample's keyboard toggle.

View counterpart: `MullionEdgeToEdge.enable(activity)` + `install(root)`.

### 10 · DraggableDivider

```kotlin
DraggableDivider(
    state = rememberDraggableSplitState(0.42f),
    minPaneWidth = 120.dp,
    first = { PaneA() },
    second = { PaneB() },
    onSplitChange = { left, right -> readout = "$left / $right" },
)
```

11dp pointer target, 5dp painted rail. Clamped before layout. The state is `rememberSaveable`. Accessibility exposes "move left" / "move right" custom actions. The split fraction never escapes `0.02..0.98`, and the component further clamps by minimum pane width.

### 11 · WindowSizeClass

The core is `dev.mullion.core.Bp`; Compose exposes:

```kotlin
val bp = currentBreakpoint()
WindowSizeClass { widthClass -> }
val plan = PaneStrategy().plan(bp, slots)
```

The sample's **Breakpoint lab** drags a simulated width and shows class, navigation, pane count, bucket and type step all changing from the same resolver.

### 12 · FoldablePane

```kotlin
FoldablePane(
    hinge = currentHinge(),
    start = { LeafA() },
    end = { LeafB() },
    safeGap = 24.dp,
    minPaneWidth = 240.dp,
    singlePane = { Combined() },
    onHingeChanged = { hinge -> log(hinge.posture) },
)
```

Geometry is described in [foldables.md](foldables.md). Fallback is a first-class state, not a squeeze.

### 13 · SharedAxisTransition

```kotlin
SharedAxisTransition(
    targetState = pane,
    axis = SharedAxis.X, // Y / Z
    durationMillis = 240,
) { current ->
    Pane(current)
}
```

Old pane fades and exits along the axis; new pane enters from the opposite side. 28dp travel, `cubic-bezier(.4,0,.2,1)`, enter/exit run together. Z uses scale. Container size change does not clip the transition.

### 14 · PermissionSheet

```kotlin
PermissionSheet(
    visible = visible,
    permissions = listOf(
        MullionPermission("Camera", "CAMERA", rationale = "Scan device QR", required = true),
        MullionPermission("Location", "ACCESS_FINE_LOCATION"),
    ),
    onDismissRequest = { visible = false },
    onGrantAll = { launchRequest() },
    onDeny = { visible = false },
    anchor = PermissionAnchor.Auto, // Bottom / Trigger
    anchorOffset = DpOffset(24.dp, 72.dp),
)
```

Compact: bottom sheet. Expanded+: anchored panel at `anchorOffset`. The component is platform-permission agnostic; the host owns the actual request. Focus moves into the sheet; back dismisses.

---

## Base Kit 15–26

### 15 · MullionButton

```kotlin
MullionButton(
    text = "Primary",
    onClick = {},
    variant = MullionButtonVariant.Filled, // Tonal / Outlined / Text
    size = MullionButtonSize.M,            // S 36 / M 44 / L 52
    enabled = true,
    loading = false,
    leadingIcon = null,
    trailingIcon = null,
)
```

Visual height can be 36/44/52dp; touch target is always at least 48dp. Press is a 97% scale with no ripple dependency. Filled uses a volt-to-volt-high gradient and a subtle volt shadow.

`MullionIconButton` is the circular variant.

### 16 · MullionTextField

```kotlin
MullionTextField(
    value = query,
    onValueChange = { query = it },
    label = "Device name",
    placeholder = "Pixel 8 Pro",
    state = MullionFieldState.Default, // Error / Disabled
    supportingText = null,
    leadingIcon = null,
    trailingIcon = null,
    singleLine = true,
)
```

42dp field, 12dp radius, monospace value, uppercase label. Focus raises the border to volt; error uses signal orange; disabled reduces opacity. Wraps `BasicTextField` so it stays custom and accessible.

### 17 · MullionProgressBar

```kotlin
MullionProgressBar(progress = 0.62f, label = "Build", showValue = true)
MullionProgressBar(progress = null, label = "Indeterminate")
MullionProgressBar(progress = null, shape = MullionProgressShape.Circular, circularSize = 28.dp)
```

Linear track is 6dp. The indeterminate loop is drawn on a Canvas, not animated via layout. Circular determinate draws an arc; indeterminate rotates a 280° arc. Both report progress semantics.

### 18 · MullionCheckbox / MullionRadio

```kotlin
MullionCheckbox(checked, onCheckedChange, label = "Hinge avoidance")
MullionCheckbox(checked = false, onCheckedChange = {}, indeterminate = true)
MullionRadio(selected, onSelect, label = "Expanded")
```

48dp row targets. The glyph is drawn on a Canvas: animated fill, check path, indeterminate dash, radio dot. Roles are `Checkbox` / `RadioButton`.

### 19 · MullionSwitch

```kotlin
MullionSwitch(checked, onCheckedChange, label = "Auto-collapse")
```

52×30dp track, 22dp thumb, 22dp travel, spring settle. 48dp row target. Role `Switch`.

### 20 · MullionSlider

```kotlin
MullionSlider(
    value = gutter,
    onValueChange = { gutter = it },
    valueRange = 0f..48f,
    steps = 0,
    label = "Pane gutter",
    valueLabel = { "${it.roundToInt()}dp" },
)
```

5dp track, 20dp thumb, 32dp pointer target. Tap jumps, drag scrubs, optional step snapping. Progress semantics plus accessibility increment/decrement actions. Single cursor in 1.0; range slider is on the roadmap.

### 21 · MullionSegmentedControl

```kotlin
MullionSegmentedControl(
    segments = segmentsFor(currentBreakpoint()),
    selectedIndex = selected,
    onSelected = { selected = it },
)
```

38dp container, 30dp segments, pill indicator. Segment count is the caller's decision so the control can honestly express 2 / 3 / 5 states without hiding children behind an internal breakpoint.

### 22 · MullionBadge

```kotlin
MullionBadge(count = 3) { MullionAvatar("Lin") }
MullionBadge(kind = MullionBadgeKind.Dot) { Icon() }
MullionBadge(text = "NEW") { Icon() }
MullionBadge(count = 120) { Icon() } // renders 99+
```

Dot 11dp, number min 20dp, text 18dp. Badge is anchored top-end with a dark 2dp cutout ring so it reads over avatars.

### 23 · MullionAvatar / MullionAvatarStack

```kotlin
MullionAvatar("Lin Wei")                       // deterministic gradient + initials
MullionAvatar("Dev", size = MullionAvatarSize.L)
MullionAvatarStack(names, max = 4, overlap = 12.dp)
```

Sizes 28/40/56dp. Initials use the first letters of the first two words, or the first two characters for CJK. The stack overlaps by 12dp, adds a `+N` counter and a background ring. An optional `image` slot replaces the initials.

### 24 · MullionCard

```kotlin
MullionCard(
    onClick = { open(it) },
    selected = false,
    contentPadding = PaddingValues(18.dp),
) {
    CardTitle()
    CardBody()
}
```

12dp radius, glass panel, 14dp ambient shadow, press scale 0.985. `selected` adds a volt border. `onClick = null` gives a non-interactive surface.

### 25 · MullionTabs

```kotlin
MullionTabs(
    tabs = tabs.map { MullionTab(it) },
    selectedIndex = selected,
    onSelected = { selected = it },
    overflowAfter = if (currentBreakpoint() >= Bp.Large) 5 else null,
)
```

Scrolls when `overflowAfter = null`; otherwise keeps N tabs visible and puts the rest behind `⋯`. Selecting an overflow item is reflected on the trigger. 38dp height, 2dp gradient indicator, optional badges.

### 26 · MullionSnackbar

```kotlin
val host = rememberMullionSnackbarHostState()
val scope = rememberCoroutineScope()

Box(Modifier.fillMaxSize()) {
    Screen()
    MullionSnackbarHost(host)
}

// trigger
scope.launch {
    val result = host.showSnackbar(
        message = "Switched to Bp 840",
        actionLabel = "Undo",
        durationMillis = 3_200,
    )
}
```

Placement follows navigation mode: full width above a bottom bar, 420dp capsule with a rail, 292dp capsule at the trailing edge with a drawer. Auto-dismiss is owned by the host, so a configuration change re-renders the same state. The action completes a `CompletableDeferred` with `ActionPerformed` / `Dismissed`.

---

## Design tokens

`dev.mullion.compose.theme`

| Token | Value |
| --- | --- |
| `MullionColors.Background` | `#04070C` |
| `MullionColors.Volt` | `#35E8FF` |
| `MullionColors.VoltHigh` | `#84F2FF` |
| `MullionColors.Signal` | `#FF8354` |
| `MullionColors.Mint` | `#3EE8A8` |
| `MullionColors.Amber` | `#FFD166` |
| `MullionColors.Ink` | `#EDF4FA` |
| `MullionColors.Muted` | `#94A9BB` |
| `MullionColors.Faint` | `#5F7688` |
| `MullionMotion.MicroMillis` | 120ms |
| `MullionMotion.StateMillis` | 180ms |
| `MullionMotion.PaneMillis` | 240ms |
| `MullionMotion.SheetMillis` | 340ms |
| `MullionMotion.DockMillis` | 380ms |
| `MullionMotion.SnackbarMillis` | 3200ms |
| `MullionDimens.touchTarget` | 48dp |
| `MullionDimens.radiusWindow` | 28dp |
| `MullionDimens.radiusPanel` | 18dp |
| `MullionDimens.radiusCard` | 12dp |

Glass is built from translucent gradients and a 1dp light edge. Compose cannot sample a live backdrop before Android 12, so MULLION does not promise a live blur on API 21; the glass reads consistently from API 21 through 36. `GlassLevel` controls fill depth and elevation.

### Accessibility

- Every interactive control has a 48dp target, role and semantics.
- Draggable divider and slider expose custom actions.
- Dialog/sheet panels request focus and expose pane titles.
- Snackbar is a polite live region.
- Tab/radio groups use selectable semantics.

### Motion

Motion is short and structural: 120ms for control press, 180ms for state color, 240ms for panes, 340–380ms for sheets. Reduced-motion support is a roadmap item; there is no essential motion that blocks interaction today.
