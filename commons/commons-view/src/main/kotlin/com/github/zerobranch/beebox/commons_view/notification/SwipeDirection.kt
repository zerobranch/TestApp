package com.github.zerobranch.beebox.commons_view.notification

enum class SwipeDirection(val value: Int) {
    NONE(0),
    LEFT(1),
    RIGHT(2),
    HORIZONTAL(3);

    companion object {
        fun getByValue(value: Int) = values().firstOrNull { it.value == value } ?: NONE
    }
}