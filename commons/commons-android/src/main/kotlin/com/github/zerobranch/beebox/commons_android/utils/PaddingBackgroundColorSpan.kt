package com.github.zerobranch.beebox.commons_android.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.style.LineBackgroundSpan
import kotlin.math.roundToInt

class PaddingBackgroundColorSpan(
    private val backgroundColor: Int,
    private val padding: Int
) : LineBackgroundSpan {
    private val bgRect: Rect = Rect()

    override fun drawBackground(
        c: Canvas,
        p: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lnum: Int
    ) {
        val textWidth = p.measureText(text, start, end).roundToInt()
        val paintColor = p.color

        bgRect.set(
            left - padding,
            top - (if (lnum == 0) padding / 2 else -(padding / 2)),
            left + textWidth + padding,
            bottom + padding / 2
        )

        p.color = backgroundColor
        c.drawRect(bgRect, p)
        p.color = paintColor
    }
}