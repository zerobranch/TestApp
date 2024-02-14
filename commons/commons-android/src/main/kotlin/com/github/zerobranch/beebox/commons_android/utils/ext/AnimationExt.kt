package com.github.zerobranch.beebox.commons_android.utils.ext

import android.view.animation.Animation

fun Animation.addAnimationListener(
    onStart: ((Animation?) -> Unit)? = null,
    onEnd: ((Animation?) -> Unit)? = null,
    onRepeat: ((Animation?) -> Unit)? = null,
) {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onStart?.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd?.invoke(animation)
        }

        override fun onAnimationRepeat(animation: Animation?) {
            onRepeat?.invoke(animation)
        }
    })
}