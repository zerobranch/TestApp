package dsl.dep

import Dependencies
import dsl.DebuggingDslMarker
import dsl.implementation
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@DebuggingDslMarker
class DebuggingDsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val okhttp: Unit
        get() { delegate.implementation(Dependencies.Debugging.okhttp) }

    val timber: Unit
        get() { delegate.implementation(Dependencies.Debugging.timber) }

    val remoteDebugger: Unit
        get() { delegate.implementation(Dependencies.Debugging.remoteDebugger) }

    val leakcanary: Unit
        get() { delegate.implementation(Dependencies.Debugging.leakcanary) }
}