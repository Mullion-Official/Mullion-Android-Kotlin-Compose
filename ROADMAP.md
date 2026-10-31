# Roadmap

MULLION 1.0 is the “one ruler, two tiers, 26 modules” release. The roadmap is
deliberately short: adaptation is the product, not a growing widget catalogue.

## 1.0 — shipped

- 5 width classes and a single resolver.
- 14 adaptive + 12 base Compose modules.
- View structural tier + resource scaler + glass views.
- Fold-aware DockedSheet / FoldablePane.
- 26-module live sample at 375 / 760 / 1180.
- 33 core unit tests, Apache-2.0, Maven publications.
- Real-device verification at 411 / 800 / 1280dp.

## 1.1 — platform polish

- **Maven Central release** of `dev.mullion:core/compose/view` with signed
  artifacts and a Bill of Materials.
- **Range slider** (two cursors) with the same clamp/step semantics.
- **Reduced-motion mode**: honor `Settings.Global.ANIMATOR_DURATION_SCALE` and
  Compose’s motion scale; keep sheet/dialog state transitions readable.
- **Multi-hinge geometry** (`HingeSet`) for tri-fold and dual-screen devices.
- **More Window 1.5 posture states** once they are available at minSdk 21
  without raising the baseline.
- **View-tier parity for fold geometry**: a `MullionFoldLayout` that consumes
  `HingeInfo` and splits two leaves.

## 1.2 — developer experience

- Compose preview annotations / multipreview for 320, 480, 600, 840, 1200 and
  fold postures.
- Screenshot tests for the 26 trays (Roborazzi or Paparazzi).
- A Gradle lint rule that flags raw width thresholds (`screenWidthDp >=`) in
  modules that depend on MULLION.
- Optional KSP validator for `PaneStrategy` maps and resource bucket parity.
- Light theme with a real backdrop blur on API 31+ while preserving the API 21
  fallback.

## Not planned

- A full Material replacement. MULLION supplies structure and a glass skin, not
  every widget.
- An icon pack. Icons are slots so apps keep their own visual language.
- Networking, permissions, storage or analytics.
- A second breakpoint table for “special” screens.

## How to influence the roadmap

Open a discussion with the concrete width/hinge/inset scenario you are trying to
solve. The best proposals include a reproduction at two adjacent classes
(e.g. 599 and 600) and a screenshot of the failure mode.
