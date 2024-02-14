package com.github.zerobranch.beebox.commons_view.notification

enum class VisibilityState(val value: Int) {
    UNDEFINED(0),
    SLIDED_LEFT(1),
    SLIDED_RIGHT(2),
    OPENED(3);

    fun isClosed() = this == SLIDED_LEFT || this == SLIDED_RIGHT
}