import dsl.io
import dsl.other

plugins {
    id(Config.PluginIds.javaLibrary)
    id(Config.PluginIds.kotlinJvm)
    id(Config.PluginIds.ksp)
}

java {
    sourceCompatibility = Config.Versions.javaVersion
    targetCompatibility = Config.Versions.javaVersion
}

dependencies {
    other { coroutines; localDate; zip4j }
    io { moshi }
}