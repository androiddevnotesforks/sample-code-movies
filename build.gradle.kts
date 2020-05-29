@file:Suppress("InvalidPackageDeclaration", "DEPRECATION")

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.CompileOptions
import com.android.build.gradle.internal.dsl.DefaultConfig
import com.android.build.gradle.internal.dsl.LintOptions
import com.android.build.gradle.internal.dsl.PackagingOptions
import com.android.build.gradle.internal.dsl.TestOptions
import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import de.mannodermaus.gradle.plugins.junit5.AndroidJUnitPlatformPlugin
import de.mannodermaus.gradle.plugins.junit5.junitPlatform
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.petros.movies.config.Build
import io.petros.movies.config.Config
import io.petros.movies.config.Sources
import io.petros.movies.config.android.Android
import io.petros.movies.config.android.App
import io.petros.movies.config.android.LocalProperties
import io.petros.movies.config.android.findLocalProperty
import io.petros.movies.config.kotlin.Java
import io.petros.movies.config.tests.Logs
import io.petros.movies.config.tests.Tests
import io.petros.movies.config.utils.Utils
import io.petros.movies.config.utils.logCredentials
import io.petros.movies.config.utils.logModule
import io.petros.movies.config.utils.logPlugin
import io.petros.movies.config.utils.logVariant
import io.petros.movies.plugin.coverage.CoverageExtension
import io.petros.movies.plugin.coverage.CoveragePlugin
import io.petros.movies.plugin.coverage.CoverageTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.android.build.gradle.AppPlugin as AndroidApplicationPlugin
import com.android.build.gradle.LibraryPlugin as AndroidLibraryPlugin
import com.android.build.gradle.api.AndroidSourceSet as AndroidSourceSetLegacy
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin as KotlinKaptPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper as KotlinAndroidPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper as KotlinPlugin

/* BUILD SCRIPT */

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Plugins.KOTLIN)
        classpath(Plugins.ANDROID)
        classpath(Plugins.ANDROID_NAVIGATION)
        classpath(Plugins.ANDROID_MANIFEST)
        classpath(Plugins.DETEKT)
        classpath(Plugins.ANDROID_J_UNIT_5)
        classpath(Plugins.VERSIONS)
    }
}

/* PLUGINS */

plugins {
    id(Plugins.Id.Kotlin.Android.ANDROID_MANIFEST) version Plugins.Version.ANDROID_MANIFEST
}

autoManifest {
    packageName.set(App.APPLICATION_ID)
    applyRecursively.set(true)
    replaceDashesWithDot.set(true)
}

/* PROJECTS CONFIGURATION */

allprojects {
    val githubUsername = findLocalProperty(LocalProperties.GitHub.USERNAME)
    val githubPassword = findLocalProperty(LocalProperties.GitHub.PASSWORD)
    logCredentials(Pair(githubUsername, githubPassword))
    repositories {
        google()
        jcenter()
        maven(url = Repos.Url.GitHub.STATEFUL) {
            credentials {
                username = githubUsername
                password = githubPassword
            }
        }
    }
}

/* PROJECT PLUGINS CONFIGURATION */

subprojects {
    subprojectsPlugins()
    subprojectsTasks()
}

fun Project.subprojectsPlugins() {
    plugins.withType(KotlinPlugin::class) {
        logPlugin(Plugins.Id.Kotlin.KOTLIN)
        java { java() }
        sourceSets()
        testOptions()
    }
    plugins.withType(KotlinKaptPlugin::class) {
        logPlugin(Plugins.Id.Kotlin.KAPT)
        kapt { kapt() }
    }
    plugins.withType(KotlinAndroidPlugin::class) {
        logPlugin(Plugins.Id.Kotlin.Android.ANDROID)
    }
    plugins.withType(AndroidLibraryPlugin::class) {
        logPlugin(Plugins.Id.Android.LIBRARY)
        androidLibrary { androidLibrary() }
    }
    plugins.withType(AndroidApplicationPlugin::class) {
        logPlugin(Plugins.Id.Android.APPLICATION)
        androidApplication { androidApplication() }
    }
    plugins.withType(DetektPlugin::class) {
        logPlugin(Plugins.Id.Quality.DETEKT)
        detekt { detekt() }
    }
    plugins.withType(AndroidJUnitPlatformPlugin::class) {
        logPlugin(Plugins.Id.Test.Android.J_UNIT_5)
        androidBase { androidBase() }
    }
    plugins.withType(VersionsPlugin::class) {
        logPlugin(Plugins.Id.Dependency.VERSIONS)
    }
    plugins.withType(JacocoPlugin::class) {
        logPlugin(Plugins.Id.Test.JACOCO)
        jacocoRobolectric()
    }
    plugins.withType(CoveragePlugin::class) {
        logPlugin(Plugins.Id.Test.COVERAGE)
        coverage { coverage() }
    }
}

fun Project.subprojectsTasks() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions { kotlinOptions() }
    }
    tasks.withType<DependencyUpdatesTask> {
        versionsOptions()
    }
    tasks.withType<Zip> {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    afterEvaluate {
        val isKotlinModule = pluginManager.hasPlugin(Plugins.Id.Kotlin.KOTLIN)
        logModule(isKotlinModule)
        tasks.create(Build.Tasks.JACOCO, JacocoReport::class) { jacoco(isKotlinModule) }
    }
    tasks.withType<CoverageTask> {
        dependsOn(Build.Tasks.JACOCO)
    }
}

/* *********************************************************************************************************************** */

/* PLUGINS EXTENSION FUNCTIONS */

fun Project.java(configure: JavaPluginExtension.() -> Unit) =
    extensions.configure(JavaPluginExtension::class.java, configure)

fun Project.sourceSets() {
    configure<SourceSetContainer> {
        mainSourceSets()
        testSourceSets()
    }
}

fun Project.testOptions() {
    tasks.getByName<Test>(Build.Tasks.TEST) {
        testLogging()
        testJUnit5()
    }
}

fun Project.kapt(configure: KaptExtension.() -> Unit) =
    extensions.configure(KaptExtension::class.java, configure)

fun Project.androidBase(configure: BaseExtension.() -> Unit) =
    extensions.configure(BaseExtension::class.java, configure)

fun Project.androidLibrary(configure: LibraryExtension.() -> Unit) =
    extensions.configure(LibraryExtension::class.java, configure)

fun Project.androidApplication(configure: AppExtension.() -> Unit) =
    extensions.configure(AppExtension::class.java, configure)

fun Project.detekt(configure: DetektExtension.() -> Unit) =
    extensions.configure(DetektExtension::class.java, configure)

fun Project.coverage(configure: CoverageExtension.() -> Unit) =
    extensions.configure(CoverageExtension::class.java, configure)

/* CONFIGURATION EXTENSION FUNCTIONS */

fun JavaPluginExtension.java() {
    sourceCompatibility = Java.version
    targetCompatibility = Java.version
}

@Suppress("SpreadOperator")
fun Test.testLogging() {
    testLogging {
        events(*Logs.events)
        setExceptionFormat(Logs.EXCEPTION_FORMAT)
        debug {
            events(*Logs.debugEvents)
            setExceptionFormat(Logs.DEBUG_EXCEPTION_FORMAT)
        }
        showExceptions = true
        showCauses = true
        showStackTraces = true
        info.events = debug.events
        info.exceptionFormat = debug.exceptionFormat
        addTestListener(object : TestListener {
            override fun beforeSuite(desc: TestDescriptor) = Unit
            override fun beforeTest(desc: TestDescriptor) = Unit
            override fun afterTest(desc: TestDescriptor, result: TestResult) = Unit
            override fun afterSuite(desc: TestDescriptor, result: TestResult) {
                if (desc.parent != null) println(Logs.testConsoleOutput(result))
            }
        })
    }
}

fun Test.testJUnit5() {
    useJUnitPlatform { includeEngines(Tests.Engine.JUnit.VINTAGE, Tests.Engine.Spek.SPEK) }
}

fun KaptExtension.kapt() {
    useBuildCache = true
}

fun BaseExtension.androidBase() {
    testOptions { testOptionsJUnit5() }
}

fun LibraryExtension.androidLibrary() {
    compileSdkVersion(Android.Sdk.COMPILE)
    defaultConfig { defaultConfig() }
    packagingOptions { packagingOptions() }
    compileOptions { compileOptions() }
    sourceSets { androidSourceSets() }
    testOptions { testOptions() }
    lintOptions { lintOptions() }
    buildFeatures()
    variantOptions()
}

fun AppExtension.androidApplication() {
    compileSdkVersion(Android.Sdk.COMPILE)
    defaultConfig { defaultConfig() }
    packagingOptions { packagingOptions() }
    compileOptions { compileOptions() }
    sourceSets { androidSourceSets() }
    testOptions { testOptions() }
    lintOptions { lintOptions() }
    buildFeatures()
    variantOptions()
}

fun DetektExtension.detekt() {
    toolVersion = Plugins.Version.DETEKT
    parallel = false
    config = files(Config.Detekt.CONFIG_FILE_PATH)
    buildUponDefaultConfig = false
    disableDefaultRuleSets = false
    debug = false
    ignoreFailures = false
    reports {
        html.enabled = true
        xml.enabled = true
        txt.enabled = true
    }
}

fun DefaultConfig.defaultConfig() {
    minSdkVersion(Android.Sdk.MIN)
    targetSdkVersion(Android.Sdk.TARGET)
}

fun PackagingOptions.packagingOptions() {
    exclude(Android.PackagingOption.Exclude.LICENCE)
    exclude(Android.PackagingOption.Exclude.LICENCE_NOTICE)
    exclude(Android.PackagingOption.Exclude.AL)
    exclude(Android.PackagingOption.Exclude.LGPL)
}

fun CompileOptions.compileOptions() {
    sourceCompatibility = Java.version
    targetCompatibility = Java.version
}

fun NamedDomainObjectContainer<AndroidSourceSetLegacy>.androidSourceSets() {
    mainAndroidSourceSets()
    testAndroidSourceSets()
    androidTestAndroidSourceSets()
}

fun TestOptions.testOptions() {
    animationsDisabled = true
    unitTests.isIncludeAndroidResources = true
    unitTests.all { test: Test -> test.testLogging() }
}

fun TestOptions.testOptionsJUnit5() {
    junitPlatform { filters { includeEngines(Tests.Engine.JUnit.VINTAGE, Tests.Engine.Spek.SPEK) } }
}

@Suppress("SpreadOperator")
fun LintOptions.lintOptions() {
    isAbortOnError = true
    isCheckAllWarnings = true
    isIgnoreWarnings = false
    isCheckReleaseBuilds = true
    isWarningsAsErrors = true
    lintConfig = file(Config.Lint.CONFIG_FILE_PATH)
    htmlReport = true
    xmlReport = true
    disable(*Config.Lint.disabledIssues)
}

fun BaseExtension.buildFeatures() {
    buildFeatures.viewBinding = true
}

fun LibraryExtension.variantOptions() {
    ignoredVariants {
        onVariants.withName(it) {
            enabled = false
            androidTest {
                enabled = false
            }
            unitTest {
                enabled = false
            }
        }
    }
}

fun AppExtension.variantOptions() {
    ignoredVariants {
        variantFilter {
            if (name == it) ignore = true
        }
    }
}

fun ignoredVariants(variantOptions: (String) -> Unit) {
    val ignoredVariants = findLocalProperty(LocalProperties.Gradle.IGNORED_VARIANTS)?.split(Utils.COMMA)
    ignoredVariants?.forEach {
        logVariant(it)
        variantOptions(it)
    }
}

fun JacocoReport.jacoco(isKotlinModule: Boolean) {
    group = Config.Jacoco.GROUP
    description = Config.Jacoco.DESCRIPTION
    reports {
        html.isEnabled = true
        html.destination = project.file(Config.Jacoco.REPORT_HTML_DIRECTORY_PATH)
        xml.isEnabled = true
        xml.destination = project.file(Config.Jacoco.REPORT_XML_FILE_PATH)
        csv.isEnabled = true
        csv.destination = project.file(Config.Jacoco.REPORT_CSV_FILE_PATH)
    }
    sourceDirectories.setFrom(project.files(Sources.Main.KOTLIN))
    additionalSourceDirs.setFrom(project.files(Sources.Main.KOTLIN))
    classDirectories.setFrom(
        project.files(
            project.fileTree(
                Config.Jacoco.ClassDirectories.dirPair(isKotlinModule),
                Config.Jacoco.ClassDirectories.excludesPair(isKotlinModule)
            )
        )
    )
    executionData(
        project.files(
            project.fileTree(
                Config.Jacoco.ExecutionData.dirPair(),
                Config.Jacoco.ExecutionData.includePair(isKotlinModule)
            )
        )
    )
}

fun Project.jacocoRobolectric() {
    tasks.withType(Test::class.java) {
        extensions.getByType(JacocoTaskExtension::class.java).apply {
            isIncludeNoLocationClasses = true
            excludes = Config.Jacoco.Robolectric.excludes
        }
    }
}

fun CoverageExtension.coverage() {
    report = Config.Coverage.REPORT_FILE_PATH
    config = Config.Coverage.CONFIG_FILE_PATH
}

/* TASKS EXTENSION FUNCTIONS */

fun KotlinJvmOptions.kotlinOptions() {
    jvmTarget = Java.version.toString()
    allWarningsAsErrors = true
}

fun DependencyUpdatesTask.versionsOptions() {
    rejectVersionIf { isNonStable(candidate.version) }
    gradleReleaseChannel = Config.Versions.GRADLE_RELEASE_CHANNEL
    checkConstraints = false
    checkForGradleUpdate = true
    outputFormatter = Config.Versions.OUTPUT_FORMATTER
    outputDir = Config.Versions.OUTPUT_DIR
    reportfileName = Config.Versions.REPORT_FILE_NAME
}

fun isNonStable(version: String): Boolean {
    val regex = Config.Versions.REGEX.toRegex()
    val stableKeyword = Config.Versions.stableKeyword.any { version.toUpperCase().contains(it) }
    val nonStableKeyword = Config.Versions.nonStableKeyword.any { version.toUpperCase().contains(it) }
    val isStable = !nonStableKeyword && (stableKeyword || regex.matches(version))
    return isStable.not()
}

/* SOURCE SET EXTENSION FUNCTIONS - KOTLIN */

fun SourceSetContainer.mainSourceSets() {
    named(Sources.MAIN) {
        java.setSrcDirs(
            arrayListOf(
                Sources.Main.KOTLIN
            )
        )
        resources.setSrcDirs(
            arrayListOf(
                Sources.Main.RESOURCES
            )
        )
    }
}

fun SourceSetContainer.testSourceSets() {
    named(Sources.TEST) {
        java.setSrcDirs(
            arrayListOf(
                Sources.Test.KOTLIN,
                Sources.Spek.KOTLIN,
                Sources.Integration.KOTLIN,
                Sources.Robolectric.KOTLIN
            )
        )
        resources.setSrcDirs(
            arrayListOf(
                Sources.Test.RESOURCES,
                Sources.Spek.RESOURCES,
                Sources.Integration.RESOURCES,
                Sources.Robolectric.RESOURCES
            )
        )
    }
}

/* SOURCE SET EXTENSION FUNCTIONS - ANDROID */

fun NamedDomainObjectContainer<AndroidSourceSetLegacy>.mainAndroidSourceSets() {
    named(Sources.MAIN) {
        java.setSrcDirs(
            arrayListOf(
                Sources.Main.KOTLIN
            )
        )
        resources.setSrcDirs(
            arrayListOf(
                Sources.Main.RESOURCES
            )
        )
    }
}

fun NamedDomainObjectContainer<AndroidSourceSetLegacy>.testAndroidSourceSets() {
    named(Sources.TEST) {
        java.setSrcDirs(
            arrayListOf(
                Sources.Test.KOTLIN,
                Sources.Spek.KOTLIN,
                Sources.Integration.KOTLIN,
                Sources.Robolectric.KOTLIN
            )
        )
        resources.setSrcDirs(
            arrayListOf(
                Sources.Test.RESOURCES,
                Sources.Spek.RESOURCES,
                Sources.Integration.RESOURCES,
                Sources.Robolectric.RESOURCES
            )
        )
    }
}

fun NamedDomainObjectContainer<AndroidSourceSetLegacy>.androidTestAndroidSourceSets() {
    named(Sources.ANDROID_TEST) {
        java.setSrcDirs(
            arrayListOf(
                Sources.Android.Test.KOTLIN
            )
        )
        resources.setSrcDirs(
            arrayListOf(
                Sources.Android.Test.RESOURCES
            )
        )
    }
}
