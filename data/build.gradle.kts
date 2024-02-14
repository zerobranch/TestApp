import dsl.io
import dsl.other
import dsl.projectModules

plugins {
    id(Config.PluginIds.androidLibrary)
    id(Config.PluginIds.kotlin)
    id(Config.PluginIds.ksp)
    id(Config.PluginIds.serialization)
}

android {
    compileSdk = Config.Versions.androidCompileSdk
    namespace = Config.namespace(Module.Root.data)

    defaultConfig {
        minSdk = Config.Versions.androidMinSdk

        testInstrumentationRunner = Config.testRunner
        consumerProguardFiles(Config.ProGuard.consumerRules)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(Config.ProGuard.androidOptimize),
                Config.ProGuard.rules
            )
        }
    }

    compileOptions {
        sourceCompatibility = Config.Versions.javaVersion
        targetCompatibility = Config.Versions.javaVersion
    }

    kotlinOptions {
        jvmTarget = Config.Versions.javaVersion.toString()
    }
}

dependencies {
    projectModules { domain; services; logging; commons { java } }
    io { room; retrofit; datastore; moshi; }
    other { dagger; inject; coroutines; localDate; jsoup; jsonSerialization; zip4j }
}