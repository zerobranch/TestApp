package com.github.zerobranch.beebox.commons_android.utils.decorations

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.github.zerobranch.beebox.commons_android.R
import com.github.zerobranch.beebox.commons_android.utils.ext.dp
import com.github.zerobranch.beebox.commons_android.utils.ext.drawable
import kotlin.math.roundToInt

class DividerDecoration(@Px private val padding: Int = 12.dp) : RecyclerView.ItemDecoration() {
    private val mBounds = Rect()
    private val drawableId = R.drawable.utils_android_bg_divider

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null) return

        if ((parent.layoutManager as LinearLayoutManager).orientation == VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val dividerDrawable = parent.drawable(drawableId) ?: return
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + child.translationY.roundToInt()
            val top = bottom - dividerDrawable.intrinsicHeight
            dividerDrawable.setBounds(left + padding, top, right - padding, bottom)
            dividerDrawable.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val dividerDrawable = parent.drawable(drawableId) ?: return
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.layoutManager?.getDecoratedBoundsWithMargins(child, mBounds)
            val right = mBounds.right + child.translationX.roundToInt()
            val left = right - dividerDrawable.intrinsicWidth
            dividerDrawable.setBounds(left, top + padding, right, bottom - padding)
            dividerDrawable.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val dividerDrawable = parent.drawable(drawableId)
        if (dividerDrawable == null) {
            outRect[0, 0, 0] = 0
            return
        }
        if ((parent.layoutManager as LinearLayoutManager).orientation == VERTICAL) {
            outRect[0, 0, 0] = dividerDrawable.intrinsicHeight
        } else {
            outRect[0, 0, dividerDrawable.intrinsicWidth] = 0
        }
    }
}