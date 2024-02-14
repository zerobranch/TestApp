package com.github.zerobranch.beebox.commons_android.utils.ext

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

inline fun <reified T> GroupAdapter<GroupieViewHolder>.requireItem(): T {
    for (position in 0 until groupCount) {
        val item = getItem(position)
        if (item is T) {
            return item
        }
    }

    throw IllegalStateException("item ${T::class.java.canonicalName} not found")
}

inline fun <reified T> GroupAdapter<GroupieViewHolder>.each(action: (T) -> Unit) {
    runCatching {
        for (position in 0 until groupCount) {
            val item = getItem(position)
            if (item is T) {
                action.invoke(item)
            }
        }
    }
}

inline fun <reified T> GroupAdapter<GroupieViewHolder>.item(position: Int): T? =
    runCatching { getItem(position) as T }.getOrNull()

inline fun <reified T> GroupAdapter<GroupieViewHolder>.removeItem() {
    try {
        for (position in 0 until groupCount) {
            val item = getItem(position)
            if (item is T) {
                remove(item)
            }
        }
    } catch (ignore: IndexOutOfBoundsException) {
        // это нужно так как groupCount меняется асинхронно, в итоге getItem(position) может выйти за массив
        // а доступа к итератору тут нет
    }
}
