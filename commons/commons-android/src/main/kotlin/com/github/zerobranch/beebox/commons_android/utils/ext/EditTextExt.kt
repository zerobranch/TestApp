package com.github.zerobranch.beebox.commons_android.utils.ext

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText

@SuppressLint("ClickableViewAccessibility")
fun EditText.setOnEndDrawableClickListener(onClick: () -> Unit) {
    setOnTouchListener(View.OnTouchListener { _, event ->
        val drawableEndIndex = 2
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableWidth = compoundDrawables[drawableEndIndex]?.bounds?.width() ?: return@OnTouchListener false
            if (event.rawX >= right - drawableWidth * 1.5 - paddingEnd) {
                onClick.invoke()
                return@OnTouchListener true
            }
        }
        false
    })
}

fun EditText.setOnDoneClickListener(onClick: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onClick.invoke()
        }
        false
    }
}

fun EditText.setTextWithKeepState(text: CharSequence) {
    this.text = null
    append(text)
}