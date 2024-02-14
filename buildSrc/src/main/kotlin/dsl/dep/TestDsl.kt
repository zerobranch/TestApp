package dsl.dep

import Dependencies
import dsl.TestDslMarker
import dsl.androidTestImplementation
import dsl.testImplementation
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@TestDslMarker
class TestDsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val junit: Unit
        get() { delegate.testImplementation(Dependencies.Test.junit) }

    val androidJunit: Unit
        get() { delegate.androidTestImplementation(Dependencies.Test.androidJunit) }
}