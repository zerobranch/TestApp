package dsl.dep

import Dependencies
import dsl.UiDslMarker
import dsl.implementation
import dsl.ksp
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@UiDslMarker
class UiDsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val groupie: Unit
        get() {
            delegate.implementation(Dependencies.Ui.groupie)
            delegate.implementation(Dependencies.Ui.groupieViewBinding)
        }

    val coil: Unit
        get() {
            delegate.implementation(Dependencies.Ui.coil)
            delegate.implementation(Dependencies.Ui.coilGif)
        }

    val exoPlayer: Unit
        get() { delegate.implementation(Dependencies.Ui.exoPlayer) }

    val materialComponents: Unit
        get() { delegate.implementation(Dependencies.Ui.materialComponents) }

    val all: Unit
        get() {
            groupie
            coil
            exoPlayer
            materialComponents
        }
}