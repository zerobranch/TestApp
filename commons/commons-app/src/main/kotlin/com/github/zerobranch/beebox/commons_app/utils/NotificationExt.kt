package com.github.zerobranch.beebox.commons_app.utils

import android.view.ViewGroup
import androidx.annotation.StringRes
import com.github.zerobranch.beebox.commons_android.utils.ext.getString
import com.github.zerobranch.beebox.commons_app.R
import com.github.zerobranch.beebox.commons_view.notification.NotificationBar
import com.github.zerobranch.beebox.commons_view.notification.ShowingAnimation
import com.github.zerobranch.beebox.commons_view.notification.SwipeDirection

fun ViewGroup.showShortNotification(
    @StringRes title: Int? = null,
    @StringRes actionTitle: Int?,
    onActionClick: (() -> Unit)? = null
): Unit = NotificationBar.make(
    view = this,
    title = title,
    actionTitle = actionTitle,
    leftIcon = null,
    duration = NotificationBar.SHORT_DURATION,
    showingAnimation = ShowingAnimation.SLIDE_BOTTOM,
    swipeDirection = SwipeDirection.HORIZONTAL,
    onActionClick = onActionClick
)
    .show()

fun ViewGroup.showBaseError(onActionClick: (() -> Unit)? = null): Unit =
    showError(
        title = getString(R.string.common_error_default),
        actionTitle = getString(R.string.common_update),
        onActionClick = onActionClick
    )

fun ViewGroup.showNetworkError(subtitle: String? = null): Unit =
    showError(
        title = getString(R.string.common_error_no_internet),
        subtitle = subtitle
    )

fun ViewGroup.showError(
    title: String,
    subtitle: String? = null,
    actionTitle: String? = null,
    duration: Long = NotificationBar.LONG_DURATION,
    onActionClick: (() -> Unit)? = null
): Unit = NotificationBar
    .make(
        view = this,
        title = title,
        subtitle = subtitle,
        actionTitle = actionTitle?.takeIf { onActionClick != null },
        leftIcon = R.drawable.common_ic_attention,
        duration = duration,
        showingAnimation = ShowingAnimation.SLIDE_BOTTOM,
        swipeDirection = SwipeDirection.HORIZONTAL,
        onActionClick = onActionClick
    )
    .show()

fun ViewGroup.showError(
    @StringRes title: Int,
    subtitle: String? = null,
    actionTitle: String? = null,
    onActionClick: (() -> Unit)? = null
): Unit = NotificationBar
    .make(
        view = this,
        title = getString(title),
        subtitle = subtitle,
        actionTitle = actionTitle.takeIf { onActionClick != null && actionTitle != null },
        leftIcon = R.drawable.common_ic_attention,
        duration = NotificationBar.LONG_DURATION,
        showingAnimation = ShowingAnimation.SLIDE_BOTTOM,
        swipeDirection = SwipeDirection.HORIZONTAL,
        onActionClick = onActionClick
    )
    .show()

fun ViewGroup.showConnectionRestoredNotification(
    onActionClick: (() -> Unit)? = null
): NotificationBar =
    NotificationBar
        .make(
            view = this,
            title = getString(R.string.common_title_connection_restored),
            actionTitle = getString(R.string.common_update),
            leftIcon = R.drawable.common_ic_success,
            leftIconTint = R.color.common_aquamarine,
            duration = NotificationBar.LONG_DURATION,
            showingAnimation = ShowingAnimation.SLIDE_BOTTOM,
            swipeDirection = SwipeDirection.HORIZONTAL,
            withActionLoader = true,
            onActionClick = onActionClick
        )
        .apply { show() }