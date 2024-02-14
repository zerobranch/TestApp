package com.github.zerobranch.beebox.commons_android.utils.ext

fun <K, V> MutableMap<in K, in V>.putPair(pair: Pair<K, V>) {
    put(pair.first, pair.second)
}

fun <T> Iterable<T>.toArrayList(): ArrayList<T> = ArrayList<T>().also { it.addAll(this) }