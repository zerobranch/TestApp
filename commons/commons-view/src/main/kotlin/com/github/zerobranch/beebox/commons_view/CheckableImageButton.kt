package com.github.zerobranch.beebox.commons_view

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageButton

class CheckableImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr), Checkable {
    private companion object {
        val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }

    var onCheckedChangeListener: ((CheckableImageButton, Boolean) -> Unit)? = null
    var isLazyChecked = true

    private var isChecked = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckableImageButton)
        isLazyChecked = typedArray.getBoolean(
            R.styleable.CheckableImageButton_isLazyChecked,
            true
        )
        typedArray.recycle()

        isClickable = true
    }

    override fun isChecked(): Boolean = isChecked

    override fun setChecked(checked: Boolean) {
        if (checked == this.isChecked) return

        this.isChecked = checked
        refreshDrawableState()
        onCheckedChangeListener?.invoke(this, checked)
    }

    override fun toggle() = setChecked(!isChecked)

    override fun performClick(): Boolean {
        if (!isLazyChecked) {
            toggle()
        }
        return super.performClick()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }
}