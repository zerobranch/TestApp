import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

object Config {
    const val testRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val applicationId = "com.github.zerobranch.beebox"

    fun DependencyHandlerDelegate.dep(module: Module): String =
        "${if (module.root != null) ":${module.root}" else ""}:${module.name}"

    fun namespace(module: Module? = null): String =
        (module?.run { "$applicationId.${module.name}" } ?: applicationId).replace("-", "_")

    object Versions {
        private const val major = 1 // 1..∞
        private const val minor = 1 // 0..99
        private const val path = 0 // 0..99

        const val androidMinSdk = 24
        const val androidTargetSdk = 34
        const val androidCompileSdk = 34

        const val androidVersionCode: Int = (major * 100 + minor) * 100 + path
        const val androidVersionName = "$major.$minor.$path"

        const val daggerVersion = "2.50"
        const val androidGradleVersion = "8.2.2"
        const val kotlinVersion = "1.9.22"
        const val kspVersion = "1.9.22-1.0.16"
        const val benManesVersion = "0.51.0"
        const val serializationVersion = "1.9.22"

        val javaVersion = JavaVersion.VERSION_17
    }

    object PluginIds {
        const val android = "com.android.application"
        const val androidLibrary = "com.android.library"
        const val kotlin = "org.jetbrains.kotlin.android"
        const val hilt = "com.google.dagger.hilt.android"
        const val benManesVersions = "com.github.ben-manes.versions"
        const val serialization = "org.jetbrains.kotlin.plugin.serialization"

        const val ksp = "com.google.devtools.ksp"
        const val kotlinJvm = "org.jetbrains.kotlin.jvm"
        const val javaLibrary = "java-library"
    }

    object ProGuard {
        const val androidOptimize = "proguard-android-optimize.txt"
        const val rules = "proguard-rules.pro"
        const val consumerRules = "consumer-rules.pro"
    }
}