@file:Suppress("InvalidPackageDeclaration")

object Plugins {

    object Version {

        // Releases: https://blog.jetbrains.com/kotlin/category/releases
        // Release: https://github.com/JetBrains/kotlin/releases/tag/v1.7.10
        @Suppress("unused") private const val KOTLIN = "1.7.10" // Released: 08-07-22

        // Release: https://github.com/JetBrains/kotlin/releases/tag/v1.7.20-Beta
        @Suppress("unused") private const val KOTLIN_BETA = "1.7.20-Beta" // Released: 01-08-22
        const val KOTLIN_JETPACK_COMPOSE = KOTLIN // Released: 20-04-22

        // Releases: https://androidstudio.googleblog.com
        // Release: https://androidstudio.googleblog.com/2022/05/android-studio-chipmunk-available-in.html
        @Suppress("unused") const val ANDROID_GRADLE = "7.2.1" // Released: 25-05-22

        // Release: https://androidstudio.googleblog.com/2022/06/android-studio-dolphin-beta-2-now.html
        @Suppress("unused") const val ANDROID_GRADLE_BETA = "7.3.0-beta03" // Released: 06-06-22

        // Release: https://androidstudio.googleblog.com/2022/06/android-studio-electric-eel-canary-3.html
        const val ANDROID_GRADLE_CANARY = "7.4.0-alpha04" // Released: 13-06-22

        // Releases: https://github.com/GradleUp/auto-manifest/releases
        const val ANDROID_MANIFEST = "2.0" // Released: 16-04-22

        // Releases: https://github.com/detekt/detekt/releases
        @Suppress("unused") const val DETEKT = "1.20.0" // Released: 15-04-22
        const val DETEKT_RC = "1.21.0-RC1" // Released: 03-06-22

        // Releases: https://github.com/ben-manes/gradle-versions-plugin/releases
        const val DEPENDENCY_VERSIONS = "0.42.0" // Released: 04-02-22

        // Releases: https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin/releases
        const val DEPENDENCY_ANALYSIS = "1.4.0" // Released: 30-05-22
        // const val DEPENDENCY_ANALYSIS = "1.5.0" // Released: 07-06-22 // TODO: Try with newer version of the plugin.

        // Releases: https://github.com/runningcode/gradle-doctor/releases
        const val GRADLE_DOCTOR = "0.8.1" // Released: 31-05-22

        // Releases: https://github.com/jraska/modules-graph-assert/releases
        const val MODULE_GRAPH_ASSERT = "2.2.0" // Released: 18-01-22

        // Releases: https://github.com/jacoco/jacoco/releases
        const val JACOCO = "0.8.8" // Released: 05-04-22

    }

    const val KOTLIN_GRADLE = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.KOTLIN_JETPACK_COMPOSE}"
    const val ANDROID_GRADLE = "com.android.tools.build:gradle:${Version.ANDROID_GRADLE_CANARY}"
    const val ANDROID_MANIFEST = "com.gradleup:auto-manifest-plugin:${Version.ANDROID_MANIFEST}"
    const val DETEKT = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${Version.DETEKT_RC}"
    const val DETEKT_FORMATTING = "io.gitlab.arturbosch.detekt:detekt-formatting:${Version.DETEKT_RC}"
    const val DEPENDENCY_VERSIONS = "com.github.ben-manes:gradle-versions-plugin:${Version.DEPENDENCY_VERSIONS}"
    const val DEPENDENCY_ANALYSIS =
        "com.autonomousapps:dependency-analysis-gradle-plugin:${Version.DEPENDENCY_ANALYSIS}"
    const val GRADLE_DOCTOR = "com.osacky.doctor:doctor-plugin:${Version.GRADLE_DOCTOR}"

    object Id {

        @Suppress("MemberNameEqualsClassName")
        object Kotlin {

            object Android {

                const val ANDROID = "kotlin-android"
                const val ANDROID_MANIFEST = "com.gradleup.auto.manifest"

            }

            const val KOTLIN = "kotlin"
            const val KAPT = "kotlin-kapt"

        }

        object Android {

            const val APPLICATION = "com.android.application"
            const val LIBRARY = "com.android.library"

        }

        object Quality {

            const val DETEKT = "io.gitlab.arturbosch.detekt"

        }

        object Test {

            const val JACOCO = "jacoco"
            const val COVERAGE = "io.petros.movies.coverage"

        }

        object Dependency {

            const val VERSIONS = "com.github.ben-manes.versions"
            const val ANALYSIS = "com.autonomousapps.dependency-analysis"

        }

        object Gradle {

            const val DOCTOR = "com.osacky.doctor"

        }

        object Module {

            const val GRAPH_ASSERT = "com.jraska.module.graph.assertion"

        }

    }

}
