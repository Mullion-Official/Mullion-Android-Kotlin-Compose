# Changelog

All notable changes to MULLION are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
semantic versioning.

## [1.0.0] — 2026-10-31

### Added

- `mullion-core`: five width classes (320/480/600/840/1200dp), navigation
  morphology, `PaneStrategy` / `PanePlan`, hinge geometry (`HingeInfo`,
  `Foldables`), shared metrics, type steps, insets and immutable
  `MullionWindowState`.
- `mullion-compose`: adaptive light/dark MULLION theme (`MullionPalette`,
  `LocalMullionPalette`, `MullionColors` facade), palette-aware glass recipe,
  window provider with `WindowInfoTracker` + system/IME insets, 14 adaptive
  components and 12 base components (26 total).
- `mullion-view`: `MullionBreakpointLayout`, `MullionPaneLayout`,
  `MullionGlassLayout`, `MullionCardView`, `MullionButtonView`,
  `ResourceScaler`, `MullionEdgeToEdge` and the `values-w600dp` /
  `values-w840dp` resource buckets. Colors now resolve from `values` /
  `values-night` via `MullionPalette.of(context)`.
- `sample`: a real reference application — an equipment-operations console
  with a repository-backed `ReferenceViewModel`, search/filter, list-detail-
  preview, restart confirmation, actual runtime permissions, snackbars,
  activity timeline, settings and window diagnostics. The reference app is
  deliberately light-first and product-neutral, not a dark sci-fi showroom.
  The 26-module cabinet is kept as a separate `CabinetActivity` reachable from
  Settings and follows the same adaptive theme.
- 33 `mullion-core` unit tests covering thresholds, pane plans, fold splits,
  resource buckets, type steps and insets.
- 5 `sample` ViewModel tests covering load, selection cleanup, restart success,
  offline restart failure and permission results.
- `MultiPane` / `SearchLayout` content-width fallback: a preview/filter rail is
  dropped before the detail pane can collapse into a one-character column.
- Immersive bottom navigation: translucent overlay above scrolling content,
  system navigation bar transparent with contrast enforcement disabled, and
  the overlay still receives the bottom safe inset.
- Apache-2.0 license, contributor docs, security policy, issue/PR templates,
  GitHub Actions CI and a static design reference (`docs/showcase.html`).
- Maven publications with sources jars for `dev.mullion:core`,
  `dev.mullion:compose` and `dev.mullion:view`.

### Verified

- Built with JDK 21, Gradle 8.14.3, AGP 8.13.2, Kotlin 2.2.20, Compose BOM
  2025.09.00, compileSdk 36, minSdk 21.
- Reference app installed and exercised on a Pixel 7 API 36 emulator at 411dp
  and 1280dp, and on a 7.6" Fold-in API 36 AVD at open 673dp / cover 337dp with
  postures 1–3, real permission dialogs and adb-driven assertions.
- A 1080×1920 / 64.5s demo video of the reference app, recorded in full-screen
  demo mode with intro/outro and real interaction.

### Known limitations

- Window 1.4 cannot distinguish TENT from HALF_OPENED; inject posture through
  `MullionWindowProvider` if the host knows it.
- One hinge per window in 1.0; multi-fold devices are roadmap work.
- Glass is a deterministic gradient recipe, not a live backdrop blur on
  API 21–30.
- The slider is single-cursor in 1.0; range selection is roadmap work.
- The published coordinates are ready for Maven Central but the repository does
  not claim an external Central release in this snapshot.
