import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import dsl.androidX
import dsl.debugging
import dsl.io
import dsl.ktx
import dsl.other
import dsl.projectModules
import dsl.test
import dsl.ui
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id(Config.PluginIds.android)
    id(Config.PluginIds.kotlin)
    id(Config.PluginIds.ksp)
    id(Config.PluginIds.hilt)
}

android {
    compileSdk = Config.Versions.androidCompileSdk
    namespace = Config.namespace()

    defaultConfig {
        applicationId = Config.applicationId

        vectorDrawables.useSupportLibrary = true

        minSdk = Config.Versions.androidMinSdk
        targetSdk = Config.Versions.androidTargetSdk

        versionCode = Config.Versions.androidVersionCode
        versionName = Config.Versions.androidVersionName

        resourceConfigurations.addAll(mutableSetOf("en", "ru"))

        testInstrumentationRunner = Config.testRunner

        setProperty("archivesBaseName", "beebox-${versionName}-${versionCode}")
        buildConfigField("String", "BASE_URL", "\"https://wooordhunt.ru/\"")
        buildConfigField("String", "BEEBOX_LOG_FILE_NAME", "\"beebox.log\"")
        buildConfigField("String", "DATA_BASE_NAME", "\"beebox.db\"")
    }

    compileOptions {
        sourceCompatibility = Config.Versions.javaVersion
        targetCompatibility = Config.Versions.javaVersion
    }

    kotlinOptions {
        jvmTarget = Config.Versions.javaVersion.toString()
    }

    signingConfigs {
//        create("release") {
//            val signPropertiesFile = rootProject.file(
//                rootProject.projectDir.absolutePath + "/keystore/release_keystore.properties"
//            )
//            val props = Properties()
//            props.load(signPropertiesFile.inputStream())
//
//            storeFile = file(props.getProperty("keystore"))
//            storePassword = props.getProperty("storePassword")
//            keyAlias = props.getProperty("keyAlias")
//            keyPassword = props.getProperty("keyPassword")
//        }
        create("dev") {
            val signPropertiesFile = rootProject.file(
                rootProject.projectDir.absolutePath + "/keystore/debug_keystore.properties"
            )
            val props = Properties()
            props.load(signPropertiesFile.inputStream())

            storeFile = file(props.getProperty("keystore"))
            storePassword = props.getProperty("storePassword")
            keyAlias = props.getProperty("keyAlias")
            keyPassword = props.getProperty("keyPassword")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("dev")
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }
        release {
//            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile(Config.ProGuard.androidOptimize),
                Config.ProGuard.rules
            )
        }
    }

    applicationVariants.all {
        outputs.all {
            val project = "Beebox"
            val buildType = buildType.name

            (this as BaseVariantOutputImpl).outputFileName =
                "${project}_${buildType}_${versionName}_${versionCode}.apk"
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    projectModules {
        data
        domain
        logging
        services

        presentation { all }
        commons { all }
    }

    androidX {
        lifecycleComponents
        lifecycleDefaultComponents
        viewpager
        constraintLayout
        swipeRefreshLayout
        splashscreen
    }

    ui { groupie; materialComponents }
    ktx { all }
    io { retrofit; moshi; datastore; room { runtime } }
    other { navigation; dagger; hiltWorkManager; coroutines; insetter; localDate; }
    debugging { okhttp; remoteDebugger; timber }
    test { junit; androidJunit }
}