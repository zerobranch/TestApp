package dsl.dep

import Module
import dsl.CommonsProjectDslMarker
import dsl.internalProjectImplementation
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@CommonsProjectDslMarker
class CommonsProjectDsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val java: Unit
        get() { delegate.internalProjectImplementation(Module.Commons.java) }

    val android: Unit
        get() { delegate.internalProjectImplementation(Module.Commons.android) }

    val items: Unit
        get() { delegate.internalProjectImplementation(Module.Commons.items) }

    val app: Unit
        get() { delegate.internalProjectImplementation(Module.Commons.app) }

    val view: Unit
        get() { delegate.internalProjectImplementation(Module.Commons.view) }

    val all: Unit
        get() {
            java
            android
            items
            app
            view
        }
}