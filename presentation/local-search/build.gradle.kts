import dsl.other
import dsl.presentationBaseDep

plugins {
    id(Config.PluginIds.androidLibrary)
    id(Config.PluginIds.kotlin)
    id(Config.PluginIds.ksp)
    id(Config.PluginIds.hilt)
}

android {
    compileSdk = Config.Versions.androidCompileSdk
    namespace = Config.namespace(Module.Presentation.localSearch)

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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    presentationBaseDep()
}