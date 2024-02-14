object Dependencies {

    object Ui {
        private const val materialComponentsVersion = "1.9.0"
        private const val exoPlayerVersion = "2.16.1"
        private const val groupieVersion = "2.10.1"
        private const val coilVersion = "2.5.0"

        const val groupie = "com.github.lisawray.groupie:groupie:$groupieVersion"
        const val groupieViewBinding = "com.github.lisawray.groupie:groupie-viewbinding:$groupieVersion"

        const val exoPlayer = "com.google.android.exoplayer:exoplayer:$exoPlayerVersion"
        const val materialComponents = "com.google.android.material:material:$materialComponentsVersion"
        const val coil = "io.coil-kt:coil:$coilVersion"
        const val coilGif = "io.coil-kt:coil-gif:$coilVersion"
    }

    object AndroidX {
        private const val lifecycleComponentsVersion = "2.2.0"
        private const val lifecycleDefaultComponentsVersion = "2.6.2"
        private const val viewpagerVersion = "1.0.0"
        private const val constraintLayoutVersion = "2.1.4"
        private const val swipeRefreshLayoutVersion = "1.1.0"
        private const val customTabsVersion = "1.7.0"
        private const val annotationVersion = "1.7.0"
        private const val splashscreenVersion = "1.0.1"
        private const val datastoreVersion = "1.0.0"
        private const val biometricVersion = "1.2.0-alpha05"

        const val lifecycleComponents = "androidx.lifecycle:lifecycle-extensions:$lifecycleComponentsVersion"
        const val lifecycleDefaultComponents = "androidx.lifecycle:lifecycle-common-java8:$lifecycleDefaultComponentsVersion"
        const val viewpager = "androidx.viewpager2:viewpager2:$viewpagerVersion"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:$swipeRefreshLayoutVersion"
        const val customTabs = "androidx.browser:browser:$customTabsVersion"
        const val annotation = "androidx.annotation:annotation:$annotationVersion"
        const val splashscreen = "androidx.core:core-splashscreen:$splashscreenVersion"
        const val datastorePreferences = "androidx.datastore:datastore-preferences:$datastoreVersion"
        const val datastoreProto = "androidx.datastore:datastore:$datastoreVersion"
        const val biometric = "androidx.biometric:biometric:$biometricVersion"
    }

    object WorkManager {
        private const val workVersion = "2.9.0"

        const val runtime = "androidx.work:work-runtime:$workVersion"
        const val ktx = "androidx.work:work-runtime-ktx:$workVersion"
    }

    object Room {
        private const val roomVersion = "2.6.1"

        const val runtime = "androidx.room:room-runtime:$roomVersion"
        const val compiler = "androidx.room:room-compiler:$roomVersion"
        const val ktx = "androidx.room:room-ktx:$roomVersion"
    }

    object Firebase {
        private const val firebaseCloudMessagingVersion = "23.0.0"
        private const val firebaseCrashlyticsVersion = "18.2.8"
        private const val firebaseAnalyticsVersion = "20.1.0"
        private const val firebaseBomVersion = "29.1.0"

        const val bom = "com.google.firebase:firebase-bom:$firebaseBomVersion"
        const val cloudMessaging = "com.google.firebase:firebase-messaging:$firebaseCloudMessagingVersion"
        const val crashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx:$firebaseCrashlyticsVersion"
        const val analyticsKtx = "com.google.firebase:firebase-analytics-ktx:$firebaseAnalyticsVersion"
    }

    object Ktx {
        private const val ktxVersion = "1.12.0"
        private const val ktxActivityVersion = "1.8.2"
        private const val ktxFragmentVersion = "1.6.2"

        const val activity = "androidx.activity:activity-ktx:$ktxActivityVersion"
        const val core = "androidx.core:core-ktx:$ktxVersion"
        const val fragment = "androidx.fragment:fragment-ktx:$ktxFragmentVersion"
    }

    object Navigation {
        private const val navigationVersion = "2.7.6"

        const val fragment = "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
        const val ui = "androidx.navigation:navigation-ui-ktx:$navigationVersion"
    }

    object Moshi {
        private const val moshiVersion = "1.15.0"

        const val moshi = "com.squareup.moshi:moshi:$moshiVersion"
        const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:$moshiVersion"
        const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion"
        const val moshiAdapters = "com.squareup.moshi:moshi-adapters:$moshiVersion"
    }

    object Retrofit {
        private const val retrofitVersion = "2.9.0"

        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
        const val converterMoshi = "com.squareup.retrofit2:converter-moshi:$retrofitVersion"
    }

    object DI {
        private const val daggerVersion = Config.Versions.daggerVersion
        private const val hiltVersion = "1.2.0-rc01"
        private const val injectVersion = "1"

        const val dagger = "com.google.dagger:hilt-android:$daggerVersion"
        const val androidCompiler = "com.google.dagger:hilt-android-compiler:$daggerVersion"
        const val compiler = "androidx.hilt:hilt-compiler:$hiltVersion"
        const val inject = "javax.inject:javax.inject:$injectVersion"
        const val hiltWorkManager = "androidx.hilt:hilt-work:$hiltVersion"
    }

    object Test {
        private const val jUnitVersion = "4.13.2"
        private const val androidJUnitVersion = "1.1.5"

        const val junit = "junit:junit:$jUnitVersion"
        const val androidJunit = "androidx.test.ext:junit:$androidJUnitVersion"
    }

    object Analytics {
        private const val appMetricaVersion = "4.1.1"
        const val appMetrica = "com.yandex.android:mobmetricalib:$appMetricaVersion"
    }

    object Other {
        private const val localDateVersion = "1.4.6"
        private const val insetterVersion = "0.6.1"
        private const val coroutinesVersion = "1.7.3"
        private const val lightSpannerVersion = "1.0.1"
        private const val zip4jVersion = "2.11.5"
        private const val jsoupVersion = "1.17.2"
        private const val jsonSerializationVersion = "1.6.2"
        private const val dropboxVersion = "5.4.5"

        const val localDate = "com.jakewharton.threetenabp:threetenabp:$localDateVersion"
        const val insetter = "dev.chrisbanes.insetter:insetter:$insetterVersion"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
        const val lightSpanner = "com.github.zerobranch:light-spanner:$lightSpannerVersion"
        const val zip4j = "net.lingala.zip4j:zip4j:$zip4jVersion"
        const val jsoup = "org.jsoup:jsoup:$jsoupVersion"
        const val jsonSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:$jsonSerializationVersion"
    }

    object Debugging {
        private const val okhttpVersion = "4.12.0"
        private const val timberVersion = "5.0.1"
        private const val remoteDebuggerVersion = "1.1.2"
        private const val leakcanaryVersion = "2.8.1"

        const val okhttp = "com.squareup.okhttp3:logging-interceptor:$okhttpVersion"
        const val timber = "com.jakewharton.timber:timber:$timberVersion"
        const val remoteDebugger = "com.github.zerobranch.android-remote-debugger:debugger:$remoteDebuggerVersion"
        const val leakcanary = "com.squareup.leakcanary:leakcanary-android:$leakcanaryVersion"
    }
}