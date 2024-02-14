package dsl.dep

import Module
import dsl.ProjectDslMarker
import dsl.internalProjectImplementation
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@ProjectDslMarker
class ProjectDsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val data: Unit
        get() { delegate.internalProjectImplementation(Module.Root.data) }

    val domain: Unit
        get() { delegate.internalProjectImplementation(Module.Root.domain) }

    val logging: Unit
        get() { delegate.internalProjectImplementation(Module.Root.logging) }

    val services: Unit
        get() { delegate.internalProjectImplementation(Module.Root.services) }

    fun presentation(build: PresentationProjectDsl.() -> Unit) {
        PresentationProjectDsl(delegate).apply(build)
    }

    fun commons(build: CommonsProjectDsl.() -> Unit) {
        CommonsProjectDsl(delegate).apply(build)
    }
}