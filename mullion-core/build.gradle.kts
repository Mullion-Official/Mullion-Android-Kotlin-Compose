plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "dev.mullion.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        explicitApi()
    }
}


val isJitPack = System.getenv("JITPACK") == "true"
val jitPackGroup = "com.github.Mullion-Official.Mullion-Android-Kotlin-Compose"

group = if (isJitPack) jitPackGroup else "dev.mullion"
version = if (isJitPack) System.getenv("VERSION") ?: "1.0.0" else "1.0.0"

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifactId = if (isJitPack) "mullion-core" else "core"
                pom {
                    name.set("MULLION Core")
                    description.set("Breakpoint, pane, fold and window metrics engine for MULLION. Zero reflection.")
                    url.set("https://github.com/Mullion-Official/Mullion-Android-Kotlin-Compose")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                    developers {
                        developer {
                            id.set("mullion")
                            name.set("MULLION contributors")
                            url.set("https://github.com/Mullion-Official/Mullion-Android-Kotlin-Compose")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/Mullion-Official/Mullion-Android-Kotlin-Compose.git")
                        developerConnection.set("scm:git:ssh://github.com/Mullion-Official/Mullion-Android-Kotlin-Compose.git")
                        url.set("https://github.com/Mullion-Official/Mullion-Android-Kotlin-Compose")
                    }
                }
            }
        }
        repositories {
            maven {
                name = "localRepo"
                url = uri(rootProject.layout.buildDirectory.get().asFile.resolve("repo"))
            }
        }
    }
}


dependencies {
    testImplementation(libs.junit)
}
