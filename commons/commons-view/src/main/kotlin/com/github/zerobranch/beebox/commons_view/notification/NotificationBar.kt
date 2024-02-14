package com.github.zerobranch.beebox.commons_view.notification

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import com.github.zerobranch.beebox.commons_view.R

class NotificationBar private constructor(
    private val parent: ViewGroup,
    private val inTitle: String?,
    private val inSubtitle: String?,
    private val inActionTitle: String?,
    @DrawableRes private val inLeftIcon: Int?,
    @ColorRes private val inLeftIconTint: Int?,
    private val inDuration: Long,
    private val inShowingAnimation: ShowingAnimation,
    private val inSwipeDirection: SwipeDirection,
    private val inWithActionLoader: Boolean,
    @StyleRes private val inDefStyleRes: Int,
    private val inOnVisibilityChangedListener: ((VisibilityState) -> Unit)?,
    private val inOnActionClick: (() -> Unit)?
) {
    private var notificationView: NotificationView? = null

    fun dismiss() {
        notificationView?.dismiss()
        notificationView = null
    }

    fun show() {
        reset()

        val notificationView = NotificationView(
            context = parent.context,
            defStyleAttr = if (inDefStyleRes == 0) R.attr.notificationViewStyle else 0,
            defStyleRes = inDefStyleRes
        ).apply {
            showingAnimation = inShowingAnimation
            swipeDirection = inSwipeDirection
            duration = inDuration
            onVisibilityChangedListener = inOnVisibilityChangedListener
            tag = NotificationView.TAG
            onActionClick = inOnActionClick
            withActionLoader = inWithActionLoader

            setTitle(inTitle)
            setSubtitle(inSubtitle)
            setActionTitle(inActionTitle)
            setLeftIcon(inLeftIcon)
            setLeftIconTint(inLeftIconTint)
        }

        notificationView.isInvisible = true
        parent.addView(notificationView)
        setLayoutParams(parent, notificationView)

        notificationView.targetView.post {
            notificationView.isVisible = true
            notificationView.showWithAnimation()
            notificationView.show()
        }

        this.notificationView = notificationView
    }

    private fun reset() {
        parent.findViewWithTag<NotificationView>(NotificationView.TAG)?.removeMe()
        notificationView = null
    }

    companion object {
        const val INFINITE_DURATION = NotificationView.INFINITE_DURATION
        const val SHORT_DURATION = NotificationView.SHORT_DURATION_MS
        const val LONG_DURATION = NotificationView.LONG_DURATION_MS

        fun make(
            view: View,
            @StringRes title: Int? = null,
            @StringRes subtitle: Int? = null,
            @StringRes actionTitle: Int? = null,
            @DrawableRes leftIcon: Int? = null,
            @ColorRes leftIconTint: Int? = null,
            duration: Long = SHORT_DURATION,
            showingAnimation: ShowingAnimation = ShowingAnimation.SCALE_FADE,
            swipeDirection: SwipeDirection = SwipeDirection.HORIZONTAL,
            withActionLoader: Boolean = false,
            @StyleRes defStyleRes: Int = 0,
            onVisibilityChangedListener: ((VisibilityState) -> Unit)? = null,
            onActionClick: (() -> Unit)? = null
        ): NotificationBar = make(
            view = view,
            title = title?.run { view.context.getString(title) },
            subtitle = subtitle?.run { view.context.getString(subtitle) },
            actionTitle = actionTitle?.run { view.context.getString(actionTitle) },
            leftIcon = leftIcon,
            leftIconTint = leftIconTint,
            duration = duration,
            showingAnimation = showingAnimation,
            swipeDirection = swipeDirection,
            withActionLoader = withActionLoader,
            defStyleRes = defStyleRes,
            onVisibilityChangedListener = onVisibilityChangedListener,
            onActionClick = onActionClick
        )

        fun make(
            view: View,
            title: String? = null,
            subtitle: String? = null,
            actionTitle: String? = null,
            @DrawableRes leftIcon: Int? = null,
            @ColorRes leftIconTint: Int? = null,
            duration: Long = SHORT_DURATION,
            showingAnimation: ShowingAnimation = ShowingAnimation.SCALE_FADE,
            swipeDirection: SwipeDirection = SwipeDirection.HORIZONTAL,
            withActionLoader: Boolean = false,
            @StyleRes defStyleRes: Int = 0,
            onVisibilityChangedListener: ((VisibilityState) -> Unit)? = null,
            onActionClick: (() -> Unit)? = null
        ): NotificationBar {
            val parent = findSuitableParent(view)
                ?: throw IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.")

            return NotificationBar(
                parent = parent,
                inTitle = title,
                inSubtitle = subtitle,
                inActionTitle = actionTitle,
                inLeftIcon = leftIcon,
                inLeftIconTint = leftIconTint,
                inDuration = duration,
                inShowingAnimation = showingAnimation,
                inSwipeDirection = swipeDirection,
                inWithActionLoader = withActionLoader,
                inDefStyleRes = defStyleRes,
                inOnVisibilityChangedListener = onVisibilityChangedListener,
                inOnActionClick = onActionClick
            )
        }

        private fun setLayoutParams(parent: ViewGroup, notificationView: NotificationView) {
            val lp: ViewGroup.LayoutParams

            when (parent) {
                is CoordinatorLayout -> {
                    lp = CoordinatorLayout.LayoutParams(
                        CoordinatorLayout.LayoutParams.MATCH_PARENT,
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.gravity = Gravity.BOTTOM
                }
                is FrameLayout -> {
                    lp = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.gravity = Gravity.BOTTOM
                }
                is ConstraintLayout -> {
                    lp = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                }
                else -> return
            }

            notificationView.layoutParams = lp
        }

        private fun NotificationView.showWithAnimation() {
            when (showingAnimation) {
                ShowingAnimation.SLIDE_LEFT -> translationX = -totalTargetWidth
                ShowingAnimation.SLIDE_RIGHT -> translationX = totalTargetWidth
                ShowingAnimation.SLIDE_BOTTOM -> translationY = totalTargetHeight
                ShowingAnimation.FADE -> alpha = 0f
                ShowingAnimation.SCALE_FADE -> {
                    alpha = 0f
                    scaleX = NotificationView.OUT_SCALE
                    scaleY = NotificationView.OUT_SCALE
                }
                else -> {}
            }

            this
                .animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationX(0f)
                .translationY(0f)
                .setDuration(NotificationView.IN_OUT_ANIMATION_DELAY)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        private fun findSuitableParent(view: View): ViewGroup? {
            var targetView: View? = view
            do {
                when (targetView) {
                    is CoordinatorLayout -> return targetView
                    is FrameLayout -> {
                        if (targetView !is FragmentContainerView) {
                            return targetView
                        }
                    }
                    is ConstraintLayout -> return targetView
                }

                if (targetView != null) {
                    val parent = targetView.parent
                    targetView = if (parent is View) parent else null
                }
            } while (targetView != null)

            return null
        }
    }
}
