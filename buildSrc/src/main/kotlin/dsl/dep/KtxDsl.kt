package dsl.dep

import Dependencies
import dsl.KtxDslMarker
import dsl.implementation
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@KtxDslMarker
class KtxDsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val fragment: Unit
        get() { delegate.implementation(Dependencies.Ktx.fragment) }

    val core: Unit
        get() { delegate.implementation(Dependencies.Ktx.core) }

    val activity: Unit
        get() { delegate.implementation(Dependencies.Ktx.activity) }

    val all: Unit
        get() {
            fragment
            core
            activity
        }
}