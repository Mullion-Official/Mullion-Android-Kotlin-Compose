# Verification

> What was actually built, installed and tapped — not only what compiles.

## Environment

| Item | Value |
| --- | --- |
| Host | macOS, Apple silicon |
| JDK | 21.0.12 |
| Gradle | 8.14.3 |
| AGP | 8.13.2 |
| Kotlin | 2.2.20 |
| Compose BOM | 2025.09.00 |
| compileSdk / targetSdk | 36 |
| minSdk | 21 |
| Device A | Pixel 7 AVD, API 36, 1080×2400 @420dpi |
| Device B | 7.6" Fold-in AVD, API 36, 1768×2208 @420dpi (cover 884×2208) |

## Automated checks

```bash
./gradlew :mullion-core:testDebugUnitTest
./gradlew :sample:testDebugUnitTest
./gradlew :sample:assembleDebug
```

Result:

```text
:mullion-core:testDebugUnitTest  33 tests, 0 failures
:sample:testDebugUnitTest         5 tests, 0 failures
:sample:assembleDebug             BUILD SUCCESSFUL
```

The five reference-app tests cover:

- fleet loading and 3/1/1 status counts,
- filtering away the selected device clears selection,
- online restart updates status, removes the critical alert and appends a timeline event,
- offline restart fails without mutating state,
- runtime permission results update the granted map and close the sheet.

Core tests cover:

- all five width boundaries (479/480, 599/600, 839/840, 1199/1200),
- navigation morphology,
- PaneStrategy defaults/caps/custom maps,
- no hinge / vertical / horizontal / flat / too-narrow fold geometry,
- side-sheet minimums,
- resource buckets and 48/80/120dp, 32/44/56sp steps,
- inset folding and content-size arithmetic.

## Reference app ADB acceptance

The final APK launches the device-operations reference app, not the component cabinet.

| Check | Result |
| --- | --- |
| Cold launch loads repository: 5 devices, 3 online, 3 alerts | PASS |
| Workbench alert → Devices screen, selected B4, compact detail | PASS |
| Device detail tabs: 概览 / 指标 / 日志 switch and render | PASS |
| 重启设备 → AdaptiveDialog → 确认重启 → B4 online, alerts 3→2, timeline event | PASS |
| Offline D3 restart is disabled at UI and rejected by repository test | PASS |
| 扫描机身码 → PermissionSheet → 全部允许 → real CAMERA / LOCATION / POST_NOTIFICATIONS dialogs → 3/3 已授予 in Settings | PASS |
| 仅本次 path closes the sheet and reports session grant via Snackbar | PASS |
| Search / segmented filter / DockedSheet filter; hidden selection is cleared | PASS |
| System back from compact detail returns to the list | PASS |
| Settings: site field, switches, pane-gutter slider, window diagnostics, reload | PASS |
| Settings → 组件柜 opens CabinetActivity; back returns to reference app | PASS |
| 411dp BottomBar single pane; 673dp Rail two panes; 914dp Drawer two panes after preview fallback; 1280dp Drawer three panes | PASS |
| `demo_fullscreen` hides system bars for clean recording; normal launch restores them | PASS |

Evidence: `docs/images/ref-workbench.png`, `ref-devices-fold.png`, `ref-devices-3pane.png`, `ref-settings.png`, `ref-device-detail.png`, `ref-activity.png`.

### Why 914dp shows two panes

At 914dp the window class is `Large`, but the open Drawer takes 216dp, leaving ~698dp of content. Three preferred panes (380 + ~300 + gutters) would leave the detail under 120dp. `MultiPane` detects this from `BoxWithConstraints` and drops the preview; `SearchLayout` does the same for its filter rail. This is a deliberate content-width fallback, not a breakpoint bug.

## Foldable ADB acceptance

The final APK was installed on the 7.6" Fold-in AVD and driven with `adb shell input` + `uiautomator` assertions.

| Check | Result |
| --- | --- |
| Open display: 1768×2208 = 673dp → wide nav (`ADAPTIVE 14`, `BASE 12`) | PASS |
| Folded cover width: 884×2208 = 337dp → wide nav hidden, single-column hero | PASS |
| Restore open width → wide nav returns | PASS |
| Device states 1 (closed), 2 (half-opened), 3 (opened); app stays alive | PASS |
| Index jump → FoldablePane at 375dp → “窗格不足 240dp · 单栏避让” | PASS |
| FoldablePane at 760dp → `LEAF A` + `LEAF B` split | PASS |
| DockedSheet at 1180dp → side sheet with `确认` and `SHEET CONTENT` | PASS |
| AdaptiveDialog at 1180dp → side panel `侧滑面板 · 窄屏居中` | PASS |
| MultiPane at 760dp → list + detail; 1180dp → preview; 375dp detail + back | PASS |
| DockedSheet close/reopen, hinge toggle HALF → FLAT | PASS |
| AdaptiveDialog dismiss/reopen | PASS |
| Base Kit 760 → 1180 viewport; Button/TextField/ProgressBar/Selection/Slider/Avatar/Card/Tabs/Snackbar render | PASS |

Evidence screenshots:

- `docs/images/fold-open.png` — 673dp open width
- `docs/images/fold-folded.png` — 337dp cover width
- `docs/images/fold-pane.png` — FoldablePane split at 760dp
- `docs/images/fold-docked-sheet.png` — side-docked sheet
- `docs/images/fold-dialog.png` — side-panel dialog
- `docs/images/fold-cover.png` — folded logical width

### Emulator limitation

The AOSP foldable image in this environment reports the hinge sensor and device state, but does **not** surface a `FoldingFeature` through `WindowInfoTracker`. The framework implementation is present (`MullionWindow.kt`) and the pure hinge geometry is fully unit-tested. The sample's fold/multi-pane trays use explicit `HingeInfo` values so the visual behavior is deterministic and can be verified without relying on the emulator's display-feature stack.

On a physical foldable (Pixel Fold / 9 Pro Fold), `WindowInfoTracker` supplies `FoldingFeature` bounds and MULLION maps them to dp geometry.

## Phone / tablet checks

- Pixel 7: 411dp compact, 800dp tablet window and 1280dp XL window rendered.
- Base kit: 1 / 2 / 3 column grid at the corresponding real widths.
- Adaptive dialog side panel, DockedSheet bottom/side, FoldablePane fallback/split, MultiPane 1/2/3 panes.
- View interop: real `MullionCardView` + `MullionButtonView` rendered inside `AndroidView`; clicking the View button updates its TextView.
- Snackbar triggered from adb tap; action/dismiss path completed.

## Demo video

`docs/media/mullion-demo.mp4`

- 1080×1920, 30fps, H.264, 64.5s, no audio (add your own Douyin music).
- Intro/outro title cards + two live reference-app takes:
  workbench → B4 detail → metrics/logs → restart confirm → permission sheet → filter → settings.
- Recorded in `demo_fullscreen` mode so no status/navigation bars appear.
- A cover image is at `docs/media/mullion-cover.png`.

## Known test limitations

- Hinge-folding animation cannot be observed as a display resize on this AOSP foldable image; width reflow was tested by setting the cover/open logical sizes and postures separately.
- No instrumentation screenshot test suite yet; the sample and adb assertions are the current integration surface.
- TENT posture is not distinguishable through Window 1.4; explicit `HingeInfo` injection is used.
