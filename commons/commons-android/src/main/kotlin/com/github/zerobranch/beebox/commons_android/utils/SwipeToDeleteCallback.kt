package com.github.zerobranch.beebox.commons_android.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.zerobranch.beebox.commons_android.R
import com.github.zerobranch.beebox.commons_android.utils.ext.dp
import com.github.zerobranch.beebox.commons_android.utils.ext.getDrawableCompat
import com.github.zerobranch.beebox.commons_android.utils.ext.withTint
import com.xwray.groupie.Group
import com.xwray.groupie.GroupieViewHolder

class SwipeToDeleteCallback(
    context: Context,
    @DrawableRes private val endIcon: Int,
    @DrawableRes private val startIcon: Int,
    @ColorInt private val backgroundColor: Int,
    @Px private val backgroundRadius: Float = 0F,
    directions: Int = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
    private val swipePredicate: (Group) -> Boolean = { true },
    private val onSwiped: (Group, direction: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE,
    directions
) {
    private val finalIconHeight = 40.dp
    private val finalIconSpacing = 16.dp

    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    private val iconBackground = context
        .getDrawableCompat(R.drawable.utils_android_swipe_to_delete_bg_icon)
        .withTint(
            context,
            R.color.utils_android_swipe_to_delete_icon_bg_tint
        )

    private val endIconTinted = context
        .getDrawableCompat(endIcon)
        .withTint(context, R.color.utils_android_swipe_to_delete_icon_tint)

    private val startIconTinted = context
        .getDrawableCompat(startIcon)
        .withTint(context, R.color.utils_android_swipe_to_delete_icon_tint)

    private val finalEndIcon = LayerDrawable(arrayOf(iconBackground, endIconTinted)).apply {
        val iconBackgroundIndex = 0
        val iconIndex = 1

        val iconHeight = endIconTinted.intrinsicHeight
        val iconWidth = endIconTinted.intrinsicWidth

        setLayerSize(iconBackgroundIndex, finalIconHeight, finalIconHeight)
        setLayerSize(iconIndex, iconWidth, iconHeight)

        val innerIconTop = (intrinsicHeight - iconHeight) / 2
        val innerIconBottom = innerIconTop + iconHeight
        val innerIconLeft = (intrinsicWidth - iconWidth) / 2
        val innerIconRight = innerIconLeft + iconWidth

        setLayerInset(iconBackgroundIndex, 0, 0, 0, 0)
        setLayerInset(iconIndex, innerIconLeft, innerIconTop, innerIconRight, innerIconBottom)
    }

    private val finalStartIcon = LayerDrawable(arrayOf(iconBackground, startIconTinted)).apply {
        val iconBackgroundIndex = 0
        val iconIndex = 1

        val iconHeight = startIconTinted.intrinsicHeight
        val iconWidth = startIconTinted.intrinsicWidth

        setLayerSize(iconBackgroundIndex, finalIconHeight, finalIconHeight)
        setLayerSize(iconIndex, iconWidth, iconHeight)

        val innerIconTop = (intrinsicHeight - iconHeight) / 2
        val innerIconBottom = innerIconTop + iconHeight
        val innerIconLeft = (intrinsicWidth - iconWidth) / 2
        val innerIconRight = innerIconLeft + iconWidth

        setLayerInset(iconBackgroundIndex, 0, 0, 0, 0)
        setLayerInset(iconIndex, innerIconLeft, innerIconTop, innerIconRight, innerIconBottom)
    }

    private val background = GradientDrawable().apply {
        setColor(backgroundColor)
        shape = GradientDrawable.RECTANGLE
        cornerRadii = floatArrayOf(
            0f,
            0f,
            backgroundRadius,
            backgroundRadius,
            backgroundRadius,
            backgroundRadius,
            0f,
            0f
        )
    }

    private val finalEndIconHeight = finalEndIcon.intrinsicHeight
    private val finalEndIconWidth = finalEndIcon.intrinsicWidth

    private val endIconHeight = endIconTinted.intrinsicHeight
    private val endIconWidth = endIconTinted.intrinsicWidth

    private val finalStartIconHeight = finalStartIcon.intrinsicHeight
    private val finalStartIconWidth = finalStartIcon.intrinsicWidth

    private val startIconHeight = startIconTinted.intrinsicHeight

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = super.getMovementFlags(recyclerView, viewHolder)
        .takeIf { swipePredicate.invoke((viewHolder as GroupieViewHolder).item) }
        ?: 0

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwiped.invoke((viewHolder as GroupieViewHolder).item, direction)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = .3f

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView, dX)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false)
            return
        }

        if (dX < 0) {
            drawBackgroundForEndIcon(c, itemView, dX)
            drawEndIcon(c, itemView)
        } else if (dX > 0) {
            drawBackgroundForStartIcon(c, itemView, dX)
            drawStartIcon(c, itemView)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }



    private fun drawBackgroundForEndIcon(c: Canvas, itemView: View, dX: Float) {
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)
    }

    private fun drawBackgroundForStartIcon(c: Canvas, itemView: View, dX: Float) {
        background.setBounds(
            itemView.left,
            itemView.top,
            itemView.left + dX.toInt(),
            itemView.bottom
        )
        background.draw(c)
    }

    private fun drawEndIcon(c: Canvas, itemView: View) {
        val itemHeight = itemView.bottom - itemView.top
        val finalIconTop = itemView.top + (itemHeight - finalEndIconHeight + endIconHeight) / 2
        val finalIconBottom = finalIconTop + finalEndIconHeight
        val finalIconRight = itemView.right - finalIconSpacing + endIconWidth
        val finalIconLeft = finalIconRight - finalEndIconWidth

        finalEndIcon.setBounds(finalIconLeft, finalIconTop, finalIconRight, finalIconBottom)
        finalEndIcon.draw(c)
    }

    private fun drawStartIcon(c: Canvas, itemView: View) {
        val itemHeight = itemView.bottom - itemView.top
        val finalIconTop = itemView.top + (itemHeight - finalStartIconHeight + startIconHeight) / 2
        val finalIconBottom = finalIconTop + finalStartIconHeight
        val finalIconLeft = itemView.left + finalIconSpacing
        val finalIconRight = finalIconLeft + finalStartIconWidth

        finalStartIcon.setBounds(finalIconLeft, finalIconTop, finalIconRight, finalIconBottom)
        finalStartIcon.draw(c)
    }

    private fun clearCanvas(c: Canvas, itemView: View, dX: Float) {
        c.drawRect(
            itemView.right + dX,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat(),
            clearPaint
        )
    }
}