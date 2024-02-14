package com.github.zerobranch.beebox.commons_java.ext

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

inline fun <reified K : Any, reified V : Any> Moshi.fromJsonMap(json: String): Map<K, V>? {
    return runCatching {
        val type: Type = Types.newParameterizedType(Map::class.java, K::class.java, V::class.java)
        val adapter = adapter<Map<K, V>>(type)
        adapter.fromJson(json)
    }.getOrNull()
}

inline fun <reified T : Any> Moshi.fromJsonList(json: String): List<T>? {
    return runCatching {
        val type: Type = Types.newParameterizedType(List::class.java, T::class.java)
        val jsonAdapter = adapter<List<T>>(type)
        return jsonAdapter.fromJson(json)
    }.getOrNull()
}

inline fun <reified T : Any> Moshi.fromJson(json: String): T? {
    return runCatching {
        val jsonAdapter: JsonAdapter<T> = adapter(T::class.java)
        return jsonAdapter.fromJson(json)
    }.getOrNull()
}

inline fun <reified T> Moshi.toJson(obj: T): String? =
    runCatching { adapter(T::class.java).toJson(obj) }.getOrNull()
