import dsl.androidX
import dsl.ktx
import dsl.other
import dsl.projectModules
import dsl.ui

plugins {
    id(Config.PluginIds.androidLibrary)
    id(Config.PluginIds.kotlin)
    id(Config.PluginIds.ksp)
}

android {
    compileSdk = Config.Versions.androidCompileSdk
    namespace = Config.namespace(Module.Commons.app)

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
    projectModules { domain; logging; commons { android; view } }
    androidX { lifecycleComponents; lifecycleDefaultComponents; viewpager; constraintLayout }
    ui { materialComponents; groupie; coil }
    ktx { all }
    other { coroutines; insetter; navigation }
}