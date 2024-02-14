package com.github.zerobranch.beebox.commons_view.notification

enum class ShowingAnimation(val value: Int) {
    NONE(0),
    SLIDE_LEFT(1),
    SLIDE_RIGHT(2),
    SLIDE_BOTTOM(3),
    FADE(4),
    SCALE_FADE(5);

    companion object {
        fun getByValue(value: Int) = values().firstOrNull { it.value == value } ?: NONE
    }
}