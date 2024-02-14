package com.github.zerobranch.beebox.commons_android.utils.delegates

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T : Any> args(): ReadWriteProperty<Fragment, T> = FragmentArgumentDelegate()

fun <T : Any> nullableArgs(): ReadWriteProperty<Fragment, T?> = FragmentNullableArgumentDelegate()

fun <T : Any> args(key: String): ReadWriteProperty<Fragment, T> =
    FragmentArgumentBundleDelegate(key)

fun <T : Any> nullableArgs(key: String): ReadWriteProperty<Fragment, T?> =
    FragmentNullableArgumentBundleDelegate(key)

/**
 * Eases the Fragment.newInstance ceremony by marking the fragment's args with this delegate
 * Just write the property in newInstance and read it like any other property after the fragment has been created
 */
class FragmentArgumentDelegate<T : Any> : ReadWriteProperty<Fragment, T> {
    var value: T? = null

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (value == null) {
            val args = thisRef.arguments
                ?: throw IllegalStateException("Cannot read property ${property.name} if no arguments have been set")
            @Suppress("UNCHECKED_CAST")
            value = args.get(property.name) as T
        }
        return value ?: throw IllegalStateException("Property ${property.name} could not be read")
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        (thisRef.arguments ?: Bundle()).also {
            it.putAll(bundleOf(property.name to value))
            thisRef.arguments = it
        }
    }
}

class FragmentNullableArgumentDelegate<T : Any?> : ReadWriteProperty<Fragment, T?> {
    var value: T? = null

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T? {
        if (value == null) {
            val args = thisRef.arguments ?: return null

            @Suppress("UNCHECKED_CAST")
            value = args.get(property.name) as T?
        }
        return value
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T?) {
        (thisRef.arguments ?: Bundle()).also {
            it.putAll(bundleOf(property.name to value))
            thisRef.arguments = it
        }
    }
}

class FragmentArgumentBundleDelegate<T : Any>(
    private val key: String
) : ReadWriteProperty<Fragment, T> {
    var value: T? = null

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (value == null) {
            val args = thisRef.arguments
                ?: throw IllegalStateException("Cannot read property ${property.name} if no arguments have been set")
            @Suppress("UNCHECKED_CAST")
            value = args.get(key) as T
        }
        return value ?: throw IllegalStateException("Property ${property.name} could not be read")
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        (thisRef.arguments ?: Bundle()).also {
            it.putAll(bundleOf(key to value))
            thisRef.arguments = it
        }
    }
}

class FragmentNullableArgumentBundleDelegate<T : Any?>(
    private val key: String
) : ReadWriteProperty<Fragment, T?> {
    var value: T? = null

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T? {
        if (value == null) {
            val args = thisRef.arguments ?: return null

            @Suppress("UNCHECKED_CAST")
            value = args.get(key) as T?
        }
        return value
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T?) {
        (thisRef.arguments ?: Bundle()).also {
            it.putAll(bundleOf(key to value))
            thisRef.arguments = it
        }
    }
}