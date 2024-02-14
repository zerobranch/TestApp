package com.github.zerobranch.beebox.commons_android.utils.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FractionRes
import androidx.annotation.PluralsRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.postDelayed
import androidx.core.view.updateMarginsRelative
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.CornerFamily
import java.lang.reflect.Field

fun View.getPlurals(
    @PluralsRes pluralsResId: Int,
    value: Int,
    formattedValue: Any? = null,
    @StringRes zeroValueStringResId: Int? = null
): String = context.getPlurals(pluralsResId, value, formattedValue, zeroValueStringResId)

fun View.getString(@StringRes stringResId: Int): String = context.getString(stringResId)

fun View.getString(@StringRes stringResId: Int, vararg formatArgs: Any): String =
    context.getString(stringResId, *formatArgs)

fun View.canScrollBottom() = canScrollVertically(1)

fun View.canScrollRight() = canScrollHorizontally(1)

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    if (requestFocus()) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.gone() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.marginLayoutParams(): ViewGroup.MarginLayoutParams? = layoutParams as? ViewGroup.MarginLayoutParams

fun View.colorInt(@ColorRes colorId: Int) = context.colorInt(colorId)

fun View.drawable(@DrawableRes drawableResId: Int): Drawable? =
    context.drawable(drawableResId)

fun View.backgroundTint(@ColorRes colorId: Int) {
    backgroundTintList = ContextCompat.getColorStateList(context, colorId)
}

fun View.foregroundTint(@ColorRes colorId: Int) {
    foregroundTintList = ContextCompat.getColorStateList(context, colorId)
}

fun ImageView.imageTint(@ColorRes colorId: Int) {
    imageTintList = ContextCompat.getColorStateList(context, colorId)
}

fun FloatingActionButton.supportImageTint(@ColorRes colorId: Int) {
    supportImageTintList = ContextCompat.getColorStateList(context, colorId)
}

fun View.getFraction(@FractionRes fractionResId: Int): Float = context.getFraction(fractionResId)

fun View.toPx(@DimenRes dimenReId: Int) = context.toPx(dimenReId)

fun View.fadeIn(
    duration: Long = 400,
    startDelay: Long = 0,
    interpolator: Interpolator? = null,
    withStartAction: Runnable? = null
) = animate()
    .setStartDelay(startDelay)
    .alpha(1f)
    .setDuration(duration)
    .withStartAction(withStartAction)
    .apply { interpolator?.let(::setInterpolator) }
    .start()

fun View.fadeOut(
    duration: Long = 400,
    startDelay: Long = 0,
    interpolator: Interpolator? = null,
    withEndAction: Runnable? = null
) = animate()
    .setStartDelay(startDelay)
    .alpha(0f)
    .setDuration(duration)
    .withEndAction(withEndAction)
    .apply { interpolator?.let(::setInterpolator) }
    .start()

fun View.expand(
    duration: Long = 200,
    startDelay: Long = 0,
    withEndAction: Runnable? = null
) = animate()
    .setStartDelay(startDelay)
    .scaleX(1f)
    .scaleY(1f)
    .setDuration(duration)
    .withEndAction(withEndAction)
    .start()

fun View.collapse(
    duration: Long = 200,
    startDelay: Long = 0,
    withEndAction: Runnable? = null
) = animate()
    .setStartDelay(startDelay)
    .scaleX(0f)
    .scaleY(0f)
    .setDuration(duration)
    .withEndAction(withEndAction)
    .start()

fun NestedScrollView.scrollToTop() = scrollTo(0, 0)

fun NestedScrollView.isViewVisible(view: View): Boolean {
    val scrollBounds = Rect()
    this.getDrawingRect(scrollBounds)
    val top = view.y
    val bottom = top + view.height
    return scrollBounds.top < top && scrollBounds.bottom > bottom
}

fun View.startAnimation(
    @AnimRes animRes: Int,
    config: (Animation.() -> Unit)? = null
) {
    startAnimation(
        AnimationUtils.loadAnimation(context, animRes).apply {
            config?.invoke(this)
        })
}

fun ViewConfiguration.isClick(xDiff: Int, yDiff: Int): Boolean =
    xDiff < scaledTouchSlop && yDiff < scaledTouchSlop

fun MaterialButton.setBottomRadius(@DimenRes radius: Int) {
    shapeAppearanceModel = shapeAppearanceModel
        .toBuilder()
        .setBottomRightCorner(CornerFamily.ROUNDED, toPx(radius).toFloat())
        .setBottomLeftCorner(CornerFamily.ROUNDED, toPx(radius).toFloat())
        .setTopLeftCorner(CornerFamily.ROUNDED, 0f)
        .setTopRightCorner(CornerFamily.ROUNDED, 0f)
        .build()
}

fun MaterialButton.setRadius(@DimenRes radius: Int) {
    shapeAppearanceModel = shapeAppearanceModel
        .toBuilder()
        .setAllCorners(CornerFamily.ROUNDED, toPx(radius).toFloat())
        .build()
}

fun BottomSheetBehavior<*>.addBottomSheetDialogCallback(
    onStateChanged: ((bottomSheet: View, newState: Int) -> Unit)? = null,
    onSlide: ((bottomSheet: View, slideOffset: Float) -> Unit)? = null,
) {
    addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            onStateChanged?.invoke(bottomSheet, newState)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            onSlide?.invoke(bottomSheet, slideOffset)
        }
    })
}

fun View.debounceClick(debounceTime: Long = 600L, action: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return

            action.invoke()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

inline fun <T : View> T.afterMeasured(crossinline action: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                action.invoke(this@afterMeasured)
            }
        }
    })
}

fun View.postIfAttached(action: () -> Unit) {
    post {
        if (!isAttachedToWindow) return@post
        action.invoke()
    }
}

fun RecyclerView.scrollToPositionByCenter(position: Int) {
    val helper = onFlingListener as? SnapHelper ?: return

    scrollToPosition(position)
    postIfAttached {
        val mLayoutManager = layoutManager ?: return@postIfAttached
        val view: View = mLayoutManager.findViewByPosition(position) ?: return@postIfAttached
        val snapDistance: IntArray = helper.calculateDistanceToFinalSnap(mLayoutManager, view) ?: return@postIfAttached
        if (snapDistance[0] != 0 || snapDistance[1] != 0) {
            scrollBy(snapDistance[0], snapDistance[1])
        }
    }
}

fun RecyclerView.attachHelper(helper: SnapHelper) {
    if (onFlingListener == null) {
        helper.attachToRecyclerView(this)
    }
}

fun TextView.setAttrTextAppearance(@AttrRes attr: Int) {
    val typedArray = context.obtainStyledAttributes(TypedValue().data, intArrayOf(attr))
    val resId = typedArray.getResourceId(0, 0)
    typedArray.recycle()
    TextViewCompat.setTextAppearance(this, resId)
}

fun View.getActivity(): Activity? = context.getActivity()

fun View.updateHorizontalPadding(@Px padding: Int) {
    updatePadding(left = padding, right = padding)
}

fun View.updateHorizontalMargin(@Px margin: Int, append: Boolean = false) {
    updateMargins(start = margin, end = margin, append = append)
}

fun View.updateMargins(
    @Px start: Int? = null,
    @Px top: Int? = null,
    @Px end: Int? = null,
    @Px bottom: Int? = null,
    append: Boolean = false
) {
    val lp = (layoutParams as? ViewGroup.MarginLayoutParams) ?: return

    lp.updateMarginsRelative(
        start = start?.run { start.takeUnless { append } ?: (start + lp.marginStart) } ?: lp.marginStart,
        top = top?.run { top.takeUnless { append } ?: (top + lp.topMargin) } ?: lp.topMargin,
        end = end?.run { end.takeUnless { append } ?: (end + lp.marginEnd) } ?: lp.marginEnd,
        bottom = bottom?.run { bottom.takeUnless { append } ?: (bottom + lp.bottomMargin) } ?: lp.bottomMargin
    )
}

fun View.setAccessibility(callback: (AccessibilityNodeInfoCompat) -> Unit) {
    ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            callback.invoke(info)
        }
    })
}

fun RecyclerView.setRecyclerViewAccessibility(callback: (AccessibilityNodeInfoCompat) -> Unit) {
    ViewCompat.setAccessibilityDelegate(this, object : RecyclerViewAccessibilityDelegate(this) {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            callback.invoke(info)
        }
    })
}

context(View)
fun AccessibilityNodeInfoCompat.setHorizontalTabInfo(position: Int) {
    setCollectionItemInfo(
        AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(
            0, /* rowIndex= */
            1, /* rowSpan= */
            position, /* columnIndex= */
            1, /* columnSpan= */
            false, /* heading= */
            this@View.isSelected /* selected= */
        )
    )

    if (this@View.isSelected) {
        isClickable = false
        removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
    }

    roleDescription = getString(com.google.android.material.R.string.item_view_role_description)
}

fun AccessibilityNodeInfoCompat.setHorizontalCollectionInfo(itemsSize: Int) {
    setCollectionInfo(
        AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(
            1, /* rowCount= */
            itemsSize, /* columnCount= */
            false, /* hierarchical= */
            AccessibilityNodeInfoCompat.CollectionInfoCompat.SELECTION_MODE_SINGLE /* selectionMode = */
        )
    )
}

@Throws(NoSuchFieldException::class)
fun MaterialToolbar.getSubtitleView(): TextView? =
    runCatching {
        val tb = this as Toolbar
        val field: Field = Toolbar::class.java.getDeclaredField("mSubtitleTextView")
        field.isAccessible = true
        field.get(tb) as TextView?
    }.onFailure { th ->
        throw th
    }.getOrNull()

@Throws(NoSuchFieldException::class)
fun MaterialToolbar.getTitleView(): TextView? =
    runCatching {
        val tb = this as Toolbar
        val field: Field = Toolbar::class.java.getDeclaredField("mTitleTextView")
        field.isAccessible = true
        field.get(tb) as TextView?
    }.onFailure { th ->
        throw th
    }.getOrNull()

fun View?.hideForAccessibility() {
    this ?: return
    ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO)
}

fun View.enableAccessibilityHeading() {
    ViewCompat.setAccessibilityHeading(this, true)
}

context(Fragment)
fun View.safePost(action: () -> Unit) = post {
    if (view == null) return@post
    else action.invoke()
}

context(Fragment)
fun View.safePostDelayed(
    delay: Long,
    action: () -> Unit
): Runnable = postDelayed(delay) {
    if (view == null) return@postDelayed
    else action.invoke()
}

fun View.softPost(action: () -> Unit) = post {
    if (isAttachedToWindow) {
        action.invoke()
    }
}

fun View.softPostDelayed(
    delay: Long,
    action: () -> Unit
): Runnable = postDelayed(delay) {
    if (isAttachedToWindow) {
        action.invoke()
    }
}

val View.screenshot: Bitmap
    get() {
        val btm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(btm)
        draw(c)
        return btm
    }

val View.absoluteLocation: Pair<Int, Int>
    get() {
        val locationInWindowArray = IntArray(2)
        getLocationInWindow(locationInWindowArray)
        return locationInWindowArray.first() to locationInWindowArray.last()
    }

@SuppressLint("ClickableViewAccessibility")
fun View.setOnTouchDown(action: () -> Unit) {
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            action.invoke()
        }
        return@setOnTouchListener false
    }
}