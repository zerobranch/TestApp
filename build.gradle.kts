import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(Config.PluginIds.android) version Config.Versions.androidGradleVersion apply false
    id(Config.PluginIds.androidLibrary) version Config.Versions.androidGradleVersion apply false
    id(Config.PluginIds.kotlin) version Config.Versions.kotlinVersion apply false
    id(Config.PluginIds.ksp) version Config.Versions.kspVersion apply false
    id(Config.PluginIds.hilt) version Config.Versions.daggerVersion apply false
    id(Config.PluginIds.serialization) version Config.Versions.serializationVersion apply false
    id(Config.PluginIds.benManesVersions) version Config.Versions.benManesVersion
}

allprojects {
    tasks.withType(KotlinCompile::class).all {
        kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    // ./gradlew dependencyUpdates
    // /build/dependencyUpdates
    rejectVersionIf {
        listOf("alpha", "beta", "rc", "dev").any { candidate.version.contains(it, ignoreCase = true) }
    }
    checkForGradleUpdate = true
    gradleReleaseChannel = GradleReleaseChannel.RELEASE_CANDIDATE.id
    revision = "integration" // See available revisions
    outputFormatter = "html" // xml, json, html, plain
    outputDir = "build/dependencyUpdates"
    reportfileName = "dependency_update_report"
}