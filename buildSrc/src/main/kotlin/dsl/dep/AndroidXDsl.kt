package dsl.dep

import Dependencies
import dsl.AndroidXDslMarker
import dsl.implementation
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@AndroidXDslMarker
class AndroidXDsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val lifecycleComponents: Unit
        get() { delegate.implementation(Dependencies.AndroidX.lifecycleComponents) }

    val lifecycleDefaultComponents: Unit
        get() { delegate.implementation(Dependencies.AndroidX.lifecycleDefaultComponents) }

    val viewpager: Unit
        get() { delegate.implementation(Dependencies.AndroidX.viewpager) }

    val constraintLayout: Unit
        get() { delegate.implementation(Dependencies.AndroidX.constraintLayout) }

    val swipeRefreshLayout: Unit
        get() { delegate.implementation(Dependencies.AndroidX.swipeRefreshLayout) }

    val customTabs: Unit
        get() { delegate.implementation(Dependencies.AndroidX.customTabs) }

    val annotation: Unit
        get() { delegate.implementation(Dependencies.AndroidX.annotation) }

    val splashscreen: Unit
        get() { delegate.implementation(Dependencies.AndroidX.splashscreen) }

    val biometric: Unit
        get() { delegate.implementation(Dependencies.AndroidX.biometric) }
}