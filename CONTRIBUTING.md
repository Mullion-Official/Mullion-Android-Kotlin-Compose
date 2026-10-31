# Contributing to MULLION

Thanks for helping make Android adaptation less repetitive. This project aims for a small, sharp API rather than a large catalogue.

## Ground rules

- Be respectful. See [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
- Discuss large API additions in an issue before writing them.
- Keep the framework reflection-free and usable from **minSdk 21**.
- Do not add a second breakpoint table. If a new policy is needed, express it as
  `PaneStrategy` / a core value, not an `if (width)` in a screen.
- By contributing, you agree that your contribution is licensed under
  Apache-2.0.

## Repository layout

```text
mullion-core/     pure decisions, 33 unit tests, no Compose/View imports
mullion-compose/  theme, window provider, adaptive + base components
mullion-view/     structural ViewGroup + glass views + resources
sample/           the 26-module live cabinet
docs/             architecture, breakpoints, foldables, components, view tier
```

## Development setup

Requirements:

- JDK 17+ (the verified build uses JDK 21)
- Android SDK Platform 36 + Build Tools 36
- Android Studio is optional; CLI works

```bash
./gradlew :mullion-core:testDebugUnitTest
./gradlew :sample:assembleDebug
./gradlew :sample:installDebug
```

If Gradle cannot find the SDK, create `local.properties`:

```properties
sdk.dir=/absolute/path/to/Android/sdk
```

## API rules

A public API is a promise. Before adding one:

1. Is the decision pure? Put it in `mullion-core` if so.
2. Does it remove a width/inset/hinge decision from application code? If not, it
   probably belongs in the app.
3. Can it be previewed without a device? Accept an override (`mode`, `hinge`,
   `simulatedInsets`) rather than reading global state.
4. Does it work at 320dp, 599dp, 600dp, 839dp, 840dp, 1199dp and 1200dp?
5. Is the touch target at least 48dp? Are roles and labels present?
6. Does it keep existing state across a resize?

## Adding a component

1. Add the pure policy/geometry to `mullion-core` and a test if applicable.
2. Add a Composable under `mullion-compose/src/main/kotlin/dev/mullion/compose/adaptive`
   or `.../base`.
3. Use `MullionColors` / `MullionMotion` / `MullionTextStyles`; do not hardcode
   new colors or durations without a token.
4. Add a sample demo card in `sample/.../showcase`.
5. Add a section to `docs/components.md` and the static cabinet if appropriate.
6. Run the tests and assemble the sample.

## Style

- Kotlin official style (4 spaces, trailing commas where useful).
- Public types and functions need KDoc explaining the decision, not the syntax.
- Prefer explicit state (`data class` / immutable) over globals.
- Avoid new dependencies in `mullion-core`; it should remain tiny.
- Compose dependencies for consumers are `api` only when types are exposed.
- No reflection. No `values-w820dp` magic numbers in app code.
- Comments in English; user-facing copy may be Chinese or bilingual.

## Testing

- Pure logic: JUnit in `mullion-core/src/test`.
- Broken boundaries are bugs: test 479/480, 599/600, 839/840, 1199/1200.
- Fold geometry: test no hinge, vertical, horizontal, flat, too-narrow.
- Compose/View visual behavior: use the sample; attach screenshots to PRs when
  layout changes.

```bash
./gradlew :mullion-core:testDebugUnitTest
./gradlew :sample:assembleDebug
```

## Commit and PR

- Keep commits focused; “core: …”, “compose: …”, “sample: …”, “docs: …” prefixes
  are welcome.
- Describe the user-visible behavior and the tested widths.
- Include a before/after if the change affects layout.
- Do not bump the version or changelog for a normal PR; maintainers do that on
  release.
- CI must pass (`Android CI`).

## Release checklist (maintainers)

1. Update `CHANGELOG.md` and `Mullion.VERSION` / module versions.
2. Run the full build and the 33 core tests.
3. Publish to the local Maven repo and inspect POMs/AARs/sources.
4. Tag `vX.Y.Z`.
5. Publish to Maven Central / your private repo.
6. Update the static docs and screenshots if the UI changed.
