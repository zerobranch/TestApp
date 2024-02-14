package com.github.zerobranch.beebox.commons_java.ext

fun <T> MutableList<T>.replace(elements: Collection<T>) {
    clear()
    addAll(elements)
}

fun List<*>.deepEquals(other: List<*>) =
    this.size == other.size && this.mapIndexed { index, element -> element == other[index] }.all { it }


fun <T> MutableList<T>.addNonNull(item: T?) {
    if (item != null) {
        add(item)
    }
}

fun <T> List<T>.indexOfOrNull(item: T): Int? = indexOf(item).takeIf { it != -1 }

inline fun <T> Iterable<T>.containsBy(predicate: (T) -> Boolean): Boolean {
    for (element in this) if (predicate(element)) return true
    return false
}