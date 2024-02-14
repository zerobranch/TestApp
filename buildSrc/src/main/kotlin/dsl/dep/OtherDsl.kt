package dsl.dep

import Dependencies
import dsl.OtherDslMarker
import dsl.implementation
import dsl.ksp
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@OtherDslMarker
class OtherDsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val navigation: Unit
        get() {
            delegate.implementation(Dependencies.Navigation.fragment)
            delegate.implementation(Dependencies.Navigation.ui)
        }

    val dagger: Unit
        get() {
            delegate.implementation(Dependencies.DI.dagger)
            delegate.ksp(Dependencies.DI.androidCompiler)
            delegate.ksp(Dependencies.DI.compiler)
        }

    val inject: Unit
        get() { delegate.implementation(Dependencies.DI.inject) }

    val hiltWorkManager: Unit
        get() { delegate.implementation(Dependencies.DI.hiltWorkManager) }

    val workManager: Unit
        get() {
            delegate.implementation(Dependencies.WorkManager.runtime)
            delegate.implementation(Dependencies.WorkManager.ktx)
        }

    val localDate: Unit
        get() { delegate.implementation(Dependencies.Other.localDate) }

    val jsoup: Unit
        get() { delegate.implementation(Dependencies.Other.jsoup) }

    val insetter: Unit
        get() { delegate.implementation(Dependencies.Other.insetter) }

    val coroutines: Unit
        get() { delegate.implementation(Dependencies.Other.coroutines) }

    val lightSpanner: Unit
        get() { delegate.implementation(Dependencies.Other.lightSpanner) }

    val zip4j: Unit
        get() { delegate.implementation(Dependencies.Other.zip4j) }

    val jsonSerialization: Unit
        get() { delegate.implementation(Dependencies.Other.jsonSerialization) }
}