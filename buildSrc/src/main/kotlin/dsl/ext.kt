package dsl

import Config.dep
import Module
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

internal fun DependencyHandlerDelegate.internalProjectImplementation(projectModule: Module): Dependency? =
    implementation(project(dep(projectModule)))

internal fun DependencyHandler.ksp(dependencyNotation: Any): Dependency? =
    add("ksp", dependencyNotation)

internal fun DependencyHandler.`implementation`(dependencyNotation: Any): Dependency? =
    add("implementation", dependencyNotation)

internal fun DependencyHandler.project(
    path: String,
    configuration: String? = null
): ProjectDependency = uncheckedCast(
    project(
        if (configuration != null) mapOf("path" to path, "configuration" to configuration)
        else mapOf("path" to path)
    )
)

internal fun DependencyHandler.`testImplementation`(dependencyNotation: Any): Dependency? =
    add("testImplementation", dependencyNotation)

internal fun DependencyHandler.`androidTestImplementation`(dependencyNotation: Any): Dependency? =
    add("androidTestImplementation", dependencyNotation)

internal inline fun <T> uncheckedCast(obj: Any?): T = obj as T
