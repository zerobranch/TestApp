package dsl.dep

import Module
import dsl.PresentationProjectDslMarker
import dsl.internalProjectImplementation
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@PresentationProjectDslMarker
class PresentationProjectDsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val all: Unit
        get() {
            Module.Presentation.all.forEach { screenDep ->
                delegate.internalProjectImplementation(screenDep)
            }
        }
}