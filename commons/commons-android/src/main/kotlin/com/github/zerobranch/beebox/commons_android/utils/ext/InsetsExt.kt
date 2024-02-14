package com.github.zerobranch.beebox.commons_android.utils.ext

import androidx.core.view.WindowInsetsCompat

val WindowInsetsCompat.navigationBarHeight: Int
    get() = getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

val WindowInsetsCompat.statusBarHeight: Int
    get() = getInsets(WindowInsetsCompat.Type.statusBars()).top

val WindowInsetsCompat.isKeyboardOpened: Boolean
    get() = getInsets(WindowInsetsCompat.Type.ime()).bottom > 0

val WindowInsetsCompat.keyboardHeight: Int
    get() = getInsets(WindowInsetsCompat.Type.ime()).bottom
