package com.github.zerobranch.beebox.commons_android.utils.decorations

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleableRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.zerobranch.beebox.commons_android.R
import com.github.zerobranch.beebox.commons_android.utils.ext.dp
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import kotlin.math.roundToInt

class DividerItemDecoration @JvmOverloads constructor(
    context: Context,
    defStyleAttr: Int = R.attr.dividerItemStyle,
    orientation: Int = VERTICAL,
    horizontalMargin: Int = 0,
    private var dividerInsetStart: Int = -1,
    private var dividerInsetEnd: Int = -1,
    private val isReverseDraw: Boolean,
    private val itemPredicate: (Group) -> Boolean = { true }
) : RecyclerView.ItemDecoration() {
    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL
    }

    @setparam:Px
    @get:Px
    var dividerThickness: Int = 1.dp

    @setparam:ColorInt
    @get:ColorInt
    var dividerColor: Int
        get() = color
        set(color) {
            this.color = color
            dividerDrawable = DrawableCompat.wrap(dividerDrawable)
            DrawableCompat.setTint(dividerDrawable, color)
        }

    var orientation = 0
        set(value) {
            require(value == HORIZONTAL || value == VERTICAL) {
                "Invalid orientation: $orientation. It should be either HORIZONTAL or VERTICAL"
            }
            field = value
        }

    var lastItemDecorated = true

    @field:ColorInt
    private var color: Int = Color.BLACK
    private val tempRect = Rect()
    private var dividerDrawable: Drawable

    init {
        val attrs = context.obtainStyledAttributes(null, R.styleable.DividerItem, defStyleAttr, 0)

        color = attrs.getColor(R.styleable.DividerItem_dividerItemColor, color)
        dividerThickness = attrs.toPx(R.styleable.DividerItem_dividerItemThickness, dividerThickness)

        if (dividerInsetStart == -1) {
            dividerInsetStart = horizontalMargin + attrs.toPx(R.styleable.DividerItem_dividerInsetStart, 0)
        }

        if (dividerInsetEnd == -1) {
            dividerInsetEnd = horizontalMargin + attrs.toPx(R.styleable.DividerItem_dividerInsetEnd, 0)
        }

        lastItemDecorated = attrs.getBoolean(R.styleable.DividerItem_showLastItemDecoration, true)

        attrs.recycle()

        dividerDrawable = ShapeDrawable()
        dividerColor = color
        this.orientation = orientation
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, 0, 0)

        val adapter = parent.adapter as? GroupAdapter ?: return
        val position = parent.getChildAdapterPosition(view).takeIf { it != RecyclerView.NO_POSITION } ?: return

        if (!itemPredicate.invoke(adapter.getGroupAtAdapterPosition(position))) {
            return
        }

        if (!lastItemDecorated && position == adapter.getItemCount() - 1) {
            return
        }

        if (orientation == VERTICAL) {
            outRect.bottom = dividerDrawable.intrinsicHeight + dividerThickness
        } else {
            outRect.right = dividerDrawable.intrinsicWidth + dividerThickness
        }
    }

    override fun onDraw(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.layoutManager == null) {
            return
        }

        if (orientation == VERTICAL) {
            drawForVerticalOrientation(canvas, parent)
        } else {
            drawForHorizontalOrientation(canvas, parent)
        }
    }

    private fun drawForVerticalOrientation(canvas: Canvas, parent: RecyclerView) {
        val adapter = parent.adapter as? GroupAdapter ?: return

        canvas.save()
        var left: Int
        var right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right, parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val isRtl = ViewCompat.getLayoutDirection(parent) == ViewCompat.LAYOUT_DIRECTION_RTL
        left += if (isRtl) dividerInsetEnd else dividerInsetStart
        right -= if (isRtl) dividerInsetStart else dividerInsetEnd
        val childCount = parent.childCount
        val dividerCount = if (lastItemDecorated) childCount else childCount - 1
        for (i in 0 until dividerCount) {
            val child = parent.getChildAt(i)

            val isAllowDraw = runCatching {
                itemPredicate.invoke(adapter.getGroupAtAdapterPosition(i))
            }.getOrNull() ?: true

            if (!isAllowDraw) continue

            parent.getDecoratedBoundsWithMargins(child, tempRect)
            // Take into consideration any translationY added to the view.
            val bottom = (if (isReverseDraw) tempRect.top else tempRect.bottom) + child.translationY.roundToInt()
            val top = bottom - dividerDrawable.intrinsicHeight - dividerThickness
            dividerDrawable.setBounds(left, top, right, bottom)
            dividerDrawable.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawForHorizontalOrientation(canvas: Canvas, parent: RecyclerView) {
        val adapter = parent.adapter as? GroupAdapter ?: return

        canvas.save()
        var top: Int
        var bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top, parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }
        top += dividerInsetStart
        bottom -= dividerInsetEnd
        val childCount = parent.childCount
        val dividerCount = if (lastItemDecorated) childCount else childCount - 1
        for (i in 0 until dividerCount) {
            val child = parent.getChildAt(i)

            if (!itemPredicate.invoke(adapter.getGroupAtAdapterPosition(i))) {
                continue
            }

            parent.getDecoratedBoundsWithMargins(child, tempRect)
            // Take into consideration any translationY added to the view.
            val right = (if (isReverseDraw) tempRect.left else tempRect.right) + child.translationX.roundToInt()
            val left = right - dividerDrawable.intrinsicWidth - dividerThickness
            dividerDrawable.setBounds(left, top, right, bottom)
            dividerDrawable.draw(canvas)
        }
        canvas.restore()
    }

    private fun TypedArray.toPx(@StyleableRes index: Int, defValue: Int): Int = getDimensionPixelSize(index, defValue)
}
