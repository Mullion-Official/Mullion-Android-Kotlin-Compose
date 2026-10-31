plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

android {
    namespace = "dev.mullion.compose"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
        )
    }
}


group = "dev.mullion"
version = "1.0.0"

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
                artifactId = "compose"
                pom {
                    name.set("MULLION Compose")
                    description.set("Adaptive and base Compose components for MULLION. 320dp to 1200dp, fold-aware.")
                    url.set("https://github.com/mullion-android/mullion")
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
                            url.set("https://github.com/mullion-android/mullion")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/mullion-android/mullion.git")
                        developerConnection.set("scm:git:ssh://github.com/mullion-android/mullion.git")
                        url.set("https://github.com/mullion-android/mullion")
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
    api(project(":mullion-core"))
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.foundation)
    api(libs.compose.material3)
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.window)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
}
