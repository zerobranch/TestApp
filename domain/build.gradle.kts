import dsl.other
import dsl.projectModules

plugins {
    id(Config.PluginIds.javaLibrary)
    id(Config.PluginIds.kotlinJvm)
    id(Config.PluginIds.serialization)
}

java {
    sourceCompatibility = Config.Versions.javaVersion
    targetCompatibility = Config.Versions.javaVersion
}

dependencies {
    projectModules { commons { java } }
    other { coroutines; localDate; inject; jsonSerialization }
}