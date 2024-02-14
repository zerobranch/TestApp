package com.github.zerobranch.beebox.commons_view.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.core.widget.TextViewCompat
import androidx.customview.widget.ViewDragHelper
import com.github.zerobranch.beebox.commons_view.R
import com.github.zerobranch.beebox.commons_view.databinding.NotificationViewLayoutBinding
import com.github.zerobranch.beebox.commons_view.colorInt
import com.github.zerobranch.beebox.commons_view.dp
import com.github.zerobranch.beebox.commons_view.setTextOrHide
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NotificationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_AUTO_CLOSE_VELOCITY = 5000.0F
        private const val DEFAULT_DRAG_SENSITIVITY = 1.0F

        internal const val TAG = "NotificationView"
        internal const val IN_OUT_ANIMATION_DELAY = 250L
        internal const val SHORT_DURATION_MS = 3_750L
        internal const val LONG_DURATION_MS = 5000L
        internal const val INFINITE_DURATION = -1L
        internal const val OUT_SCALE = 0.9F
    }

    internal var swipeDirection: SwipeDirection = SwipeDirection.HORIZONTAL
    internal var onVisibilityChangedListener: ((VisibilityState) -> Unit)? = null
    internal var onActionClick: (() -> Unit)? = null
    internal var duration: Long = SHORT_DURATION_MS
    internal var showingAnimation: ShowingAnimation = ShowingAnimation.SCALE_FADE
    internal var withActionLoader = false

    private var dragSensitivity: Float = DEFAULT_DRAG_SENSITIVITY
    private var autoCloseVelocity: Float = DEFAULT_AUTO_CLOSE_VELOCITY

    private val dragHelper: ViewDragHelper by lazy(LazyThreadSafetyMode.NONE) {
        ViewDragHelper.create(this as ViewGroup, dragSensitivity, DragHelperCallback())
    }

    private val binding =
        NotificationViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private var totalWidth = 0
    private var draggingViewLeft = 0
    private var draggingState = 0

    private val startX
        get() = targetView.marginStart

    internal val targetView
        get() = binding.root

    internal val totalTargetWidth: Float
        get() = targetView.width.toFloat() + 4.dp

    internal val totalTargetHeight: Float
        get() = targetView.height.toFloat() + 12.dp

    init {
        if (id == -1) {
            id = View.generateViewId()
        }

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.NotificationView,
            defStyleAttr,
            defStyleRes
        )
        val actionRippleColor =
            typedArray.getColor(R.styleable.NotificationView_actionRippleColor, Color.WHITE)
        val backgroundTint =
            typedArray.getColor(R.styleable.NotificationView_backgroundTint, Color.BLACK)
        val leftIconTint = typedArray.getColor(R.styleable.NotificationView_leftIconTint, Color.RED)
        val titleTextColor =
            typedArray.getColor(R.styleable.NotificationView_titleTextColor, Color.WHITE)
        val subtitleTextColor =
            typedArray.getColor(R.styleable.NotificationView_subtitleTextColor, Color.WHITE)
        val actionTextColor =
            typedArray.getColor(R.styleable.NotificationView_actionTextColor, Color.BLUE)

        val titleTextAppearance =
            typedArray.getResourceId(R.styleable.NotificationView_titleTextAppearance, -1)
        val subtitleTextAppearance =
            typedArray.getResourceId(R.styleable.NotificationView_subtitleTextAppearance, -1)
        val actionTextAppearance =
            typedArray.getResourceId(R.styleable.NotificationView_actionTextAppearance, -1)

        typedArray.recycle()

        with(binding) {
            TextViewCompat.setTextAppearance(notificationViewSubtitle, subtitleTextAppearance)
            TextViewCompat.setTextAppearance(notificationViewTitle, titleTextAppearance)
            TextViewCompat.setTextAppearance(notificationViewAction, actionTextAppearance)

            root.backgroundTintList = ColorStateList.valueOf(backgroundTint)
            notificationViewTitle.setTextColor(titleTextColor)
            notificationViewSubtitle.setTextColor(subtitleTextColor)
            notificationViewAction.setTextColor(actionTextColor)
            notificationViewAction.rippleColor = ColorStateList.valueOf(actionRippleColor)
            notificationViewLeftIcon.setColorFilter(leftIconTint, PorterDuff.Mode.SRC_IN)

            notificationViewAction.setOnClickListener {
                if (withActionLoader) {
                    duration = INFINITE_DURATION
                    notificationViewAction.isInvisible = true
                    notificationViewLoader.show()
                } else {
                    dismiss()
                }
                onActionClick?.invoke()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        totalWidth = w
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean =
        dragHelper.shouldInterceptTouchEvent(event)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (draggingState.isMoving()) {
            dragHelper.processTouchEvent(event)
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        superState ?: return null

        return SavedState(superState).apply {
            mSwipeDirection = swipeDirection
            mDuration = duration
            mShowingAnimation = showingAnimation
            mDragSensitivity = dragSensitivity
            mAutoCloseVelocity = autoCloseVelocity
            mTotalWidth = totalWidth
            mDraggingViewLeft = draggingViewLeft
            mDraggingState = draggingState
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        swipeDirection = state.mSwipeDirection
        duration = state.mDuration
        showingAnimation = state.mShowingAnimation
        dragSensitivity = state.mDragSensitivity
        autoCloseVelocity = state.mAutoCloseVelocity
        totalWidth = state.mTotalWidth
        draggingViewLeft = state.mDraggingViewLeft
        draggingState = state.mDraggingState
    }

    internal fun show() {
        scheduleClose()
    }

    internal fun dismiss() {
        val targetAnimator: ViewPropertyAnimator = targetView.animate()
        when (showingAnimation) {
            ShowingAnimation.NONE -> {
                removeMe()
                return
            }

            ShowingAnimation.SLIDE_LEFT -> targetAnimator.translationX(-totalTargetWidth)
            ShowingAnimation.SLIDE_RIGHT -> targetAnimator.translationX(totalTargetWidth)
            ShowingAnimation.SLIDE_BOTTOM -> targetAnimator.translationY(totalTargetHeight)
            ShowingAnimation.FADE -> targetAnimator.alpha(0f)
            ShowingAnimation.SCALE_FADE -> {
                targetAnimator
                    .alpha(0f)
                    .scaleX(OUT_SCALE)
                    .scaleY(OUT_SCALE)
            }
        }

        clearAnimation()
        animate().cancel()

        targetAnimator
            .setDuration(IN_OUT_ANIMATION_DELAY)
            .withEndAction { removeMe() }
            .setInterpolator(AccelerateInterpolator())
            .start()
    }

    internal fun setTitle(title: String?) {
        binding.notificationViewTitle.setTextOrHide(title)
    }

    internal fun setSubtitle(subtitle: String?) {
        binding.notificationViewSubtitle.setTextOrHide(subtitle)
    }

    internal fun setActionTitle(actionTitle: String?) {
        binding.notificationViewAction.setTextOrHide(actionTitle)
    }

    internal fun setLeftIcon(@DrawableRes leftIcon: Int?) {
        if (leftIcon == null) {
            binding.notificationViewLeftIcon.isVisible = false
        } else {
            binding.notificationViewLeftIcon.isVisible = true
            binding.notificationViewLeftIcon.setImageResource(leftIcon)
        }
    }

    internal fun setLeftIconTint(@ColorRes tint: Int?) {
        tint?.run {
            binding.notificationViewLeftIcon.setColorFilter(
                context.colorInt(tint),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    internal fun removeMe() {
        (parent as? ViewGroup)?.removeView(this)
    }

    private fun Int.isIdle() = this == ViewDragHelper.STATE_IDLE

    private fun Int.isDragging() = this == ViewDragHelper.STATE_DRAGGING

    private fun Int.isSettling() = this == ViewDragHelper.STATE_SETTLING

    private fun Int.isMoving() = isDragging() || isSettling()

    private fun scheduleClose() {
        if (duration == INFINITE_DURATION) {
            return
        }

        postDelayed(
            {
                if (duration != INFINITE_DURATION) {
                    dismiss()
                }
            },
            duration
        )
    }

    private fun onVisibilityChanged(visibilityState: VisibilityState) {
        if (visibilityState.isClosed()) {
            removeMe()
        }

        onVisibilityChangedListener?.invoke(visibilityState)
    }

    private fun clampLeftViewPosition(left: Int): Int =
        if (left < startX) max(left, -totalWidth) else startX

    private fun clampRightViewPosition(left: Int): Int =
        if (left > startX) min(left, totalWidth) else startX

    private fun clampHorizontalViewPosition(left: Int): Int =
        when {
            left < startX -> max(left, -totalWidth)
            left > startX -> min(left, totalWidth)
            else -> startX
        }

    private inner class DragHelperCallback : ViewDragHelper.Callback() {
        override fun onViewDragStateChanged(state: Int) {
            if (state == draggingState) return

            if (draggingState.isMoving() && state.isIdle()) {
                when {
                    draggingViewLeft == startX -> onVisibilityChanged(VisibilityState.OPENED)
                    draggingViewLeft >= totalWidth -> onVisibilityChanged(VisibilityState.SLIDED_RIGHT)
                    draggingViewLeft < totalWidth -> onVisibilityChanged(VisibilityState.SLIDED_LEFT)
                }
            }

            draggingState = state
        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean = true

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            draggingViewLeft = left
        }

        override fun getViewHorizontalDragRange(child: View): Int = totalWidth

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int =
            when (swipeDirection) {
                SwipeDirection.NONE -> startX
                SwipeDirection.LEFT -> clampLeftViewPosition(left)
                SwipeDirection.RIGHT -> clampRightViewPosition(left)
                SwipeDirection.HORIZONTAL -> clampHorizontalViewPosition(left)
            }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (draggingViewLeft == startX || abs(draggingViewLeft) == totalWidth) {
                return
            }

            val settleX = when {
                xvel > autoCloseVelocity && draggingViewLeft > 0 -> totalWidth
                xvel > autoCloseVelocity && draggingViewLeft < 0 -> 0
                xvel < -autoCloseVelocity && draggingViewLeft < 0 -> -totalWidth
                xvel < -autoCloseVelocity && draggingViewLeft > 0 -> 0
                draggingViewLeft > totalWidth / 2 -> totalWidth
                draggingViewLeft in 0..totalWidth / 2 -> 0
                draggingViewLeft in -(totalWidth / 2)..0 -> 0
                draggingViewLeft < -(totalWidth / 2) -> -totalWidth
                else -> startX
            }

            if (dragHelper.settleCapturedViewAt(settleX + startX, targetView.top)) {
                ViewCompat.postInvalidateOnAnimation(this@NotificationView)
            }
        }
    }

    private class SavedState : BaseSavedState {
        var mSwipeDirection: SwipeDirection = SwipeDirection.NONE
        var mDuration: Long = 0L
        var mShowingAnimation: ShowingAnimation = ShowingAnimation.NONE
        var mDragSensitivity: Float = 0F
        var mAutoCloseVelocity: Float = 0F
        var mTotalWidth = 0
        var mDraggingViewLeft = 0
        var mDraggingState = 0

        constructor(superState: Parcelable) : super(superState)

        private constructor(inParcel: Parcel) : super(inParcel) {
            mSwipeDirection = SwipeDirection.getByValue(inParcel.readInt())
            mDuration = inParcel.readLong()
            mShowingAnimation = ShowingAnimation.getByValue(inParcel.readInt())
            mDragSensitivity = inParcel.readFloat()
            mAutoCloseVelocity = inParcel.readFloat()
            mTotalWidth = inParcel.readInt()
            mDraggingViewLeft = inParcel.readInt()
            mDraggingState = inParcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(mSwipeDirection.value)
            out.writeLong(mDuration)
            out.writeInt(mShowingAnimation.value)
            out.writeFloat(mDragSensitivity)
            out.writeFloat(mAutoCloseVelocity)
            out.writeInt(mTotalWidth)
            out.writeInt(mDraggingViewLeft)
            out.writeInt(mDraggingState)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(inParcel: Parcel): SavedState = SavedState(inParcel)

                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }
}
