package dsl.dep

import Dependencies
import dsl.IODslMarker
import dsl.RoomDslMarker
import dsl.implementation
import dsl.ksp
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

@IODslMarker
class IODsl internal constructor(
    private val delegate: DependencyHandlerDelegate
) {
    val room: Unit
        get() {
            delegate.implementation(Dependencies.Room.runtime)
            delegate.implementation(Dependencies.Room.ktx)
            delegate.ksp(Dependencies.Room.compiler)
        }

    val moshi: Unit
        get() {
            delegate.implementation(Dependencies.Moshi.moshi)
            delegate.implementation(Dependencies.Moshi.moshiKotlin)
            delegate.implementation(Dependencies.Moshi.moshiAdapters)
            delegate.ksp(Dependencies.Moshi.moshiCodegen)
        }

    val retrofit: Unit
        get() {
            delegate.implementation(Dependencies.Retrofit.retrofit)
            delegate.implementation(Dependencies.Retrofit.converterMoshi)
        }

    val datastore: Unit
        get() {
            delegate.implementation(Dependencies.AndroidX.datastoreProto)
            delegate.implementation(Dependencies.AndroidX.datastorePreferences)
        }

    fun DependencyHandlerDelegate.room(build: RoomDsl.() -> Unit) {
        RoomDsl(this).apply(build)
    }

    @RoomDslMarker
    class RoomDsl internal constructor(
        private val delegate: DependencyHandlerDelegate
    ) {
        val runtime: Unit
            get() { delegate.implementation(Dependencies.Room.runtime) }

        val ktx: Unit
            get() { delegate.implementation(Dependencies.Room.ktx) }

        val compiler: Unit
            get() { delegate.ksp(Dependencies.Room.compiler) }
    }
}