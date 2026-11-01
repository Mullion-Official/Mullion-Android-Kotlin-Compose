# Publishing

> Three modules, one group, Apache-2.0.

## Coordinates

```text
dev.mullion:core:1.0.0
dev.mullion:compose:1.0.0
dev.mullion:view:1.0.0
```

Each module applies `maven-publish`, publishes the release variant and attaches a sources jar. POM metadata includes the Apache-2.0 license, the repository SCM and the MULLION contributor entry.

## Publish to the repository-local Maven repo

The build is configured to write a Maven layout under the root `build/repo`:

```bash
./gradlew \
  :mullion-core:publishReleasePublicationToLocalRepoRepository \
  :mullion-compose:publishReleasePublicationToLocalRepoRepository \
  :mullion-view:publishReleasePublicationToLocalRepoRepository
```

Output:

```text
build/repo/dev/mullion/core/1.0.0/core-1.0.0.aar
build/repo/dev/mullion/core/1.0.0/core-1.0.0-sources.jar
build/repo/dev/mullion/core/1.0.0/core-1.0.0.pom
build/repo/dev/mullion/compose/1.0.0/...
build/repo/dev/mullion/view/1.0.0/...
```

Consume it from a sibling project:

```kotlin
repositories {
    maven { url = uri("../mullion/build/repo") }
}

dependencies {
    implementation("dev.mullion:core:1.0.0")
    implementation("dev.mullion:compose:1.0.0")
}
```

## Publish to Maven Central

The publications are ready; Central requires a few things that are intentionally not committed:

1. Register the `dev.mullion` group ID.
2. Add a signing configuration (`signing` plugin + in-memory GPG key from CI secrets).
3. Add the Sonatype Central repository and staging tasks.
4. Fill `~/.gradle/gradle.properties` with credentials.

A typical signing block:

```kotlin
plugins {
    signing
}

signing {
    val signingKey: String? = providers.gradleProperty("signingKey").orNull
    val signingPassword: String? = providers.gradleProperty("signingPassword").orNull
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}
```

Then add the Central repository:

```kotlin
publishing {
    repositories {
        maven {
            name = "central"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = providers.gradleProperty("centralUsername").orNull
                password = providers.gradleProperty("centralPassword").orNull
            }
        }
    }
}
```

Verify before uploading:

```bash
./gradlew publishAllPublicationsToLocalRepoRepository
# inspect the POMs, AARs and sources jars
```

## Versioning

- `1.0.0` is the first public API.
- `mullion-core` is the compatibility boundary: changing `Bp` thresholds is a breaking change.
- Compose and View depend on core through `api`, so consumers see a single core version.
- The sample is not published.

## Artifact contents

| Artifact | What it contains |
| --- | --- |
| `core` | pure Kotlin decisions + 33 tests (tests are not packaged) |
| `compose` | theme, window provider, 26 Compose modules |
| `view` | 4 View classes, 2 glass views, ResourceScaler, EdgeToEdge, resources/attrs |

All three are `minSdk 21`, `compileSdk 36`, Java 17 bytecode, Apache-2.0.


## JitPack

The repository ships `jitpack.yml` with an explicit multi-module install:

```yaml
jdk:
  - openjdk17
install:
  - ./gradlew :mullion-core:publishToMavenLocal :mullion-compose:publishToMavenLocal :mullion-view:publishToMavenLocal
```

On JitPack the build scripts switch the publication coordinates to:

```text
com.github.Mullion-Official.Mullion-Android-Kotlin-Compose:mullion-core:TAG
com.github.Mullion-Official.Mullion-Android-Kotlin-Compose:mullion-compose:TAG
com.github.Mullion-Official.Mullion-Android-Kotlin-Compose:mullion-view:TAG
```

JitPack also exposes the aggregate artifact:

```text
com.github.Mullion-Official:Mullion-Android-Kotlin-Compose:TAG
```

`1.0.0` was built successfully and all three AARs were verified from the
JitPack repository. `mullion-compose` and `mullion-view` POMs correctly point
at the JitPack `mullion-core` coordinate.

The normal `dev.mullion:*` coordinates remain unchanged for local/Maven Central
publishing. JitPack is selected by its `JITPACK=true` environment variable, so
there is no second source tree.
