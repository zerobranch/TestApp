@file:Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = java.net.URI.create("https://jitpack.io"))
    }
}
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.buildFileName = "build.gradle.kts"
rootProject.name = "Beebox"

include(":app")
include(":data")
include(":logging")
include(":domain")
include(":services")

include(":presentation:home")
include(":presentation:settings")
include(":presentation:add-word-dialog")
include(":presentation:search")
include(":presentation:lists")
include(":presentation:simple-dialog")
include(":presentation:choose-category-dialog")
include(":presentation:choose-type-dialog")
include(":presentation:category-dialog")
include(":presentation:word-type-dialog")
include(":presentation:local-search")
include(":presentation:type-word-list-dialog")
include(":presentation:category-word-list-dialog")
include(":presentation:word-groups-dialog")
include(":presentation:training-settings-dialog")
include(":presentation:training")
include(":presentation:recent-trainings-dialog")
include(":presentation:backup")
include(":presentation:auth")

include(":commons:commons-app")
include(":commons:commons-java")
include(":commons:commons-items")
include(":commons:commons-android")
include(":commons:commons-view")
