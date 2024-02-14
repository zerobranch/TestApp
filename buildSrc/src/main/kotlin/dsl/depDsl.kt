package dsl

import dsl.dep.AndroidXDsl
import dsl.dep.DebuggingDsl
import dsl.dep.IODsl
import dsl.dep.KtxDsl
import dsl.dep.OtherDsl
import dsl.dep.ProjectDsl
import dsl.dep.TestDsl
import dsl.dep.UiDsl
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

fun DependencyHandlerDelegate.projectModules(build: ProjectDsl.() -> Unit) {
    ProjectDsl(this).apply(build)
}

fun DependencyHandlerDelegate.androidX(build: AndroidXDsl.() -> Unit) {
    AndroidXDsl(this).apply(build)
}

fun DependencyHandlerDelegate.ui(build: UiDsl.() -> Unit) {
    UiDsl(this).apply(build)
}

fun DependencyHandlerDelegate.debugging(build: DebuggingDsl.() -> Unit) {
    DebuggingDsl(this).apply(build)
}

fun DependencyHandlerDelegate.io(build: IODsl.() -> Unit) {
    IODsl(this).apply(build)
}

fun DependencyHandlerDelegate.ktx(build: KtxDsl.() -> Unit) {
    KtxDsl(this).apply(build)
}

fun DependencyHandlerDelegate.other(build: OtherDsl.() -> Unit) {
    OtherDsl(this).apply(build)
}

fun DependencyHandlerDelegate.test(build: TestDsl.() -> Unit) {
    TestDsl(this).apply(build)
}

fun DependencyHandlerDelegate.presentationBaseDep() {
    projectModules { domain; logging; commons { all } }
    androidX {
        lifecycleComponents
        lifecycleDefaultComponents
        viewpager
        constraintLayout
    }
    ui { materialComponents; coil; groupie }
    ktx { all }
    other { dagger; coroutines; insetter; localDate }
}
