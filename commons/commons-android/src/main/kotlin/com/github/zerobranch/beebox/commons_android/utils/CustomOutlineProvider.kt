package com.github.zerobranch.beebox.commons_android.utils

import android.graphics.Outline
import android.graphics.Rect
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.DimenRes
import com.github.zerobranch.beebox.commons_android.utils.ext.toPx

class CustomOutlineProvider(
    private val xShift: Int = 0,
    private val yShift: Int = 0,
    private val leftMargin: Int = 0,
    private val rightMargin: Int = 0,
    private val topMargin: Int = 0,
    private val bottomMargin: Int = 0,
    @DimenRes private val cornerRadius: Int? = null,
) : ViewOutlineProvider() {

    private val rect = Rect()

    override fun getOutline(view: View?, outline: Outline?) {
        view?.background?.copyBounds(rect)
        rect.offset(xShift, yShift)
        rect.set(
            rect.left + leftMargin,
            rect.top + topMargin,
            rect.right - rightMargin,
            rect.bottom - bottomMargin
        )

        val cornerRadiusValue = if (cornerRadius != null) view?.toPx(cornerRadius)?.toFloat() ?: 0f else 0f

        outline?.alpha = 0.99f
        outline?.setRoundRect(rect, cornerRadiusValue)
    }
}